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

    // Part 1
    var totalPart1 = 0L
    for ((idx, m) in machines.withIndex()) {
        val presses = minPressesLights(m)
        if (presses == Int.MAX_VALUE) {
            System.err.println("No light configuration solution for machine on line ${idx + 1}")
            exitProcess(1)
        }
        totalPart1 += presses
    }

    // Part 2
    var totalPart2 = 0L
    for ((idx, m) in machines.withIndex()) {
        val presses = minPressesJolts(m)
        if (presses == Int.MAX_VALUE) {
            System.err.println("No joltage configuration solution for machine on line ${idx + 1}")
            exitProcess(1)
        }
        totalPart2 += presses
    }

    println(totalPart1) // Part 1 answer
    println(totalPart2) // Part 2 answer
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

// -------- Part 2: joltage counters with branch-and-bound ILP --------

fun minPressesJolts(machine: Machine): Int {
    val target = machine.joltageTargets
    val m = target.size
    if (m == 0) return 0

    val rawMasks = machine.buttonMasks
    val btnCountersList = mutableListOf<IntArray>()

    // For each button, determine which counters it affects.
    for (mask in rawMasks) {
        val idxs = mutableListOf<Int>()
        for (i in 0 until m) {
            if (mask and (1 shl i) != 0) {
                idxs.add(i)
            }
        }
        // If a button affects no counters in the joltage vector, it's irrelevant.
        if (idxs.isNotEmpty()) {
            btnCountersList.add(idxs.toIntArray())
        }
    }

    val n = btnCountersList.size
    if (n == 0) {
        // Only possible if all targets are zero.
        return if (target.all { it == 0 }) 0 else Int.MAX_VALUE
    }

    // Compute U_j = max times we could possibly press button j
    // based on each counter it touches: x_j <= min_{i in J_j} target[i].
    val U_raw = IntArray(n)
    run {
        for (j in 0 until n) {
            val counters = btnCountersList[j]
            var u = Int.MAX_VALUE
            for (i in counters) {
                val t = target[i]
                if (t < u) u = t
            }
            if (u == Int.MAX_VALUE) u = 0
            U_raw[j] = u
        }
    }

    // Remove buttons with U_j == 0 (they cannot contribute).
    val filteredCounters = mutableListOf<IntArray>()
    val filteredU = mutableListOf<Int>()
    for (j in 0 until n) {
        if (U_raw[j] > 0) {
            filteredCounters.add(btnCountersList[j])
            filteredU.add(U_raw[j])
        }
    }

    val n2 = filteredCounters.size
    if (n2 == 0) {
        // Again, only solvable if all targets are zero.
        return if (target.all { it == 0 }) 0 else Int.MAX_VALUE
    }

    // Rebuild arrays with only useful buttons.
    var btnCounters = Array(n2) { IntArray(0) }
    val maxPress = IntArray(n2)
    for (j in 0 until n2) {
        btnCounters[j] = filteredCounters[j]
        maxPress[j] = filteredU[j]
    }

    // Order buttons: most "involved" first (touching more counters).
    val order = (0 until n2).sortedByDescending { btnCounters[it].size }.toIntArray()
    val orderedBtnCounters = Array(n2) { IntArray(0) }
    val orderedMaxPress = IntArray(n2)
    for (k in 0 until n2) {
        val j = order[k]
        orderedBtnCounters[k] = btnCounters[j]
        orderedMaxPress[k] = maxPress[j]
    }
    btnCounters = orderedBtnCounters
    for (k in 0 until n2) {
        maxPress[k] = orderedMaxPress[k]
    }

    // Precompute remainMax[k][i] = maximum possible additional "increments"
    // we can still give to counter i using buttons from k..n2-1.
    val remainMax = Array(n2 + 1) { IntArray(m) }
    // remainMax[n2][i] = 0 for all i already.
    for (k in n2 - 1 downTo 0) {
        val prev = remainMax[k + 1]
        val cur = remainMax[k]
        // Start from previous.
        for (i in 0 until m) {
            cur[i] = prev[i]
        }
        val u = maxPress[k]
        for (idx in btnCounters[k]) {
            cur[idx] += u
        }
    }

    // Quick feasibility check: for each counter, total possible contribution must >= target.
    for (i in 0 until m) {
        if (remainMax[0][i] < target[i]) {
            return Int.MAX_VALUE
        }
    }

    val residual = target.clone()
    var best = Int.MAX_VALUE

    fun dfs(k: Int, currentSum: Int) {
        if (currentSum >= best) return

        // Compute max residual and basic feasibility.
        var maxRes = 0
        for (i in 0 until m) {
            val r = residual[i]
            if (r < 0) return
            if (r > maxRes) maxRes = r
        }

        // If all satisfied, update best.
        if (maxRes == 0) {
            if (currentSum < best) best = currentSum
            return
        }

        // If no more buttons but still residuals, dead end.
        if (k == n2) return

        // Lower bound: at least maxRes more presses are needed.
        if (currentSum + maxRes >= best) return

        // Additional feasibility: for each counter with residual > 0,
        // we must have some remaining total capacity.
        val rem = remainMax[k]
        for (i in 0 until m) {
            if (residual[i] > 0 && rem[i] == 0) return
        }

        val affected = btnCounters[k]
        val U = maxPress[k]

        // Determine bounds [lb, ub] for x_k.
        var lb = 0
        var ub = U
        for (idx in affected) {
            val r = residual[idx]
            if (r < 0) return
            if (r < ub) ub = r

            // r = x_k + sum_{j>k} a_ij x_j, with 0 <= x_j <= U_j
            // => x_k >= r - sum_{j>k} U_j * a_ij  = r - remainMax[k+1][idx]
            val maxRemContrib = remainMax[k + 1][idx]
            val neededFromThis = r - maxRemContrib
            if (neededFromThis > lb) lb = neededFromThis
        }

        if (lb < 0) lb = 0
        if (lb > ub) return

        // Try x_k from small to big (small first to hopefully find a good solution early).
        val savedResidual = IntArray(m)
        for (x in lb..ub) {
            val newSum = currentSum + x
            if (newSum >= best) break  // x only increases, so further values won't help.

            // Save residual
            for (i in 0 until m) savedResidual[i] = residual[i]
            if (x != 0) {
                for (idx in affected) {
                    residual[idx] -= x
                }
            }

            dfs(k + 1, newSum)

            // Restore residual
            for (i in 0 until m) residual[i] = savedResidual[i]
        }
    }

    dfs(0, 0)
    return best
}

private fun encodeState(values: IntArray, strides: IntArray): Int {
    var id = 0
    for (i in values.indices) {
        id += values[i] * strides[i]
    }
    return id
}

private fun decodeState(id: Int, radices: IntArray, out: IntArray) {
    var rem = id
    for (i in radices.indices) {
        val base = radices[i]
        out[i] = rem % base
        rem /= base
    }
}
