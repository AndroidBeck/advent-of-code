data class Robot(var px: Int, var py: Int, var vx: Int, var vy: Int) {
    fun move(t: Int, xMax: Int, yMax: Int, xMin: Int = 0, yMin: Int = 0) {
        px = ((px + vx * t) % xMax + xMax) % xMax
        py = ((py + vy * t) % yMax + yMax) % yMax
    }
}

fun quadrantSafety(input: List<String>, time: Int = 100, xMax: Int = 101, yMax: Int = 103): Int {
    val robots = parseInput(input)
    robots.println()
    var quadrants = IntArray(4)
    val xBorder = xMax / 2
    val yBorder = yMax / 2
//    val matrix = Array(yMax) { IntArray(xMax) }
    robots.forEach {
        it.move(time, xMax, yMax)
        if (it.px < xBorder && it.py < yBorder) quadrants[0]++
        if (it.px > xBorder && it.py < yBorder) quadrants[1]++
        if (it.px < xBorder && it.py > yBorder) quadrants[2]++
        if (it.px > xBorder && it.py > yBorder) quadrants[3]++
//        matrix[it.py][it.px]++
    }
//    matrix.print()
    var result = 1
    quadrants.forEachIndexed { i, it ->
        result *= it
//        "quadrant $i: $it"
    }
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

fun main() {
    val testInput = readInput("Day14_test")
    val input = readInput("Day14")
    check(quadrantSafety(testInput, xMax = 11, yMax = 7) == 12)
    quadrantSafety(input).println()
}
