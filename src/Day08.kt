fun calcAntinodes(field: List<String>): Int {
    val n = field.size
    val antennasMap = mutableMapOf<Char, MutableList<Coordinate>>()
    val antinodes = mutableSetOf<Coordinate>()
    field.forEachIndexed { i, line ->
        line.forEachIndexed { j, c ->
            if (c != '.') antennasMap.computeIfAbsent(c) { mutableListOf() }.add(Coordinate(i, j))
        }
    }

    antennasMap.values.forEach { list ->
        if (list.size < 2) return@forEach
        antinodes += findAntinodesInField(list, n)
    }
//    printAntinodesOnField(field, antinodes)
    return antinodes.size
}

private fun printAntinodesOnField(field: List<String>, antinodes: Set<Coordinate>) {
    field.forEachIndexed { i, line ->
        println()
        line.forEachIndexed { j, c ->
            if (Pair(i, j) in antinodes) print('#') else print('.')
        }
    }
}

private fun findAntinodesInField(list: List<Coordinate>, border: Int): Set<Coordinate> {
    val newAntiNodes = mutableSetOf<Coordinate>()
    val n = list.size
//    list.joinToString(" ", prefix = "lit of coord: ").println()
    if (n < 2) return newAntiNodes
    var i = 1
    while (i < n) {
        val firstNode = list[i - 1]
        for (j in i..< n) {
            val secondNode = list[j]
            val pairOfAntinodes = findPairOfAntinodes(firstNode, secondNode)
            if (pairOfAntinodes.first.isInBorders(border)) newAntiNodes.add(pairOfAntinodes.first)
            if (pairOfAntinodes.second.isInBorders(border)) newAntiNodes.add(pairOfAntinodes.second)
//            println("nodes = $firstNode $secondNode, antinodesToCheck = ${pairOfAntinodes.first} ${pairOfAntinodes.second} ${newAntiNodes.joinToString(" ", prefix = "new antinodes set: ")}")
        }
        i++
    }
    return newAntiNodes
}

private fun findPairOfAntinodes(node1: Coordinate, node2: Coordinate): Pair<Coordinate, Coordinate> {
    val deltaFirstCoord = node1.first - node2.first
    val deltaSecondCoord = node1.second - node2.second
    val aFirst = Coordinate(node1.first + deltaFirstCoord, node1.second + deltaSecondCoord)
    val aSecond = Coordinate(node2.first - deltaFirstCoord, node2.second - deltaSecondCoord)
    return Pair(aFirst, aSecond)
}

private fun Coordinate.isInBorders(n: Int): Boolean {
    return this.first >= 0 && this.second >= 0 && this. first < n && this.second < n
}

fun main() {
    val testInput = readInput("Day08_test")
    check(calcAntinodes(testInput) == 14)

    val input = readInput("Day08")
    calcAntinodes(input).println()
}
