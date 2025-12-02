import java.io.File

fun main() {
    // Read your puzzle input: single long line with ranges.
    val line = File("src/C25_Day02.txt").readText().trim()

    // Parse "a-b,c-d,..." into List<LongRange>
    val ranges: List<LongRange> = line
        .split(',')
        .filter { it.isNotBlank() }
        .map { part ->
            val (fromStr, toStr) = part.split('-')
            val from = fromStr.toLong()
            val to = toStr.toLong()
            from..to
        }

    // Precompute powers of 10 up to 10^18 (more than enough for typical AoC inputs)
    val pow10 = LongArray(19).apply {
        this[0] = 1L
        for (i in 1 until size) {
            this[i] = this[i - 1] * 10L
        }
    }

    val maxR = ranges.maxOf { it.last }
    val maxDigits = maxR.toString().length

    val invalidIds = mutableListOf<Long>()

    // For each possible half-length k
    for (k in 1..(maxDigits / 2)) {
        val start = pow10[k - 1]          // smallest k-digit number (no leading zero)
        val end = pow10[k] - 1            // largest k-digit number

        for (x in start..end) {
            val candidate = x * pow10[k] + x  // concatenate x with itself

            if (candidate > maxR) {
                // For this k, all further x will give larger candidates, so break early
                break
            }

            // Check if candidate is in ANY of the ranges
            if (ranges.any { candidate in it }) {
                invalidIds.add(candidate)
            }
        }
    }

    val answer = invalidIds.sum()

    println("Invalid IDs found: ${invalidIds.size}")
    println("Sum of invalid IDs: $answer")
}
