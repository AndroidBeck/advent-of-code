import Direction.*

private const val EMPTY = '.'
// private const val START = 'S'
 private const val END = 'E'
private val passable = setOf(EMPTY, END)

fun minScore(matrix: List<String>): Int {
    val n = matrix.size
    val scores = Array(n) { IntArray(n) { Int.MAX_VALUE } }
    val (start, finish) = listOf(Coordinate(1, n - 2), Coordinate(n - 2, 1))
    fun dfs(c: Coordinate, dir: Direction, score: Int): Int {
        if (score < c.getValueIn(scores)) scores[c.y()][c.x()] = score else return score
        if (c.getValueIn(matrix) == END) return score
        c.moveInDir(dir).let { if (it.getValueIn(matrix) in passable) dfs(it, dir, score + 1) }
        c.moveInDir(dir.turnLeft()).let { if (it.getValueIn(matrix) in passable) dfs(it, dir.turnLeft(), score + 1001) }
        c.moveInDir(dir.turnRight()).let { if (it.getValueIn(matrix) in passable) dfs(it, dir.turnRight(), score + 1001) }
        return score
    }
    dfs(start, EAST, 0)
    return finish.getValueIn(scores)
}

private fun debugPrint(matrix: List<String>, scores: Array<IntArray>) {
    matrix.forEach { it.println() }
    scores.forEach { it.joinToString(" ").println() }
}

fun main() {
    val smallTestInput = readInput("Day16_small_test")
    val testInput = readInput("Day16_test")
    val input = readInput("Day16")
    check(minScore(smallTestInput) == 7036)
    check(minScore(testInput) == 11048)
    minScore(input).println()
}
