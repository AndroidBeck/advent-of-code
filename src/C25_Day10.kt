import java.io.File
import kotlin.system.exitProcess

data class Machine(
    val lightTargetMask: Int,
    val buttonMasks: IntArray,
    val joltageTargets: IntArray
)

fun main() {
    val file = File("src/C25_Day10.txt")
    if (!file.exists()) {
        System.err.println("Input file src/C25_Day10.txt not found")
        exitProcess(1)
    }

    val machines = file.readLines().filter { it.isNotBlank() }.map { parseMachine(it) }

    var totalPart1 = 0L
    var totalPart2 = 0L

    println("=== Per-machine debug output ===")

    for ((idx, m) in machines.withIndex()) {
        val lineNo = idx + 1

        // Part 1
//        val p1 = minPressesLights(m)
//        if (p1 == Int.MAX_VALUE) {
//            println("Machine $lineNo: Part1 = NO SOLUTION")
//        } else {
//            println("Machine $lineNo: Part1 = $p1 presses")
//            totalPart1 += p1
//        }

        // Part 2
        val p2 = minPressesJolts(m)
        if (p2 == Int.MAX_VALUE) {
            println("Machine $lineNo: Part2 = NO SOLUTION")
        } else {
            println("Machine $lineNo: Part2 = $p2 presses")
            totalPart2 += p2
        }

        println("----")
    }

    println("=== Totals ===")
    println("Part 1 total presses = $totalPart1")
    println("Part 2 total presses = $totalPart2")
}


// -------- Parsing --------

/*
 Example line:
 [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
*/
fun parseMachine(line: String): Machine {
    // Indicator pattern [ ... ]
    val patternStart = line.indexOf('[')
    val patternEnd = line.indexOf(']', patternStart + 1)
    if (patternStart == -1 || patternEnd == -1) {
        throw IllegalArgumentException("Invalid line (no pattern []): $line")
    }
    val pattern = line.substring(patternStart + 1, patternEnd).trim()

    // Light target mask: bit i = 1 if pattern[i] == '#'
    var targetMask = 0
    for (i in pattern.indices) {
        if (pattern[i] == '#') {
            targetMask = targetMask or (1 shl i)
        }
    }

    // Joltage targets { ... }
    val curlyStart = line.indexOf('{', patternEnd + 1)
    val curlyEnd = if (curlyStart != -1) line.indexOf('}', curlyStart + 1) else -1
    val jolts: IntArray =
        if (curlyStart == -1 || curlyEnd == -1) {
            IntArray(0)
        } else {
            val inside = line.substring(curlyStart + 1, curlyEnd).trim()
            if (inside.isEmpty()) {
                IntArray(0)
            } else {
                inside.split(',')
                    .map { it.trim().toInt() }
                    .toIntArray()
            }
        }

    // Buttons: only look between patternEnd and curlyStart (if any)
    val buttonsSegment =
        if (curlyStart == -1) line.substring(patternEnd + 1)
        else line.substring(patternEnd + 1, curlyStart)

    val buttonRegex = Regex("""\(([^)]*)\)""")
    val buttonMasksList = mutableListOf<Int>()

    for (match in buttonRegex.findAll(buttonsSegment)) {
        val inside = match.groupValues[1].trim()
        if (inside.isEmpty()) continue
        var mask = 0
        val parts = inside.split(',')
        for (p in parts) {
            val token = p.trim()
            if (token.isEmpty()) continue
            val idx = token.toInt()
            if (idx < 0) continue
            mask = mask or (1 shl idx)
        }
        if (mask != 0) {
            buttonMasksList.add(mask)
        }
    }

    val buttonMasks = buttonMasksList.toIntArray()

    return Machine(
        lightTargetMask = targetMask,
        buttonMasks = buttonMasks,
        joltageTargets = jolts
    )
}

// -------- Part 1: lights with XOR (meet-in-the-middle) --------

fun minPressesLights(machine: Machine): Int {
    val target = machine.lightTargetMask
    val buttons = machine.buttonMasks
    val n = buttons.size

    // If target is all off, no presses needed
    if (target == 0) return 0
    if (n == 0) return Int.MAX_VALUE

    val h1 = n / 2
    val h2 = n - h1

    // First half subsets
    val size1 = 1 shl h1
    val toggles1 = IntArray(size1)
    val weight1 = IntArray(size1)

    for (mask in 1 until size1) {
        val lsb = mask and -mask
        val bit = Integer.numberOfTrailingZeros(lsb)
        val prev = mask xor lsb
        toggles1[mask] = toggles1[prev] xor buttons[bit]
        weight1[mask] = weight1[prev] + 1
    }

    val bestForToggle = HashMap<Int, Int>(size1)
    for (mask in 0 until size1) {
        val t = toggles1[mask]
        val w = weight1[mask]
        val prev = bestForToggle[t]
        if (prev == null || w < prev) {
            bestForToggle[t] = w
        }
    }

    // Second half subsets
    val size2 = 1 shl h2
    val toggles2 = IntArray(size2)
    val weight2 = IntArray(size2)

    for (mask in 1 until size2) {
        val lsb = mask and -mask
        val bit = Integer.numberOfTrailingZeros(lsb) + h1
        val prev = mask xor lsb
        toggles2[mask] = toggles2[prev] xor buttons[bit]
        weight2[mask] = weight2[prev] + 1
    }

    var best = Int.MAX_VALUE

    // Combine halves: t1 XOR t2 == target
    for (mask in 0 until size2) {
        val t2 = toggles2[mask]
        val w2 = weight2[mask]
        val neededT1 = target xor t2
        val w1 = bestForToggle[neededT1] ?: continue
        val total = w1 + w2
        if (total < best) best = total
    }

    return best
}

// -------- Part 2: joltage counters (branch-and-bound ILP) --------

fun minPressesJolts(machine: Machine): Int {
    val target = machine.joltageTargets
    val m = target.size
    if (m == 0) return 0

    val rawMasks = machine.buttonMasks
    val tmpBtnCounters = mutableListOf<IntArray>()

    // For each button, determine which counters it affects (indices within [0, m))
    for (mask in rawMasks) {
        val idxs = mutableListOf<Int>()
        for (i in 0 until m) {
            if (mask and (1 shl i) != 0) {
                idxs.add(i)
            }
        }
        if (idxs.isNotEmpty()) {
            tmpBtnCounters.add(idxs.toIntArray())
        }
    }

    var n = tmpBtnCounters.size
    if (n == 0) {
        // Only possible if all targets are zero
        return if (target.all { it == 0 }) 0 else Int.MAX_VALUE
    }

    // Compute U_j = max times we could possibly press button j
    // based on each counter it touches: x_j <= min_{i in J_j} target[i].
    val Uraw = IntArray(n)
    run {
        for (j in 0 until n) {
            val counters = tmpBtnCounters[j]
            var u = Int.MAX_VALUE
            for (i in counters) {
                val t = target[i]
                if (t < u) u = t
            }
            if (u == Int.MAX_VALUE) u = 0
            Uraw[j] = u
        }
    }

    // Remove buttons with U_j == 0 (they cannot help reach positive targets).
    val filteredCounters = mutableListOf<IntArray>()
    val filteredU = mutableListOf<Int>()
    for (j in 0 until n) {
        if (Uraw[j] > 0) {
            filteredCounters.add(tmpBtnCounters[j])
            filteredU.add(Uraw[j])
        }
    }

    n = filteredCounters.size
    if (n == 0) {
        return if (target.all { it == 0 }) 0 else Int.MAX_VALUE
    }

    // Rebuild arrays with only useful buttons.
    var btnCounters = Array(n) { IntArray(0) }
    val maxPress = IntArray(n)
    for (j in 0 until n) {
        btnCounters[j] = filteredCounters[j]
        maxPress[j] = filteredU[j]
    }

    // Check every counter is touched by at least one button; otherwise impossible if target[i] > 0.
    val touchedBy = IntArray(m)
    for (j in 0 until n) {
        for (idx in btnCounters[j]) {
            touchedBy[idx]++
        }
    }
    for (i in 0 until m) {
        if (target[i] > 0 && touchedBy[i] == 0) {
            return Int.MAX_VALUE
        }
    }

    // Order buttons: primarily by how many counters they touch, breaking ties by sum of target values.
    val order = (0 until n).sortedWith(
        compareByDescending<Int> { btnCounters[it].size }
            .thenByDescending { j -> btnCounters[j].sumOf { idx -> target[idx] } }
    ).toIntArray()

    val orderedBtnCounters = Array(n) { IntArray(0) }
    val orderedMaxPress = IntArray(n)
    for (k in 0 until n) {
        val j = order[k]
        orderedBtnCounters[k] = btnCounters[j]
        orderedMaxPress[k] = maxPress[j]
    }
    btnCounters = orderedBtnCounters
    for (k in 0 until n) {
        maxPress[k] = orderedMaxPress[k]
    }

    val deg = IntArray(n) { btnCounters[it].size }
    val Btot = target.sum()

    // remainMax[k][i] = maximum additional increments we can give to counter i using buttons k..n-1
    val remainMax = Array(n + 1) { IntArray(m) }
    for (k in n - 1 downTo 0) {
        val prev = remainMax[k + 1]
        val cur = remainMax[k]
        // start from prev
        for (i in 0 until m) {
            cur[i] = prev[i]
        }
        val u = maxPress[k]
        for (idx in btnCounters[k]) {
            cur[idx] += u
        }
    }

    // remainD[k] = max extra total "increments" (sum of all counters) from buttons k..n-1
    // dMaxRem[k] = largest degree among buttons k..n-1
    val remainD = IntArray(n + 1)
    val dMaxRem = IntArray(n + 1)
    for (k in n - 1 downTo 0) {
        remainD[k] = remainD[k + 1] + deg[k] * maxPress[k]
        dMaxRem[k] = maxOf(dMaxRem[k + 1], deg[k])
    }

    // Quick global feasibility: each counter must be reachable in total.
    for (i in 0 until m) {
        if (remainMax[0][i] < target[i]) {
            return Int.MAX_VALUE
        }
    }
    if (remainD[0] < Btot) {
        return Int.MAX_VALUE
    }

    val residual = target.clone()
    var best = Int.MAX_VALUE

    fun dfs(k: Int, currentSum: Int, usedIncrements: Int) {
        if (currentSum >= best) return

        val B_res = Btot - usedIncrements

        // Compute max residual and basic validity
        var maxRes = 0
        for (i in 0 until m) {
            val r = residual[i]
            if (r < 0) return
            if (r > maxRes) maxRes = r
        }

        // All satisfied
        if (maxRes == 0) {
            if (currentSum < best) best = currentSum
            return
        }

        // No more buttons to use
        if (k == n) return

        // Lower bound on additional presses using residual and remaining degrees
        val dMax = dMaxRem[k]
        if (dMax == 0) return
        val lbAddFromRes = maxRes
        val lbAddFromTotal = (B_res + dMax - 1) / dMax
        val lbAdd = maxOf(lbAddFromRes, lbAddFromTotal)
        if (currentSum + lbAdd >= best) return

        // Capacity checks
        val remCap = remainMax[k]
        for (i in 0 until m) {
            if (residual[i] > remCap[i]) return
        }
        if (remainD[k] < B_res) return

        val affected = btnCounters[k]
        val U_k = maxPress[k]

        // Determine bounds [lb, ub] for x_k from per-counter constraints
        var lb = 0
        var ub = U_k
        val remNextCap = remainMax[k + 1]
        for (idx in affected) {
            val r = residual[idx]
            if (r < ub) ub = r
            val needFromThis = r - remNextCap[idx]
            if (needFromThis > lb) lb = needFromThis
        }
        if (lb < 0) lb = 0

        // Global equation bounds from total increments:
        // d_k * x_k + increments_from_future = B_res
        val d_k = deg[k]
        var lowEq = B_res - remainD[k + 1]
        if (lowEq < 0) lowEq = 0
        val lbEq = (lowEq + d_k - 1) / d_k
        val ubEq = B_res / d_k

        if (lbEq > lb) lb = lbEq
        if (ubEq < ub) ub = ubEq
        if (lb > ub) return

        // Try values for x_k from lb to ub (small first).
        val saved = IntArray(m)
        for (x in lb..ub) {
            val newSum = currentSum + x
            if (newSum >= best) break

            // Save residual
            for (i in 0 until m) {
                saved[i] = residual[i]
            }

            if (x != 0) {
                for (idx in affected) {
                    residual[idx] -= x
                }
            }

            dfs(k + 1, newSum, usedIncrements + d_k * x)

            // Restore
            for (i in 0 until m) {
                residual[i] = saved[i]
            }
        }
    }

    dfs(0, 0, 0)
    return best
}
