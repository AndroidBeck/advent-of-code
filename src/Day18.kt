fun minStepsNumber(input: List<String>, fallen: Int, xMax: Int = 70, yMax: Int = 70, p2: Boolean = false, debug: Boolean = false): Int {
    val corruptBlocks = input.map { it.split(",").map { s -> s.toInt() } }.map { l -> Coordinate(l[0], l[1]) }
    val corruptMap = mutableMapOf<Coordinate, Int>() // coord -> time of corruption
    val isSafeMatrix = Array(yMax + 1) { BooleanArray(xMax + 1)  { true } }
    val steps = Array(yMax + 1) { IntArray(xMax + 1) { Int.MAX_VALUE } }

    fun Coordinate.isSafe() = x() in 0..xMax && y() in 0..yMax && isSafeMatrix.get(this)

    val start = Coordinate(0, 0)
    val finish = Coordinate(xMax, yMax)
    val path = mutableListOf<Coordinate>()

    fun dfs(c: Coordinate, step: Int) {
        if (step < steps.get(c)) steps.set(c, step) else return
        if (c != finish) c.getNeighbours().forEach { neighbourC -> if (neighbourC.isSafe()) dfs(neighbourC,  step + 1) } else return
    }

    if (!p2) {
        for (i in 0..< fallen) {
            val c = corruptBlocks[i]
            corruptMap[c] = i
            isSafeMatrix.set(c, false)
        }
        dfs(start, 0)
        return steps.get(finish)
    }

    corruptBlocks.forEachIndexed { i, c ->
        corruptMap[c] = i
        if (i < c.sumXY()) isSafeMatrix.set(c, false)
    }

    fun dfsBack(c: Coordinate) {
        path.add(c)
        if (c == start) return
        val possibleC = c.getNeighbours().filter { it.isSafe() }
        val minValue = possibleC.minOf { it.getValueIn(steps) }
        val next = possibleC.first { it.getValueIn(steps) == minValue }
        dfsBack(next)
    }

    var iterations = 0
    repeat(corruptBlocks.size) {
        dfs(start, 0)
        dfsBack(finish)
        val pathSet = path.toSet()
        val corruptBlocksOnPath = pathSet.intersect(corruptMap.keys)
        corruptBlocksOnPath.forEach { c ->
            val time = corruptMap[c]!!
            val step = steps.get(c)
            if (step >= time - 1) { // time + 1 ???
                iterations++
                isSafeMatrix.set(c, false)
                path.clear()
                for (i in 0.. yMax) {
                    for (j in 0.. xMax) steps[i][j] = Int.MAX_VALUE
                }
                return@repeat
            }
        }
        if (debug) printDebugInfo(isSafeMatrix, corruptMap, steps, path, iterations)
        return path.size
    }

    println("No way out!")
    return -1
}

private fun printDebugInfo(isSafeMatrix: Array<BooleanArray>, corruptMap: Map<Coordinate, Int>, steps: Array<IntArray>, path: List<Coordinate>, iterations: Int) {
    val n = isSafeMatrix.size
    "Corrupt block falls:".println()
    for (i in 0.. n - 1) {
        val builder = StringBuilder()
        for (j in 0 .. n - 1) {
            val c = Coordinate(j, i)
            val symbol = corruptMap[c] ?: '.'
            builder.append("$symbol ")
        }
        builder.println()
    }
    "Steps:".println()
    steps.forEach { nums -> nums.joinToString("\t").println() }
    "Path:".println()
    val pathSet = path.toSet()
    for (i in 0.. n - 1) {
        val builder = StringBuilder()
        for (j in 0 .. n - 1) {
            val c = Coordinate(j, i)
            val symbol = if (!isSafeMatrix[i][j]) '#'
            else if (c in pathSet) {
                if (c !in corruptMap.keys) '+' else corruptMap[c]!! //'?'
            }
            else if (c in corruptMap.keys) 'w'
            else '.'
            builder.append(symbol).append(' ')
        }
        builder.println()
    }
    "steps = ${path.size} iterations = $iterations".println()
}

fun main() {
    val testInput = readInput("Day18_test")
    val input = readInput("Day18")
    check(minStepsNumber(testInput, xMax = 6, yMax = 6, fallen = 12) == 22)
    minStepsNumber(input, fallen = 1024).println()
}
