fun minStepsNumber(input: List<String>, fallen: Int, xMax: Int = 70, yMax: Int = 70): Int {
    val corruptBlocks = parseInput(input, fallen)
    return calculateMinSteps(corruptBlocks, xMax, yMax)
}

private fun calculateMinSteps(corruptBlocks: List<Coordinate>, xMax: Int, yMax: Int): Int {
    val corruptMap = mutableMapOf<Coordinate, Int>()
    val isSafeMatrix = Array(yMax + 1) { BooleanArray(xMax + 1) { true } }
    val steps = Array(yMax + 1) { IntArray(xMax + 1) { Int.MAX_VALUE } }
    val start = Coordinate(0, 0)
    val finish = Coordinate(xMax, yMax)

    fun Coordinate.isSafe() = x() in 0..xMax && y() in 0..yMax && isSafeMatrix.get(this)
    fun dfs(c: Coordinate, step: Int) {
        if (step < steps.get(c)) steps.set(c, step) else return
        if (c != finish) c.getNeighbours()
            .forEach { neighbourC -> if (neighbourC.isSafe()) dfs(neighbourC, step + 1) } else return
    }

    corruptBlocks.forEachIndexed { i, c ->
        corruptMap[c] = i
        isSafeMatrix.set(c, false)
    }
    dfs(start, 0)
    return steps.get(finish).takeIf { it != Int.MAX_VALUE } ?: -1
}

private fun parseInput(input: List<String>, fallen: Int): List<Coordinate> {
    return input.take(fallen).map { it.split(",").map { s -> s.toInt() } }.map { l -> Coordinate(l[0], l[1]) }
}

// Part 2
fun firstCoordinateBlockingThePath(input: List<String>, xMax: Int = 70, yMax: Int = 70): String {
    val n = input.size
    val corruptBlocks = parseInput(input, n)
    val ints = List(n) { i -> i }
    val blockNumber = ints.rBinSearch(0, n) { calculateMinSteps(corruptBlocks.take(it), xMax, yMax) != -1 }
    return corruptBlocks[blockNumber].let { "${it.x()},${it.y()}" }
}

fun main() {
    val testInput = readInput("Day18_test")
    val input = readInput("Day18")
    check(minStepsNumber(testInput, xMax = 6, yMax = 6, fallen = 12) == 22)
    minStepsNumber(input, fallen = 1024).println() // 344

    check(minStepsNumber(input, fallen = 3035) == 568)
    check(minStepsNumber(input, fallen = 3036) == -1)
    check(minStepsNumber(input, fallen = 3037) == -1)
    firstCoordinateBlockingThePath(input).println() // 46,18
}
