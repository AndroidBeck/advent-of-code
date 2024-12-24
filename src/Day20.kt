private const val EMPTY = '.'
private const val BARRIER = '#'
private const val START = 'S'
private const val END = 'E'

fun calcCheatsSavingPs(matrix: List<String>, needSave: Int = 100, cheatLength: Int = 2, debug: Boolean = false): Int {
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
    val cheatsQueue = ArrayDeque <Triple<Coordinate, Int, Int>>()

    val dfs = DeepRecursiveFunction<Pair<Coordinate, Int>, Unit> { (c, steps) ->
        if (scores.get(c) > steps) scores.set(c, steps) else return@DeepRecursiveFunction
        if (c == finish) return@DeepRecursiveFunction
        cheatsQueue.add(Triple(c, steps, cheatLength))
        c.getNeighbours().forEach {
            if (!it.isBarrier()) callRecursive(Pair(it, steps + 1))
        }
    }

    dfs(Pair(start, 0))
    if (debug) scores.forEach { it.joinToString(" ").println() }

    var acc = 0
    val maxScore = scores.get(finish)
    val maxMomentToFinish = maxScore - needSave
    val visitedCheatLeft = Array(n) { IntArray(n) { 0 } }
    val successC = mutableSetOf<Coordinate>()

    val barrierDfs = DeepRecursiveFunction<Triple<Coordinate, Int, Int>, Unit> { (c, steps, cheatTimeLeft) ->
        if (steps > maxMomentToFinish || cheatTimeLeft <= visitedCheatLeft.get(c)) return@DeepRecursiveFunction
        visitedCheatLeft.set(c, cheatTimeLeft)
        if (scores.get(c) - steps >= needSave && c !in successC) {
            acc++
            successC.add(c)
        }
        c.getNeighbours().forEach { neighbour ->
            if (neighbour.inBorders(n - 1)) callRecursive(Triple(neighbour, steps + 1, cheatTimeLeft - 1))
        }
    }

    while (cheatsQueue.isNotEmpty()) {
        val (c, steps, cheatsLeft) = cheatsQueue.removeLastOrNull()!!
        barrierDfs(Triple(c, steps, cheatsLeft + 1))
        visitedCheatLeft.forEach { for (i in it.indices) it[i] = 0 }
        successC.clear()
    }
    return acc
}

fun main() {
    val testInput = readInput("Day20_test")
    val input = readInput("Day20")
    check(calcCheatsSavingPs(testInput, needSave = 64, debug = false) == 1)
    check(calcCheatsSavingPs(input, debug = false) == 1497) // 1497 (Part 1)

    check(calcCheatsSavingPs(testInput, needSave = 76, cheatLength = 20) == 3)
    check(calcCheatsSavingPs(testInput, needSave = 74, cheatLength = 20) == 7)
    check(calcCheatsSavingPs(testInput, needSave = 72, cheatLength = 20) == 29)
    check(calcCheatsSavingPs(testInput, needSave = 50, cheatLength = 20) == 285)
    calcCheatsSavingPs(input, needSave = 100, cheatLength = 20).println() //  1030809
}
