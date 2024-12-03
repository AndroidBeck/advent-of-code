fun parseInputAndCalcMultiplicationsSum(input: String): Int {
    val regex = Regex("mul\\((-?\\d+),(-?\\d+)\\)")
    val pairs = regex.findAll(input).map { matchResult ->
        val n1 = matchResult.groupValues[1].toInt()
        val n2 = matchResult.groupValues[2].toInt()
        n1 to n2
    }.toList()
    var sum = 0
    pairs.forEach { pair ->
        sum += pair.first * pair.second
    }
    return sum
}

fun main() {
    check(parseInputAndCalcMultiplicationsSum("Some text mul(1,2) other text mul(3,5) and mul(3,4)\n") == 29)
    check(parseInputAndCalcMultiplicationsSum("Some text mul(1, 2) other text mul(3,5) and mul(3,4)\n") == 27)

//    val testInput = readInput("Day03_test")
//    check(parseInputAndCalcMultiplicationsSum(testInput) == 3)

    val input = readText("Day03")
    parseInputAndCalcMultiplicationsSum(input).println()
}
