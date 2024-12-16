import Direction.*
import java.util.ArrayDeque

private const val EMPTY = '.'
// private const val START = 'S'
private const val END = 'E'
private val passable = setOf(EMPTY, END)

fun minScore(matrix: List<String>, p2: Boolean = false): Int {
    val n = matrix.size
    val scores = Array(n) { IntArray(n) { Int.MAX_VALUE } }
    val depths = Array(n) { IntArray(n) { Int.MAX_VALUE } }
    val (start, finish) = listOf(Coordinate(1, n - 2), Coordinate(n - 2, 1))
    val directionsOnFinish = mutableListOf<Direction>()
    fun dfs(c: Coordinate, dir: Direction, score: Int, depth: Int): Int {
        if (depth < c.getValueIn(depths)) depths[c.y()][c.x()] = depth
        if (score < c.getValueIn(scores)) scores[c.y()][c.x()] = score else return score
        if (c.getValueIn(matrix) == END) {
            directionsOnFinish.add(dir)
            return score
        }
        c.moveInDir(dir).let { if (it.getValueIn(matrix) in passable) dfs(it, dir, score + 1, depth + 1) }
        c.moveInDir(dir.turnLeft()).let { if (it.getValueIn(matrix) in passable) dfs(it, dir.turnLeft(), score + 1001, depth + 1) }
        c.moveInDir(dir.turnRight()).let { if (it.getValueIn(matrix) in passable) dfs(it, dir.turnRight(), score + 1001, depth + 1) }
        return score
    }
    dfs(start, EAST, 0, 0)
    val minScore = finish.getValueIn(scores)
    if (!p2) return minScore

    val bestTiles = mutableSetOf(start, finish)
    val deque = ArrayDeque<Pair<Coordinate, Direction>>()
    fun dfsBack(c: Coordinate, values: Array<IntArray>, direction: Direction) {
        bestTiles.add(c)
        val currValue = c.getValueIn(values)
        if (currValue == 0) return
        val possibleDirections = listOf(direction, direction.turnLeft(), direction.turnRight())
        val minNextValue = possibleDirections.minOf { c.moveInDir(it).getValueIn(values) }
//        "dfsBack: c = $c value = $currValue direction = $direction minNexValue = $minNextValue".println()
        possibleDirections.forEach { dir ->
            val nextC = c.moveInDir(dir)
            val nextValue = nextC.getValueIn(values)
            if (nextValue == minNextValue + 1000 && getDirection(from = c, to = nextC) == direction) {
                deque.add(nextC to dir)
                dfsBack(nextC, values, dir)
            }
            if (nextValue == minNextValue) dfsBack(nextC, values, dir)
        }
    }
    directionsOnFinish.forEach { dfsBack(finish, scores, it.opposite()) }
    printDebugInfo(matrix, scores, bestTiles, depths, deque)

    return bestTiles.size
}

private fun printDebugInfo(matrix: List<String>, scores: Array<IntArray>, bestTiles: Set<Coordinate>, depths: Array<IntArray>, deque: ArrayDeque<Pair<Coordinate, Direction>>) {
    scores.forEach { it.joinToString("\t").println() }
    "\n\n".println()
//    depths.forEach { it.joinToString("\t").println() }
//    "\n\n".println()
    matrix.forEachIndexed { i, line ->
        val builder = StringBuilder(line)
        builder.forEachIndexed { j, c ->
            if (Coordinate(j, i) in bestTiles) builder[j] = 'O'
        }
        builder.println()
    }
    "deque size = ${deque.size}".println()
    deque. forEachIndexed { i, it -> "i = $i deque = ${it.first} ${it.second}".println() }
}

fun main() {
    val smallTestInput = readInput("Day16_small_test")
    val testInput = readInput("Day16_test")
    val input = readInput("Day16")

//    check(minScore(smallTestInput) == 7036)
//    check(minScore(testInput) == 11048)
//    minScore(input).println() // 127520

//    check(minScore(smallTestInput, p2 = true) == 45)
//    check(minScore(testInput, p2 = true) == 64)
    minScore(input, p2 = true).println() // 526 - to low
}
