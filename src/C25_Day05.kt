import java.io.File

fun main(args: Array<String>) {
    val fileName = if (args.isNotEmpty()) args[0] else "src/C25_Day05.txt"
    val lines = File(fileName).readLines()

    // --- Parse ranges ---
    val ranges = mutableListOf<Pair<Long, Long>>()
    var index = 0

    // First block: ranges "a-b" until a blank line
    while (index < lines.size && lines[index].isNotBlank()) {
        val line = lines[index].trim()
        val parts = line.split("-")
        val start = parts[0].toLong()
        val end = parts[1].toLong()
        ranges += start to end
        index++
    }

    // Skip blank lines between sections
    while (index < lines.size && lines[index].isBlank()) {
        index++
    }

    // --- Parse IDs (for part 1 only) ---
    val ids = mutableListOf<Long>()
    while (index < lines.size) {
        val line = lines[index].trim()
        if (line.isNotEmpty()) {
            ids += line.toLong()
        }
        index++
    }

    if (ranges.isEmpty()) {
        // No ranges means no fresh IDs at all
        println("Part 1: 0")
        println("Part 2: 0")
        return
    }

    // --- Merge overlapping / adjacent ranges ---
    val sorted = ranges.sortedWith(compareBy<Pair<Long, Long>> { it.first }.thenBy { it.second })
    val merged = mutableListOf<Pair<Long, Long>>()

    var currentStart = sorted[0].first
    var currentEnd = sorted[0].second

    for (i in 1 until sorted.size) {
        val (s, e) = sorted[i]
        if (s <= currentEnd + 1) {
            // overlaps or touches: extend the current interval
            if (e > currentEnd) currentEnd = e
        } else {
            // disjoint: push previous and start a new one
            merged += currentStart to currentEnd
            currentStart = s
            currentEnd = e
        }
    }
    merged += currentStart to currentEnd

    // ================
    // Part 1: how many of the available IDs are fresh?
    // ================
    fun isFresh(id: Long): Boolean {
        var lo = 0
        var hi = merged.size - 1
        while (lo <= hi) {
            val mid = (lo + hi) ushr 1
            val (start, end) = merged[mid]
            when {
                id < start -> hi = mid - 1
                id > end -> lo = mid + 1
                else -> return true // id in [start, end]
            }
        }
        return false
    }

    var freshCount = 0
    for (id in ids) {
        if (isFresh(id)) freshCount++
    }

    // ================
    // Part 2: how many IDs are considered fresh by the ranges?
    // (total size of the union of all ranges)
    // ================
    var totalFreshByRanges = 0L
    for ((start, end) in merged) {
        totalFreshByRanges += (end - start + 1)
    }

    println("Part 1: $freshCount")
    println("Part 2: $totalFreshByRanges")
}
