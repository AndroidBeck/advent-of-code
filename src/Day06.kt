enum class Direction {
    NORTH, SOUTH, EAST, WEST;
    fun turnRight() = when(this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }
}

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

// Part 2
private fun List<CharArray>.findAllSymbols(symbol: Char): List<Coordinate> {
    val places = mutableListOf<Coordinate>()
    this.forEachIndexed { y, line ->
        line.forEachIndexed { x, c -> if (c == symbol) places.add(Coordinate(x, y)) }
    }
    return places
}

private fun isCycled(startXY: Coordinate, obstacles: Set<Coordinate>, n: Int, startDir: Direction): Boolean {
    var (x, y) = startXY
    var direction = startDir
    val visitedObst = mutableMapOf<Coordinate, MutableSet<Direction>>()
    // Simulate
    while(true) {
        val next = moveInDirection(direction, x, y)
        val (nextX, nextY) = next
        if (nextX < 0 || nextX > n - 1 || nextY < 0 || nextY > n - 1) {
            break
        } else if (next in obstacles) {
            val added = visitedObst.computeIfAbsent(next) { mutableSetOf() }.add(direction)
            if (!added) return true
            direction = direction.turnRight()
        } else {
            x = nextX
            y = nextY
        }
    }
    return false
}

fun calcPlacesForObstacle(tiles: List<CharArray>): Int {
    val n = tiles.size
    var placesForObst = 0
    val startXY = findSymbol(tiles, '^')
    val direction = Direction.NORTH

    calcVisitedTiles(tiles)
    val possiblePlaces = tiles.findAllSymbols('X')
    val obstacles = tiles.findAllSymbols('#').toMutableSet()
    possiblePlaces.forEach { place ->
        obstacles.add(place)
        if (isCycled(startXY, obstacles, n, direction)) placesForObst++
        obstacles.remove(place)
    }
    return placesForObst
}

fun main() {
    val testInput = readInput("Day06_test").map { it.toCharArray() } // 10 x 10
    check(calcVisitedTiles(testInput) == 41)

    val input = readInput("Day06").map { it.toCharArray() } // 130 x 130
    calcVisitedTiles(input).println()

    // Part 2 (inputs are changed here)
    val testInput2 = readInput("Day06_test").map { it.toCharArray() } // 10 x 10
    check(calcPlacesForObstacle(testInput2) == 6)
    val input2 = readInput("Day06").map { it.toCharArray() } // 130 x 130
    calcPlacesForObstacle(input2).println()
}
