import java.io.File
import kotlin.math.abs

private data class MyPoint(val x: Int, val y: Int)

fun main() {
    val points = File("src/C25_Day09.txt")
        .readLines()
        .mapNotNull { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return@mapNotNull null
            val parts = trimmed.split(',')
            require(parts.size == 2) { "Invalid line: $line" }
            val x = parts[0].toInt()
            val y = parts[1].toInt()
            MyPoint(x, y)
        }

    if (points.size < 2) {
        println(0)
        return
    }

    var maxArea = 0L

    for (i in 0 until points.size) {
        val p1 = points[i]
        for (j in i + 1 until points.size) {
            val p2 = points[j]
            val width = abs(p1.x - p2.x).toLong() + 1L
            val height = abs(p1.y - p2.y).toLong() + 1L
            val area = width * height
            if (area > maxArea) {
                maxArea = area
            }
        }
    }

    println(maxArea)
}
