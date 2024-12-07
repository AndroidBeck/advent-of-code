private fun isCorrectEquation(result: Long, numbers: List<Int>): Boolean {
    var isCorrect = false
    val checkResultByRecursion = DeepRecursiveFunction<Pair<Int, Long>, Unit> { (i, midResult) ->
        // println("checkResultByRecursion: $i, $midResult")
        if (i < numbers.size) {
            callRecursive(Pair(i + 1, midResult + numbers[i]))
            callRecursive(Pair(i + 1, midResult * numbers[i]))
        } else if (midResult == result) {
            isCorrect = true
        }
    }
    checkResultByRecursion(Pair(1, numbers[0].toLong()))
    return isCorrect
}

fun calcSumOfCorrectResults(input: List<String>): Long {
    var sum = 0L
    input.forEach { line ->
        val (p1, p2) = line.split(": ")
        val result = p1.trim().toLong()
        val numbers = p2.trim().split(" ").map { it.toInt() }
        if (isCorrectEquation(result, numbers)) sum += result
    }
    return sum
}

fun main() {
    check(calcSumOfCorrectResults(listOf("6: 1 2 3")) == 6L)

    val testInput = readInput("Day07_test")
    calcSumOfCorrectResults(testInput).println()
    check(calcSumOfCorrectResults(testInput) == 3749L)

    val input = readInput("Day07")
    calcSumOfCorrectResults(input).println()
}
