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

// -------- Part 2: joltage counters with BFS --------

fun minPressesJolts(machine: Machine): Int {
    val target = machine.joltageTargets
    val m = target.size
    if (m == 0) return 0

    // Precompute which counters each button affects (indices list)
    val buttonsMask = machine.buttonMasks
    val buttonIndices = mutableListOf<IntArray>()
    for (mask in buttonsMask) {
        val idxs = mutableListOf<Int>()
        for (i in 0 until m) {
            if (mask and (1 shl i) != 0) {
                idxs.add(i)
            }
        }
        if (idxs.isNotEmpty()) {
            buttonIndices.add(idxs.toIntArray())
        }
    }
    if (buttonIndices.isEmpty()) {
        // No useful buttons; only solvable if all targets are 0
        return if (target.all { it == 0 }) 0 else Int.MAX_VALUE
    }

    // Mixed-radix encoding: radices[i] = target[i] + 1
    val radices = IntArray(m)
    val strides = IntArray(m)
    var mul = 1L
    for (i in 0 until m) {
        val base = target[i] + 1
        if (base <= 0) return Int.MAX_VALUE // negative target impossible
        radices[i] = base
        strides[i] = mul.toInt()
        mul *= base.toLong()
        // safety: if state space explodes, abort with "no solution" (shouldn't happen in puzzle input)
        if (mul > 50_000_000L) {
            // Too many states; treat as unsolvable under this approach
            return Int.MAX_VALUE
        }
    }
    val stateCount = mul.toInt()

    // Encode target state
    val targetId = encodeState(target, strides)

    // BFS
    val dist = IntArray(stateCount) { -1 }
    val queue = IntArray(stateCount)
    var head = 0
    var tail = 0

    dist[0] = 0
    queue[tail++] = 0

    val currentCounters = IntArray(m)

    while (head < tail) {
        val id = queue[head++]
        val d = dist[id]
        if (id == targetId) return d

        // Decode id -> currentCounters[]
        decodeState(id, radices, currentCounters)

        // Try pressing each button once
        for (idxs in buttonIndices) {
            var ok = true
            // Check we don't overshoot any counter this button touches
            for (idx in idxs) {
                if (currentCounters[idx] == target[idx]) {
                    ok = false
                    break
                }
            }
            if (!ok) continue

            var newId = id
            // Each press adds 1 to those counters -> +stride[idx] in ID
            for (idx in idxs) {
                newId += strides[idx]
            }

            if (dist[newId] == -1) {
                dist[newId] = d + 1
                queue[tail++] = newId
            }
        }
    }

    // If we exhausted the BFS without reaching target
    return Int.MAX_VALUE
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
