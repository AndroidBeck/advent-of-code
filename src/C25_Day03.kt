import java.io.File

/**
 * Returns the maximum joltage (as Long) that can be formed
 * by choosing exactly k digits from the bank (in order, no reordering).
 */
fun maxJoltageForK(bank: String, k: Int): Long {
    val s = bank.trim()
    val n = s.length
    require(k in 1..n) { "k must be between 1 and bank length; got k=$k, len=$n" }

    var toRemove = n - k
    val stack = StringBuilder()

    for (ch in s) {
        // While we can remove more digits and the last digit in the stack is smaller
        // than the current one, remove it to make the number larger.
        while (toRemove > 0 && stack.isNotEmpty() && stack[stack.length - 1] < ch) {
            stack.setLength(stack.length - 1) // pop last char
            toRemove--
        }
        stack.append(ch)
    }

    // If we still have more than k digits, cut off the tail.
    val resultStr =
        if (stack.length > k) stack.substring(0, k)
        else stack.toString()

    // Convert to Long
    var value = 0L
    for (c in resultStr) {
        value = value * 10 + (c - '0')
    }
    return value
}

fun main() {
    val filePath = "src/C25_Day03.txt"
    val lines = File(filePath).readLines()

    var totalPart1 = 0L
    var totalPart2 = 0L

    for (line in lines) {
        val bank = line.trim()
        if (bank.isEmpty()) continue

        // Part 1: choose exactly 2 digits
        totalPart1 += maxJoltageForK(bank, 2)

        // Part 2: choose exactly 12 digits
        totalPart2 += maxJoltageForK(bank, 12)
    }

    println("Part 1 total output joltage = $totalPart1")
    println("Part 2 total output joltage = $totalPart2")
}
