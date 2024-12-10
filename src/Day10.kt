private fun Coordinate.getNeighbours(matrix: List<List<Int>>): List<Coordinate> {
    val x = this.first
    val y = this.second
    val border = matrix.size - 1
    val neighbours = mutableListOf<Coordinate>()
    if (x > 0) neighbours.add(Coordinate(x - 1, y))
    if (x < border) neighbours.add(Coordinate(x + 1, y))
    if (y > 0) neighbours.add(Coordinate(x, y - 1))
    if (y < border) neighbours.add(Coordinate(x, y + 1))
    return neighbours
}

private fun Coordinate.getConnections(matrix: List<List<Int>>): List<Coordinate> {
    val x = this.first
    val y = this.second
    val currentValue = matrix[y][x]
    val searchValue = currentValue + 1
    val connections = mutableListOf<Coordinate>()
    getNeighbours(matrix).forEach { coord ->
        if (matrix[coord.second][coord.first] == searchValue) connections.add(coord)
    }
    return connections
}

private fun sumOfAllTrailheadsScores(input: List<String>, manyPathPerTop: Boolean = false): Int {
    val matrix = input.map { it.toCharArray().map { c -> c.digitToIntOrNull()!! } }
    val heads = mutableListOf<Coordinate>()
    val edges = mutableMapOf<Coordinate, MutableSet<Coordinate>>()
    matrix.forEachIndexed { y, line ->
        line.forEachIndexed { x, value ->
            val coordinate = Coordinate(x, y)
            if (value == 0) heads.add(coordinate)
            val connections = coordinate.getConnections(matrix)
            edges.computeIfAbsent(coordinate) { mutableSetOf() }.addAll(connections)
        }
    }
    var sum = 0
    heads.forEach { head ->
        sum += getNumberOfPathsToTops(head, edges, manyPathPerTop)
    }
    return sum
}

private fun getNumberOfPathsToTops(head: Coordinate, edges: Map<Coordinate, MutableSet<Coordinate>>, manyPathPerTop: Boolean = false): Int {
    var connections = mutableListOf(head)
    val newConnections = mutableListOf<Coordinate>()
    repeat(9) {
        if (connections.isEmpty()) return 0
        connections.forEach { node -> edges[node]?.let { newConnections.addAll(it) } }
        connections = if (manyPathPerTop) newConnections.toMutableList() else newConnections.toSet().toMutableList()
        newConnections.clear()
    }
    return connections.size
}

fun main() {
    check(sumOfAllTrailheadsScores(listOf("0123", "1234", "8765", "9876")) == 1)
    val testInput = readInput("Day10_test")
    check(sumOfAllTrailheadsScores(testInput) == 36)

    val input = readInput("Day10")
    sumOfAllTrailheadsScores(input).println()

    // Part 2
    check(sumOfAllTrailheadsScores(testInput,manyPathPerTop = true) == 81)
    sumOfAllTrailheadsScores(input, manyPathPerTop = true).println()
}
