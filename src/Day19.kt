fun possibleDesigns(input: List<String>, p2: Boolean = false): Long {
    val patterns = input.first().trim().split(", ").toSet()
    val words = input.takeLast(input.size - 2)
    val lengths = patterns.map { it.length }.toSet()
    var acc = 0L
    if (!p2) {
        words.forEach { if (isDesignPossible(it, patterns, lengths)) acc++ }
        return acc
    }

    // Part 2
    val designsMap = mutableMapOf<String, Long>()
    fun waysOfDesign(str: String): Long {
        var ways = 0L
        designsMap[str]?.let { return it }
        if (str in patterns) ways++
        lengths.forEach { length ->
            if (length < str.length && str.take(length) in patterns) {
                val newStr = str.takeLast(str.length - length)
                ways += waysOfDesign(newStr)
            }
        }
        designsMap[str] = ways
        return ways
    }

    words.forEach { acc += waysOfDesign(it) }
    return acc
}

private fun isDesignPossible(word: String, patterns: Set<String>, lengths: Set<Int>): Boolean {
    val queue = ArrayDeque<String>()
    queue.add(word)
    while (queue.isNotEmpty()) {
        val str = queue.removeFirstOrNull()!!
        if (str in patterns) return true
        lengths.forEach { length ->
            if (length > str.length || str.take(length) !in patterns) return@forEach
            queue.addFirst(str.takeLast(str.length - length))
        }
    }
    return false
}

fun main() {
    val testInput = readInput("Day19_test")
    val input = readInput("Day19")
    check(possibleDesigns(testInput) == 6L)
    possibleDesigns(input).println() // 355L

    check(possibleDesigns(testInput, p2 = true) == 16L)
    possibleDesigns(input, p2 = true).println() // 732978410442050
}
