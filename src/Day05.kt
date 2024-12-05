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
        val pagesStr = pages.joinToString(" ")
        if (isCorrectPagesSequence(pages, lessers, greaters)) {
            val medianIndex = pages.size / 2
            acc += pages[medianIndex]
            println("$i: true, index = $medianIndex page = ${pages[medianIndex]} acc = $acc, pages = $pagesStr")
        } else {
            println("$i: false, pages = $pagesStr")
        }
    }
    return acc
}

fun main() {
    val (testRules, testRequests) = readText("Day05_test").split("\n\r\n").map { it.split("\n") }
    check(getSumOfCorrectSequencesMiddlePages(testRules, testRequests) == 143) // 61, 53, and 29

    val (rules, requests) = readText("Day05").split("\n\r\n").map { it.split("\n") }
    getSumOfCorrectSequencesMiddlePages(rules, requests).println()
}
