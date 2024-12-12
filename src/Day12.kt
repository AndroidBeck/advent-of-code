private fun Coordinate.getSameTypeNeighbours(matrix: List<String>): List<Coordinate> {
    val x = this.first
    val y = this.second
    val value = matrix[y][x]
    val border = matrix.size - 1
    val neighbours = mutableListOf<Coordinate>()
    if (x > 0) neighbours.add(Coordinate(x - 1, y))
    if (x < border) neighbours.add(Coordinate(x + 1, y))
    if (y > 0) neighbours.add(Coordinate(x, y - 1))
    if (y < border) neighbours.add(Coordinate(x, y + 1))
    return neighbours.filter { matrix[it.second][it.first] == value }
}

private class Figure private constructor(val type: Char) {
    val pieces = mutableSetOf<Coordinate>()

    var perimeter: Int = 0
    val square: Int
        get() = pieces.size
    val fencingPrice: Int
        get() = square * perimeter

    companion object {
        fun build(square: Coordinate, matrix: List<String>, visited: Array<BooleanArray>): Figure {
            val x = square.first
            val y = square.second
            val figure = Figure(type = matrix[square.second][square.first])
            fun addPieceAndCalcPerimeter(square: Coordinate) {
                figure.pieces.add(square)
                visited[square.second][square.first] = true
                val sameTypeNeighbours = square.getSameTypeNeighbours(matrix)
                figure.perimeter += 4 - sameTypeNeighbours.size
                sameTypeNeighbours.forEach { neighbour ->
                    if (visited[neighbour.second][neighbour.first]) return@forEach
                    addPieceAndCalcPerimeter(neighbour)
                }
            }
            addPieceAndCalcPerimeter(square)
            square.getSameTypeNeighbours(matrix)
            return figure
        }
    }
}

private fun findFigures(input: List<String>): List<Figure> {
    val n = input.size
    val figures = mutableListOf<Figure>()
    val visited = Array(n) { BooleanArray(n) }
    input.forEachIndexed { i, line ->
        line.forEachIndexed { j, c ->
            if (visited[i][j]) return@forEachIndexed
            val square = Coordinate(j, i)
            val figure = Figure.build(square, input, visited)
            figures.add(figure)
        }
    }
    return figures
}

private fun calcFencingPrice(input: List<String>): Int {
    val figures = findFigures(input)
    return figures.sumOf { it.fencingPrice }
}

fun main() {
    check(calcFencingPrice(listOf("AAAA", "BBCD", "BBCC", "EEEC")) == 140)
    val testInput = readInput("Day12_test")
    check(calcFencingPrice(testInput) == 1930)

    val input = readInput("Day12")
    calcFencingPrice(input).println()
}
