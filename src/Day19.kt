fun possibleDesigns(input: List<String>): Int {
    val patterns = input.first().trim().split(", ").toSet()
    val words = input.takeLast(input.size - 2)
    val lengths = patterns.map { it.length }.sortedByDescending { it }
    var acc = 0
    words.forEach { if (isDesignPossible(it, patterns, lengths)) acc++ }
    return acc
}

private fun isDesignPossible(word: String, patterns: Set<String>, lengths: List<Int>): Boolean {
    val queue = ArrayDeque<String>()
    queue.add(word)
    while (queue.isNotEmpty()) {
        val str = queue.removeFirstOrNull()!!
        lengths.forEach { length ->
            if (str in patterns) return true
            if (length > str.length || str.take(length) !in patterns) return@forEach
            queue.addFirst(str.takeLast(str.length - length))
        }
    }
    return false
}

fun main() {
    val testInput = readInput("Day19_test")
    val myTestInput = readInput("Day19_test_my")
    val input = readInput("Day19")
    check(possibleDesigns(testInput) == 6)
    check(possibleDesigns(myTestInput) == 1)
    possibleDesigns(input).println() // 355
}
