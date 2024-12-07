private fun isCorrectEquation(result: Long, numbers: List<Int>, addConcatOp: Boolean): Boolean {
    var isCorrect = false
    val checkResultByRecursion = DeepRecursiveFunction<Pair<Int, Long>, Unit> { (i, midResult) ->
        // println("checkResultByRecursion: $i, $midResult")
        if (i < numbers.size) {
            callRecursive(Pair(i + 1, midResult + numbers[i]))
            callRecursive(Pair(i + 1, midResult * numbers[i]))
            if (addConcatOp) callRecursive(Pair(i + 1, concat(midResult, numbers[i].toLong())))
        } else if (midResult == result) {
            isCorrect = true
        }
    }
    checkResultByRecursion(Pair(1, numbers[0].toLong()))
    return isCorrect
}

fun calcSumOfCorrectResults(input: List<String>, addConcatOp: Boolean = false): Long {
    var sum = 0L
    input.forEach { line ->
        val (p1, p2) = line.split(": ")
        val result = p1.trim().toLong()
        val numbers = p2.trim().split(" ").map { it.toInt() }
        if (isCorrectEquation(result, numbers, addConcatOp)) sum += result
    }
    return sum
}

private fun concat(n1: Long, n2: Long): Long {
    return "${n1}${n2}".toLong()
}

fun main() {
    check(calcSumOfCorrectResults(listOf("6: 1 2 3")) == 6L)

    val testInput = readInput("Day07_test")
    check(calcSumOfCorrectResults(testInput) == 3749L)

    val input = readInput("Day07")
    calcSumOfCorrectResults(input).println()

    // Part 2
    check(calcSumOfCorrectResults(testInput, addConcatOp = true) == 11387L)
    calcSumOfCorrectResults(input, addConcatOp = true).println()
}
