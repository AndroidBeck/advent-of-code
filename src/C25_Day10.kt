import java.io.File
import kotlin.system.exitProcess

data class Machine(
    val targetMask: Int,
    val buttonMasks: IntArray
)

fun main() {
    val file = File("src/C25_Day10.txt")
    if (!file.exists()) {
        System.err.println("Input file src/C25_Day10.txt not found")
        exitProcess(1)
    }

    val lines = file.readLines().filter { it.isNotBlank() }
    var totalPresses = 0L

    for ((idx, line) in lines.withIndex()) {
        val machine = parseMachine(line)
        val presses = minPressesForMachine(machine)
        if (presses == Int.MAX_VALUE) {
            System.err.println("No solution found for machine on line ${idx + 1}")
            exitProcess(1)
        }
        totalPresses += presses
    }

    println(totalPresses)
}

// Parse one line like:
// [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
fun parseMachine(line: String): Machine {
    // Extract pattern in [ ... ]
    val patternStart = line.indexOf('[')
    val patternEnd = line.indexOf(']', patternStart + 1)
    if (patternStart == -1 || patternEnd == -1) {
        throw IllegalArgumentException("Invalid line (no pattern []): $line")
    }
    val pattern = line.substring(patternStart + 1, patternEnd).trim()

    // Target mask: bit i = 1 if pattern[i] == '#'
    var targetMask = 0
    for (i in pattern.indices) {
        if (pattern[i] == '#') {
            targetMask = targetMask or (1 shl i)
        }
    }

    // Extract all (...) button definitions
    val buttonsPart = line.substring(patternEnd + 1)
    val buttonRegex = Regex("""\(([^)]*)\)""")
    val buttonMasksList = mutableListOf<Int>()

    for (match in buttonRegex.findAll(buttonsPart)) {
        val inside = match.groupValues[1].trim()
        if (inside.isEmpty()) continue

        var mask = 0
        // Split by comma, allow spaces
        val parts = inside.split(',')
        for (p in parts) {
            val token = p.trim()
            if (token.isEmpty()) continue
            val idx = token.toInt()
            mask = mask or (1 shl idx)
        }
        // It's possible a button might not affect any lights (mask==0), but that’s useless.
        if (mask != 0) {
            buttonMasksList.add(mask)
        }
    }

    val buttonMasks = buttonMasksList.toIntArray()
    return Machine(targetMask, buttonMasks)
}

fun minPressesForMachine(machine: Machine): Int {
    val target = machine.targetMask
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

    // For each possible toggle mask of first half, keep minimal weight
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

    // Combine both halves: t1 XOR t2 == target  =>  t1 = target XOR t2
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
