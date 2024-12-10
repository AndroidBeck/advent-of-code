private fun Coordinate.getConnections(matrix: List<List<Int>>): List<Coordinate> {
    val x = this.first
    val y = this.second
    val currentValue = matrix[y][x]
    val border = matrix.size - 1
    val searchValue = currentValue + 1
    val neighbours = mutableListOf<Coordinate>()
    val connections = mutableListOf<Coordinate>()
    if (x > 0) neighbours.add(Coordinate(x - 1, y))
    if (x < border) neighbours.add(Coordinate(x + 1, y))
    if (y > 0) neighbours.add(Coordinate(x, y - 1))
    if (y < border) neighbours.add(Coordinate(x, y + 1))
    neighbours.forEach { coord ->
        if (matrix[coord.second][coord.first] == searchValue) connections.add(coord)
    }
    return connections
}

private fun sumOfAllTrailheadsScores(input: List<String>): Int {
    val matrix = input.map { it.toCharArray().map { c -> c.digitToIntOrNull()!! } }
    val heads = mutableListOf<Coordinate>()
    val tops = mutableSetOf<Coordinate>()
    val edges = mutableMapOf<Coordinate, MutableSet<Coordinate>>()
    matrix.forEachIndexed { y, line ->
        line.forEachIndexed { x, value ->
            val coordinate = Coordinate(x, y)
            if (value == 0) heads.add(coordinate)
            if (value == 9) tops.add(coordinate)
            val connections = coordinate.getConnections(matrix)
            edges.computeIfAbsent(coordinate) { mutableSetOf() }.addAll(connections)
        }
    }
//    edges.forEach { edge -> println("${edge.key}: ${edge.value.joinToString(" ")}") }
    var sum = 0
    heads.forEach { head ->
        sum += getNumberOfReachableTops(head, edges)
    }
    return sum
}

private fun getNumberOfReachableTops(head: Coordinate, edges: Map<Coordinate, MutableSet<Coordinate>>): Int {
    var connections = mutableSetOf(head)
    val newConnections = mutableSetOf<Coordinate>()
//    println("head = $head")
//    println("edges1 = ${edges[Coordinate(0,1)]} edges2 = ${edges[Coordinate(1,0)]}")
    repeat(9) {
        if (connections.isEmpty()) {
//            println("Empty connections")
            return 0
        }
        connections.forEach { node ->
            val elements = edges[node]
//            println("$node -> $elements")
            elements?.let { newConnections.addAll(it) }
        }
//        newConnections.println()
        connections = newConnections.toHashSet()
//        println("..connections = $connections")
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
}
