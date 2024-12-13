import java.util.PriorityQueue

private data class PrizeRequest(val a: CoordinateL, val b: CoordinateL, val prizeLoc: CoordinateL)
private data class State(val coord: CoordinateL, val cost: Long)

private fun minTokensToWinMaxPrizes(input: String, part2: Boolean = false): Long {
    val part2Increment = CoordinateL(10000000000000, 10000000000000)
    val requests = parseInput(input)
    return if (part2) requests.map { it.copy(prizeLoc = it.prizeLoc.lPlus(part2Increment))}.sumOf { minCostToReachThePrizeP2(it) }
    else requests.sumOf { minCostToReachThePrize(it) }
}

private fun parseInput(input: String): List<PrizeRequest> {
    val prizeRequests = mutableListOf<PrizeRequest>()
    input.split("\n\r\n").forEach { request ->
        val line = request.lines()
        val a = parseCoordinateLFromString(line[0], '+')
        val b = parseCoordinateLFromString(line[1], '+')
        val prize = parseCoordinateLFromString(line[2], '=')
        prizeRequests.add(PrizeRequest(a, b, prize))
    }
    return prizeRequests
}

private fun parseCoordinateLFromString(input: String, symbol: Char): CoordinateL {
    val regex = Regex("X\\$symbol(\\d+), Y\\$symbol(\\d+)")
    val matchResult = regex.find(input)!!
    val x = matchResult.groupValues[1].toLong()
    val y = matchResult.groupValues[2].toLong()
    return CoordinateL(x, y)
}

private fun minCostToReachThePrize(request: PrizeRequest, part2: Boolean = false): Long {
    val queue = PriorityQueue(compareBy<State> { it.cost })
    val visited = mutableSetOf<CoordinateL>()
    val (a, b, prizeC) = request
    queue.add(State(CoordinateL(0, 0), 0))
    while (queue.isNotEmpty()) {
        val currentState = queue.poll()
        if (!visited.add(currentState.coord)) continue
        if (currentState.coord.lOvercome(prizeC)) continue
        if (currentState.coord == prizeC) return currentState.cost
        queue.add(State(currentState.coord.lPlus(a), currentState.cost + 3))
        queue.add(State(currentState.coord.lPlus(b), currentState.cost + 1))
    }
    return 0
}

private fun minCostToReachThePrizeP2(request: PrizeRequest): Long {
    val (a, b, prizeC) = request
    val costA = 3
    val costB = 1

    val n2Numerator = prizeC.y() * a.x() - prizeC.x() * a.y()
    val n2Denominator = a.x() * b.y() - b.x() * a.y()
    val n2 = n2Numerator / n2Denominator

    val n1numerator = prizeC.x() - b.x() * n2
    val n1Denominator = a.x()
    val n1 = n1numerator / n1Denominator

    val cost = n1 * costA + n2 * costB
    val finalC = CoordinateL(n1 * a.x() + n2 * b.x(), n1 * a.y() + n2 * b.y())
    return if (finalC == prizeC) cost else 0
}

fun main() {
    val testInput = readText("Day13_test")
    val input = readText("Day13")
    check(minTokensToWinMaxPrizes(testInput) == 480L)
    minTokensToWinMaxPrizes(input).println()
    minTokensToWinMaxPrizes(input, part2 = true).println()
}
