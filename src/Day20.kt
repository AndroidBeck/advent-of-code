private const val EMPTY = '.'
private const val BARRIER = '#'
private const val START = 'S'
private const val END = 'E'

fun calcCheatsSavingPs(matrix: List<String>, needSave: Int = 100): Int {
    val n = matrix.size
    val scores = Array(n) { IntArray(n) { Int.MAX_VALUE } }
    var start = Coordinate (1, 1)
    var finish = Coordinate(n -1, n - 1)
    matrix.forEachIndexed { i, line ->
        line.forEachIndexed { j, c ->
            when (c) {
                BARRIER -> scores[i][j] = -1
                START -> start = Coordinate(j, i)
                END -> finish = Coordinate(j, i)
            }
        }
    }
    fun Coordinate.isBarrier() = scores.get(this) == -1
    val dfs = DeepRecursiveFunction<Pair<Coordinate, Int>, Unit> { (c, steps) ->
        if (scores.get(c) > steps) scores.set(c, steps) else return@DeepRecursiveFunction
        if (c == finish) return@DeepRecursiveFunction
        c.getNeighbours().forEach { if (it.inBorders(n - 1) && !it.isBarrier()) callRecursive(Pair(it, steps + 1)) }
    }
    dfs(Pair(start, 0))
//    scores.forEach { it.joinToString(" ").println() }

    val savedOnCheat = mutableMapOf<Int, Int>()
    val dfs2 = DeepRecursiveFunction<Pair<Coordinate, Int>, Unit> { (c, steps) ->
        if (steps > scores.get(c)) return@DeepRecursiveFunction
        if (c == finish) return@DeepRecursiveFunction
        c.getNeighbours().forEach { n1 ->
            if (n1.isBarrier()) {
                val n2 = n1.moveInDir(getDirection(from = c, to = n1))
//                "c = $c, n1 = $n1, n2 = $n2".println()
                if (n2.inBorders(n - 1)) {
                    val delta = scores.get(n2) - (steps + 2)
                    if (delta > 0) savedOnCheat[delta] = savedOnCheat.computeIfAbsent(delta) { 0 }.plus(1)
                }
            } else if (n1.inBorders(n - 1)) callRecursive(Pair(n1, steps + 1))
        }
    }
    dfs2(Pair(start, 0))
//    savedOnCheat.keys.sorted().forEach { "$it -> ${savedOnCheat[it]}".println() }
    var acc = 0
    savedOnCheat.entries.forEach { (key, value) -> if (key >= needSave) acc += value }
    return acc
}

fun main() {
    val testInput = readInput("Day20_test")
    val input = readInput("Day20")
    check(calcCheatsSavingPs(testInput, needSave = 64) == 1)
    calcCheatsSavingPs(input).println() //
}
