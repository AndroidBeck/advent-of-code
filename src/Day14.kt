data class Robot(var px: Int, var py: Int, var vx: Int, var vy: Int) {
    fun move(t: Int, xMax: Int, yMax: Int, xMin: Int = 0, yMin: Int = 0) {
        px = ((px + vx * t) % xMax + xMax) % xMax
        py = ((py + vy * t) % yMax + yMax) % yMax
    }
}

fun quadrantSafety(input: List<String>, time: Int = 100, xMax: Int = 101, yMax: Int = 103): Int {
    val robots = parseInput(input)
    val quadrants = IntArray(4)
    val xBorder = xMax / 2
    val yBorder = yMax / 2
    robots.forEach {
        it.move(time, xMax, yMax)
        if (it.px < xBorder && it.py < yBorder) quadrants[0]++
        if (it.px > xBorder && it.py < yBorder) quadrants[1]++
        if (it.px < xBorder && it.py > yBorder) quadrants[2]++
        if (it.px > xBorder && it.py > yBorder) quadrants[3]++
    }
    var result = 1
    quadrants.forEach { result *= it }
    return result
}

private fun parseInput(input: List<String>): List<Robot> {
    val robots = mutableListOf<Robot>()
    val regex = Regex("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)")
    input.forEach { line ->
        val regex = regex.find(line)!!
        val px = regex.groupValues[1].toInt()
        val py = regex.groupValues[2].toInt()
        val vx = regex.groupValues[3].toInt()
        val vy = regex.groupValues[4].toInt()
        robots.add(Robot(px, py, vx, vy))
    }
    return robots
}

private fun Array<IntArray>.print() {
    forEach { line -> line.joinToString(" ").println() }
}

private fun Array<IntArray>.visualize() {
    forEach { line ->
        val builder = StringBuilder()
        line.forEach { num ->
            val symbol = if (num == 0) '.' else 'X'
            builder.append(symbol)
        }
        builder.println()
    }
}

// Part 2
fun findEasterEggMoment(input: List<String>, xMax: Int = 101, yMax: Int = 103): Int {
    val robots = parseInput(input)
    val matrix = Array(yMax) { IntArray(xMax) }
    robots.forEach { matrix[it.py][it.px]++ } // default matrix position
    var t = 1
    var maxSq = 0
    while (t < 10000) {
        robots.forEach {
            matrix[it.py][it.px]--
            it.move(1, xMax, yMax)
            matrix[it.py][it.px]++
        }
        val maxRectSq = getMaxRectangleSquare(matrix)
        maxSq = maxSq.coerceAtLeast(maxRectSq)
        if (maxRectSq > 21) {
            matrix.visualize()
            "t = $t maxSquare = $maxRectSq".println()
            return t
        }
        t++
    }
    "Fail.. t = $t maxSq = $maxSq".println()
    return -1
}

fun getMaxRectangleSquare(matrix: Array<IntArray>): Int {
    val n = matrix.size
    val m = matrix[0].size
    val histogramXY = Array(n) { Array(m) { Pair(0, 0) } }
    var maxValue = 0
    matrix.forEachIndexed { i, line ->
        var countX = 0
        line.forEachIndexed { j, num ->
            if (num != 0) {
                countX++
                val countY = if (i > 0) histogramXY[i - 1][j].second + 1 else 0
                histogramXY[i][j] = Pair(countX, countY)
                maxValue = maxValue.coerceAtLeast(countX * countY)
            } else countX = 0
        }
    }
    return maxValue
}

fun main() {
    val testInput = readInput("Day14_test")
    val input = readInput("Day14")
    check(quadrantSafety(testInput, xMax = 11, yMax = 7) == 12)
    quadrantSafety(input).println()

    findEasterEggMoment(input)
}
