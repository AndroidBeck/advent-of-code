fun calcAntinodes(field: List<String>, isResonant: Boolean = false): Int {
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
        val newAntiNodes = mutableSetOf<Coordinate>()
        var i = 1
        while (i < list.size) {
            val firstNode = list[i - 1]
            for (j in i..< list.size) {
                val secondNode = list[j]
                val antinodesInBorders =
                    if (isResonant) findResonantAntinodesInBordersFromPair(firstNode, secondNode, n)
                    else findAntinodesInBordersFromPair(firstNode, secondNode, n)
                newAntiNodes.addAll(antinodesInBorders)
            }
            i++
        }
        antinodes += newAntiNodes
    }
    // printAntinodesOnField(field, antinodes)
    return antinodes.size
}

private fun Coordinate.isInBorders(n: Int): Boolean {
    return this.first >= 0 && this.second >= 0 && this. first < n && this.second < n
}

private fun printAntinodesOnField(field: List<String>, antinodes: Set<Coordinate>) {
    field.forEachIndexed { i, line ->
        println()
        line.forEachIndexed { j, c ->
            if (Pair(i, j) in antinodes) print('#') else print('.')
        }
    }
}

private fun findAntinodesInBordersFromPair(node1: Coordinate, node2: Coordinate, border: Int): List<Coordinate> {
    val result = mutableListOf<Coordinate>()
    val deltaFirstCoord = node1.first - node2.first
    val deltaSecondCoord = node1.second - node2.second
    val aFirst = Coordinate(node1.first + deltaFirstCoord, node1.second + deltaSecondCoord)
    val aSecond = Coordinate(node2.first - deltaFirstCoord, node2.second - deltaSecondCoord)
    if (aFirst.isInBorders(border)) result.add(aFirst)
    if (aSecond.isInBorders(border)) result.add(aSecond)
    return result
}

// Part 2
private fun findResonantAntinodesInBordersFromPair(node1: Coordinate, node2: Coordinate, border: Int): List<Coordinate> {
    val result = mutableListOf<Coordinate>()
    val deltaFirstCoord = node1.first - node2.first
    val deltaSecondCoord = node1.second - node2.second
    var i = 0
    while (i < border) {
        val aFirst = Coordinate(node1.first + deltaFirstCoord * i, node1.second + deltaSecondCoord * i)
        if (aFirst.isInBorders(border)) result.add(aFirst) else break
        i++
    }
    i = 0
    while (i < border) {
        val aSecond = Coordinate(node2.first - deltaFirstCoord * i, node2.second - deltaSecondCoord * i)
        if (aSecond.isInBorders(border)) result.add(aSecond) else break
        i++
    }
    return result
}

fun main() {
    val testInput = readInput("Day08_test")
    check(calcAntinodes(testInput) == 14)

    val input = readInput("Day08")
    calcAntinodes(input).println()

    // Part 2
    check(calcAntinodes(testInput, isResonant = true) == 34)
    calcAntinodes(input, isResonant = true).println()
}
