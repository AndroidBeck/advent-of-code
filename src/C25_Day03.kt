import java.io.File
import kotlin.math.max

fun maxBankJoltage(bank: String): Int {
    val s = bank.trim()
    val n = s.length
    require(n >= 2) { "Each bank must contain at least two digits." }

    // suffixMax[i] = max digit in s[i..end]
    val suffixMax = IntArray(n + 1) { -1 }
    for (i in n - 1 downTo 0) {
        val d = s[i] - '0'
        suffixMax[i] = max(suffixMax[i + 1], d)
    }

    var best = -1
    for (i in 0 until n - 1) {
        val first = s[i] - '0'
        val second = suffixMax[i + 1]
        val candidate = first * 10 + second
        if (candidate > best) best = candidate
    }

    return best
}

fun main() {
    val filePath = "src/C25_Day03.txt"
    val lines = File(filePath).readLines()

    var total = 0L
    for (line in lines) {
        if (line.isBlank()) continue
        total += maxBankJoltage(line)
    }

    println("Total output joltage = $total")
}
