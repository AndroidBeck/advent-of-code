import Direction.*

private const val EMPTY = '.'
private const val START = 'S'
private const val END = 'E'
private val passable = setOf(EMPTY, END)

fun minScore(matrix: List<String>, p2: Boolean = false, debug: Boolean = false): Int {
    val n = matrix.size
    val scores = Array(n) { IntArray(n) { Int.MAX_VALUE } }
    val (start, finish) = listOf(Coordinate(1, n - 2), Coordinate(n - 2, 1))
    val bestTiles = mutableSetOf(start, finish)
    val directionsOnFinish = mutableMapOf<Direction, Int>()
    Direction.entries.forEach { directionsOnFinish[it] = Int.MAX_VALUE }

    fun dfs(c: Coordinate, dir: Direction, score: Int): Int {
        if (score < c.getValueIn(scores)) scores[c.y()][c.x()] = score else return score
        if (c.getValueIn(matrix) == END) directionsOnFinish[dir]= minOf(directionsOnFinish[dir]!!, score)
        else {
            c.moveInDir(dir).let { if (it.getValueIn(matrix) in passable) dfs(it, dir, score + 1) }
            c.moveInDir(dir.turnLeft()).let { if (it.getValueIn(matrix) in passable) dfs(it, dir.turnLeft(), score + 1001) }
            c.moveInDir(dir.turnRight()).let { if (it.getValueIn(matrix) in passable) dfs(it, dir.turnRight(), score + 1001) }
        }
        return score
    }

    dfs(start, EAST, 0)
    val minScore = finish.getValueIn(scores)
    if (!p2) return minScore

    fun dfsBack(c: Coordinate, values: Array<IntArray>, direction: Direction) {
        bestTiles.add(c)
        val currValue = c.getValueIn(values)
        if (currValue == 0) return
        val possibleDirections = listOf(direction, direction.turnLeft(), direction.turnRight())
        val minNextValue = possibleDirections.minOf { c.moveInDir(it).getValueIn(values) }
        // if (debug) printDfsDebugInfo(c, currValue, direction, minNextValue)
        possibleDirections.forEach { dir ->
            val nextC = c.moveInDir(dir)
            val nextValue = nextC.getValueIn(values)
            if (nextValue == minNextValue) dfsBack(nextC, values, dir)
            if (nextValue == minNextValue + 1000 && getDirection(from = c, to = nextC) == direction) dfsBack(nextC, values, dir)
        }
    }

    directionsOnFinish.entries.forEach { if (it.value == minScore) dfsBack(finish, scores, it.key.opposite()) }
    if (debug) printDebugInfo(matrix, scores, bestTiles)
    return bestTiles.size
}

private fun printDfsDebugInfo(c: Coordinate, currValue: Int, direction: Direction, minNextValue: Int) {
    "dfsBack: c = $c value = $currValue direction = $direction minNexValue = $minNextValue".println()
}

private fun printDebugInfo(matrix: List<String>, scores: Array<IntArray>, bestTiles: Set<Coordinate>) {
    scores.forEach { it.joinToString("\t").println() }
    "\n\n".println()
    matrix.forEachIndexed { i, line ->
        val builder = StringBuilder()
        line.forEachIndexed { j, c ->
            val symbol = if (j to i in bestTiles) 'O' else c
            builder.append(symbol)
            builder.append(' ')
        }
        builder.println()
    }
}

fun main() {
    val smallTestInput = readInput("Day16_small_test")
    val testInput = readInput("Day16_test")
    val input = readInput("Day16")
    check(minScore(smallTestInput) == 7036)
    check(minScore(testInput) == 11048)
    minScore(input).println() // 127520

    check(minScore(smallTestInput, p2 = true) == 45)
    check(minScore(testInput, p2 = true) == 64)
    minScore(input, p2 = true, debug = true).println() // 565
}
