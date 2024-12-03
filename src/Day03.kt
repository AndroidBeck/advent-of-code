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

fun calcMultSumWithStartStop(input: String) = parseInputAndCalcMultiplicationsSum(extractDoSubsequence(input))

private fun extractDoSubsequence(input: String): String {
    val startStr = "do()"
    val stopStr = "don't()"
    val output = StringBuilder()
    var taking = true
    var i = 0
    while (i < input.length) {
        if (input.startsWith(stopStr, i)) {
            taking = false
            i += stopStr.length - 1
        } else if (input.startsWith(startStr, i)) {
            taking = true
            i += startStr.length - 1
        } else if (taking) output.append(input[i])
        i++
    }
    return output.toString()
}

fun main() {
    check(parseInputAndCalcMultiplicationsSum("Some text mul(1,2) other text mul(3,5) and mul(3,4)\n") == 29)
    check(parseInputAndCalcMultiplicationsSum("Some text mul(1, 2) other text mul(3,5) and mul(3,4)\n") == 27)

    val input = readText("Day03")
    parseInputAndCalcMultiplicationsSum(input).println()

    check(calcMultSumWithStartStop("xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))") == 48)
    calcMultSumWithStartStop(input).println()
}
