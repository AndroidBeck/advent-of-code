enum class Direction {
    NORTH, SOUTH, EAST, WEST;
    fun turnRight() = when(this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }
}
fun Direction.isNORTH() = this == Direction.NORTH
fun Direction.isEAST() = this == Direction.EAST
fun Direction.isSOUTH() = this == Direction.SOUTH
fun Direction.isWEST() = this == Direction.WEST

private fun findSymbol(tiles: List<CharArray>, symbol: Char): Pair<Int, Int> {
    tiles.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            if (c == symbol) return Pair(x, y)
        }
    }
    return Pair(-1,-1)
}

private fun moveInDirection(direction: Direction, x: Int, y: Int): Pair<Int, Int> {
    return when(direction) {
        Direction.NORTH -> Pair(x, y - 1)
        Direction.EAST -> Pair(x + 1, y)
        Direction.SOUTH -> Pair(x, y + 1)
        Direction.WEST -> Pair(x - 1, y)
    }
}

fun calcVisitedTiles(tiles: List<CharArray>): Int {
    val n = tiles.size
    var (x ,y) = findSymbol(tiles, '^')
    var direction = Direction.NORTH
    tiles[y][x] = 'X'
    var visitedTiles = 1

    // Simulate
    while(true) {
        val (nextX, nextY) = moveInDirection(direction, x, y)
        if (nextX < 0 || nextX > n - 1 || nextY < 0 || nextY > n - 1) {
            break
        } else if (tiles[nextY][nextX] == '#') {
            direction = direction.turnRight()
        } else {
            x = nextX
            y = nextY
            if (tiles[y][x] == '.') {
                tiles[y][x] = 'X'
                visitedTiles++
            }
        }
    }

    return visitedTiles
}

fun main() {
    val testInput = readInput("Day06_test").map { it.toCharArray() } // 10 x 10
    check(calcVisitedTiles(testInput) == 41)

    val input = readInput("Day06").map { it.toCharArray() } // 130 x 130
    calcVisitedTiles(input).println()
}
