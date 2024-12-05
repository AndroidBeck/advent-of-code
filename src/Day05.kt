private fun isCorrectPagesSequence(pages: List<Int>, lessers: List<Int>, greaters: List<Int>): Boolean {
    val map = mutableMapOf<Int, Int>()
    pages.forEachIndexed { i, page ->
        map[page] = i
    }
    for (i in lessers.indices) {
        val lesser = lessers[i]
        val greater = greaters[i]
        if (map.containsKey(lesser) && map.containsKey(greater) && map[lesser]!! >= map[greater]!!) {
            return false
        }
    }
    return true
}

fun getSumOfCorrectSequencesMiddlePages(rules: List<String>, requests: List<String>): Int {
    val lessers = MutableList(rules.size) { 0 }
    val greaters = MutableList(rules.size) { 0 }
    rules.forEachIndexed { i, rule ->
        val (lesser, greater) = rule.trim().split("|")
        lessers[i] = lesser.toInt()
        greaters[i] = greater.toInt()
    }
    var acc = 0
    requests.forEachIndexed { i,  request ->
        val pages = request.trim().split(",").map { it.toInt() }
        if (isCorrectPagesSequence(pages, lessers, greaters)) {
            val medianIndex = pages.size / 2
            acc += pages[medianIndex]
        }
    }
    return acc
}

// Part 2
private fun MutableList<Int>.shiftRight1(from: Int, to: Int) {
    val last = this[to]
    var tmp = this[from]
    for (k in from..< to) {
        this[k + 1] = tmp.also { tmp = this[k + 1] }
    }
    this[from] = last
}

private fun getCorrectPagesSequence(pages: List<Int>, lesserToGreaters: Map<Int, Set<Int>>): List<Int> {
    if (pages.size < 2) return pages
    val correctlyOrderedPages = mutableListOf<Int>()
    correctlyOrderedPages.addAll(pages)
    correctlyOrderedPages.forEachIndexed { i, page ->
        val greaterSet = lesserToGreaters[page] ?: return@forEachIndexed
        for (j in 0..< i) {
            val prevElement = correctlyOrderedPages[j]
            if (greaterSet.contains(prevElement)) {
                correctlyOrderedPages.shiftRight1(j, i)
//                println("i = $i j = $j page = $page prevElement = $prevElement newOrdered = ${correctlyOrderedPages.joinToString(" ")}")
                break
            }
        }
    }
    return correctlyOrderedPages
}

fun getSumOfIncorrectSequencesMiddlePages(rules: List<String>, requests: List<String>): Int {
    val lessers = MutableList(rules.size) { 0 }
    val greaters = MutableList(rules.size) { 0 }
    val lesserToGreaters = mutableMapOf<Int, MutableSet<Int>>()
    rules.forEachIndexed { i, rule ->
        val (lesser, greater) = rule.trim().split("|").map { it.toInt() }
        lessers[i] = lesser
        greaters[i] = greater
        lesserToGreaters.computeIfAbsent(lesser) { mutableSetOf() }.add(greaters[i])
    }
//    lesserToGreaters.forEach { println("lesserToGreaters = ${it.key} to ${it.value.joinToString(" ")}") }
    var acc = 0
    requests.forEachIndexed { i,  request ->
        val pages = request.trim().split(",").map { it.toInt() }
        if (!isCorrectPagesSequence(pages, lessers, greaters)) {
            val correctlyOrderedPages = getCorrectPagesSequence(pages, lesserToGreaters)
            val medianIndex = correctlyOrderedPages.size / 2
            acc += correctlyOrderedPages[medianIndex]
//            val pagesStr = pages.joinToString(" ")
//            println("$i: true, index = $medianIndex page = ${correctlyOrderedPages[medianIndex]}, pages = $pagesStr -> $correctlyOrderedPages")
        }
    }
    return acc
}

fun main() {
    val (testRules, testRequests) = readText("Day05_test").split("\n\r\n").map { it.split("\n") }
    check(getSumOfCorrectSequencesMiddlePages(testRules, testRequests) == 143) // 61, 53, and 29

    val (rules, requests) = readText("Day05").split("\n\r\n").map { it.split("\n") }
    getSumOfCorrectSequencesMiddlePages(rules, requests).println()

    // Part 2
    check(getSumOfIncorrectSequencesMiddlePages(testRules, testRequests) == 123) // 47, 29, 47
    getSumOfIncorrectSequencesMiddlePages(rules, requests).println()
}
