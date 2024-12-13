import java.util.PriorityQueue

private data class PrizeRequest(val a: Coordinate, val b: Coordinate, val prizeLoc: Coordinate)
private data class State(val coord: Coordinate, val cost: Int)

private fun minTokensToWinMaxPrizes(input: String): Int {
    val requests = parseInput(input)
    return requests.sumOf { minCostToReachThePrize(it) }
}

private fun parseInput(input: String): List<PrizeRequest> {
    val prizeRequests = mutableListOf<PrizeRequest>()
    input.split("\n\r\n").forEach { request ->
        val line = request.lines()
        val a = parseCoordinateFromString(line[0], '+')
        val b = parseCoordinateFromString(line[1], '+')
        val prize = parseCoordinateFromString(line[2], '=')
        prizeRequests.add(PrizeRequest(a, b, prize))
    }
    return prizeRequests
}

private fun parseCoordinateFromString(input: String, symbol: Char): Coordinate {
    val regex = Regex("X\\$symbol(\\d+), Y\\$symbol(\\d+)")
    val matchResult = regex.find(input)!!
    val x = matchResult.groupValues[1].toInt()
    val y = matchResult.groupValues[2].toInt()
    return Coordinate(x, y)
}

private fun minCostToReachThePrize(request: PrizeRequest): Int {
    val queue = PriorityQueue(compareBy<State> { it.cost })
    val visited = mutableSetOf<Coordinate>()
    queue.add(State(Coordinate(0, 0), 0))
    while (queue.isNotEmpty()) {
        val currentState = queue.poll()
        if (!visited.add(currentState.coord)) continue
        if (currentState.coord.overcome(request.prizeLoc)) continue
        if (currentState.coord == request.prizeLoc) return currentState.cost
        queue.add(State(currentState.coord.plus(request.a), currentState.cost + 3))
        queue.add(State(currentState.coord.plus(request.b), currentState.cost + 1))
    }
    return 0
}

fun main() {
    val testInput = readText("Day13_test")
    check(minTokensToWinMaxPrizes(testInput) == 480)

    val input = readText("Day13")
    minTokensToWinMaxPrizes(input).println()
}
