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

    // Precompute powers of 10 up to 10^18 (more than enough)
    val pow10 = LongArray(19).apply {
        this[0] = 1L
        for (i in 1 until size) {
            this[i] = this[i - 1] * 10L
        }
    }

    val maxR = ranges.maxOf { it.last }
    val maxDigits = maxR.toString().length

    // ---------- Part 1 ----------
    val invalidPart1 = findInvalidIdsPart1(ranges, maxR, maxDigits, pow10)
    val sumPart1 = invalidPart1.sum()
    println("Part 1: invalid IDs count = ${invalidPart1.size}")
    println("Part 1: sum of invalid IDs = $sumPart1")

    // ---------- Part 2 ----------
    val invalidPart2 = findInvalidIdsPart2(ranges, maxR, maxDigits, pow10)
    val sumPart2 = invalidPart2.sum()
    println("Part 2: invalid IDs count = ${invalidPart2.size}")
    println("Part 2: sum of invalid IDs = $sumPart2")
}

/**
 * Part 1:
 * ID is invalid if it is exactly some block of digits repeated twice: P P
 * (e.g. 55, 6464, 123123).
 */
fun findInvalidIdsPart1(
    ranges: List<LongRange>,
    maxR: Long,
    maxDigits: Int,
    pow10: LongArray
): Set<Long> {
    val invalid = mutableSetOf<Long>()

    // block length k, total length = 2k
    for (k in 1..(maxDigits / 2)) {
        val start = pow10[k - 1]        // smallest k-digit number (no leading zero)
        val end = pow10[k] - 1          // largest k-digit number

        for (x in start..end) {
            val candidate = x * pow10[k] + x  // concatenate x with itself

            if (candidate > maxR) {
                // For this k, larger x only increase candidate, so we can break
                break
            }

            if (ranges.any { candidate in it }) {
                invalid.add(candidate)
            }
        }
    }

    return invalid
}

/**
 * Part 2:
 * ID is invalid if it is some block P repeated at least twice:
 * P P, P P P, P P P P, ...
 */
fun findInvalidIdsPart2(
    ranges: List<LongRange>,
    maxR: Long,
    maxDigits: Int,
    pow10: LongArray
): Set<Long> {
    val invalid = mutableSetOf<Long>()

    // block length k
    for (k in 1..maxDigits) {
        val start = pow10[k - 1]        // smallest k-digit number (no leading zero)
        val end = pow10[k] - 1          // largest k-digit number

        val maxRep = maxDigits / k      // max repetitions before exceeding maxDigits
        if (maxRep < 2) continue        // need at least 2 repetitions

        for (x in start..end) {
            var candidate = x
            var digits = k

            // rep = how many total blocks we have so far (we start from 2)
            for (rep in 2..maxRep) {
                candidate = candidate * pow10[k] + x
                digits += k

                if (digits > maxDigits || candidate > maxR) {
                    // Any further reps will only increase the number
                    break
                }

                if (ranges.any { candidate in it }) {
                    invalid.add(candidate)
                }
            }
        }
    }

    return invalid
}
