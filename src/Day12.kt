private fun Coordinate.left() = Coordinate(first - 1, second)
private fun Coordinate.right() = Coordinate(first + 1, second)
private fun Coordinate.top() = Coordinate(first, second - 1)
private fun Coordinate.bottom() = Coordinate(first, second + 1)

private fun Coordinate.getSameTypeNeighbours(matrix: List<String>): List<Coordinate> {
    val x = this.first
    val y = this.second
    val value = matrix[y][x]
    val border = matrix.size - 1
    val neighbours = mutableListOf<Coordinate>()
    if (x > 0) neighbours.add(left())
    if (x < border) neighbours.add(right())
    if (y > 0) neighbours.add(top())
    if (y < border) neighbours.add(bottom())
    return neighbours.filter { matrix[it.second][it.first] == value }
}

private class Figure private constructor(val type: Char) {
    val pieces = mutableSetOf<Coordinate>()
    val perimeter
        get() = calculatedPerimeter
    val square: Int
        get() = pieces.size
    val fencingPrice: Int
        get() = square * perimeter
    val discount by lazy { calcDiscount() }
    val sides: Int
        get() = perimeter - discount
    val discountedFencingPrice: Int
        get() = square * sides
    private var calculatedPerimeter = 0

    private fun calcDiscount(): Int {
        var discount = 0
        val visited = mutableMapOf<Coordinate, Boolean>()
        pieces.forEach { piece ->
            visited[piece] = true
            listOf(piece.left(), piece.right()).forEach { neighbour ->
                if (neighbour in pieces && visited[neighbour] != true) {
                    if (piece.top() !in pieces && neighbour.top() !in pieces) discount++ // check tops
                    if (piece.bottom() !in pieces && neighbour.bottom() !in pieces) discount++ // check bots
                }
            }
            listOf(piece.top(), piece.bottom()).forEach { neighbour ->
                if (neighbour in pieces && visited[neighbour] != true) {
                    if (piece.left() !in pieces && neighbour.left() !in pieces) discount++ // check lefts
                    if (piece.right() !in pieces && neighbour.right() !in pieces) discount++ // check rights
                }
            }
        }
        return discount
    }

    companion object {
        fun build(square: Coordinate, matrix: List<String>, visited: Array<BooleanArray>): Figure {
            val figure = Figure(type = matrix[square.second][square.first])
            fun addPieceAndCalcPerimeter(square: Coordinate) {
                figure.pieces.add(square)
                visited[square.second][square.first] = true
                val sameTypeNeighbours = square.getSameTypeNeighbours(matrix)
                figure.calculatedPerimeter += 4 - sameTypeNeighbours.size
                sameTypeNeighbours.forEach { neighbour ->
                    if (visited[neighbour.second][neighbour.first]) return@forEach
                    addPieceAndCalcPerimeter(neighbour)
                }
            }
            addPieceAndCalcPerimeter(square)
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

// Part 2
private fun calcDiscountedPrice(input: List<String>): Int {
    val figures = findFigures(input)
    // figures.forEach { println("type = ${it.type} square = ${it.square} perimeter = ${it.perimeter} discount = ${it.discount} sides = ${it.sides}") }
    return figures.sumOf { it.discountedFencingPrice }
}

fun main() {
    val simpleTestInput = listOf("AAAA", "BBCD", "BBCC", "EEEC")
    check(calcFencingPrice(simpleTestInput) == 140)
    val testInput = readInput("Day12_test")
    check(calcFencingPrice(testInput) == 1930)
    val input = readInput("Day12")
    calcFencingPrice(input).println()

    // Part 2
    check(calcDiscountedPrice(simpleTestInput) == 80)
    check(calcDiscountedPrice(listOf( "EEEEE", "EXXXX", "EEEEE", "EXXXX", "EEEEE")) == 236)
    check(calcDiscountedPrice(testInput) == 1206)
    calcDiscountedPrice(input).println()
}
