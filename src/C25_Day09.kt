import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class MyPoint(val x: Int, val y: Int)

fun main() {
    val points = File("src/C25_Day09.txt")
        .readLines()
        .mapNotNull { line ->
            val t = line.trim()
            if (t.isEmpty()) null
            else {
                val parts = t.split(',')
                require(parts.size == 2) { "Invalid line: $line" }
                MyPoint(parts[0].toInt(), parts[1].toInt())
            }
        }

    if (points.size < 2) {
        println("Part 1: 0")
        println("Part 2: 0")
        return
    }

    val part1 = maxRectangleAnyTiles(points)
    val part2 = maxRectangleRedGreenCompressed(points)

    println("Part 1: $part1")
    println("Part 2: $part2")
}

/**
 * Part 1: unrestricted interior, just any two red tiles as opposite corners.
 */
fun maxRectangleAnyTiles(points: List<MyPoint>): Long {
    var best = 0L
    for (i in points.indices) {
        val p1 = points[i]
        for (j in i + 1 until points.size) {
            val p2 = points[j]
            val width = abs(p1.x - p2.x).toLong() + 1
            val height = abs(p1.y - p2.y).toLong() + 1
            val area = width * height
            if (area > best) best = area
        }
    }
    return best
}

/**
 * Part 2: rectangle must lie entirely inside the polygon formed by
 * the red tiles (including its boundary), where interior + edges are green.
 *
 * We:
 *  1) Coordinate-compress all x and y coordinates.
 *  2) Build a small grid of "cells" between unique x and y values.
 *  3) For each cell, test its center point with point-in-polygon.
 *  4) Build a 2D prefix sum for fast "all cells inside?" queries.
 *  5) For each pair of red points, check if the interior of the
 *     rectangle is fully inside the polygon.
 */
fun maxRectangleRedGreenCompressed(points: List<MyPoint>): Long {
    val n = points.size

    // --- Coordinate compression ---
    val xs = points.map { it.x }.toSortedSet().toList()
    val ys = points.map { it.y }.toSortedSet().toList()
    val nx = xs.size
    val ny = ys.size

    // Maps from original coordinate -> compressed index
    val xIndex = HashMap<Int, Int>(nx)
    val yIndex = HashMap<Int, Int>(ny)
    for ((idx, x) in xs.withIndex()) xIndex[x] = idx
    for ((idx, y) in ys.withIndex()) yIndex[y] = idx

    // --- Build inside[][]: whether each compressed cell is inside the polygon ---
    // Cell (ix, iy) covers [xs[ix], xs[ix+1]] x [ys[iy], ys[iy+1]]
    // We'll test at the center of that cell.
    val inside = Array(ny - 1) { BooleanArray(nx - 1) }
    for (iy in 0 until ny - 1) {
        val yMid = (ys[iy].toDouble() + ys[iy + 1].toDouble()) / 2.0
        for (ix in 0 until nx - 1) {
            val xMid = (xs[ix].toDouble() + xs[ix + 1].toDouble()) / 2.0
            if (pointInPolygon(xMid, yMid, points)) {
                inside[iy][ix] = true
            }
        }
    }

    // --- Build 2D prefix sum over inside[] ---
    // pref[iy][ix] = number of "inside" cells in rectangle of cells
    // from (0,0) to (ix-1, iy-1) inclusive in cell coordinates.
    val pref = Array(ny) { IntArray(nx) }
    for (iy in 0 until ny - 1) {
        var rowSum = 0
        for (ix in 0 until nx - 1) {
            if (inside[iy][ix]) rowSum++
            pref[iy + 1][ix + 1] = pref[iy][ix + 1] + rowSum
        }
    }

    fun sumCells(ix1: Int, iy1: Int, ix2: Int, iy2: Int): Int {
        // returns count of inside cells in block:
        // ix in [ix1, ix2-1], iy in [iy1, iy2-1]
        return pref[iy2][ix2] - pref[iy1][ix2] - pref[iy2][ix1] + pref[iy1][ix1]
    }

    var bestArea = 0L

    // --- Try all pairs of red points as opposite corners ---
    for (i in 0 until n) {
        val p1 = points[i]
        for (j in i + 1 until n) {
            val p2 = points[j]

            val width = abs(p1.x - p2.x).toLong() + 1
            val height = abs(p1.y - p2.y).toLong() + 1
            val area = width * height

            if (area <= bestArea) continue // can't beat current best

            // Degenerate rectangles (width or height = 1) won't be max.
            if (p1.x == p2.x || p1.y == p2.y) continue

            val xa = min(p1.x, p2.x)
            val xb = max(p1.x, p2.x)
            val ya = min(p1.y, p2.y)
            val yb = max(p1.y, p2.y)

            val ix1 = xIndex[xa] ?: continue
            val ix2 = xIndex[xb] ?: continue
            val iy1 = yIndex[ya] ?: continue
            val iy2 = yIndex[yb] ?: continue

            val cellsX = ix2 - ix1
            val cellsY = iy2 - iy1
            if (cellsX <= 0 || cellsY <= 0) continue

            val totalCells = cellsX * cellsY
            val insideCells = sumCells(ix1, iy1, ix2, iy2)

            if (insideCells == totalCells) {
                // interior of the rectangle is fully inside the polygon
                bestArea = area
            }
        }
    }

    return bestArea
}

/**
 * Standard even-odd point-in-polygon test (works for orthogonal polygons too).
 * Counts points on the "left/bottom" edges as inside, "top/right" as outside,
 * which is fine for our use (we only test cell centers, never exactly on edges).
 */
fun pointInPolygon(px: Double, py: Double, poly: List<MyPoint>): Boolean {
    var inside = false
    val n = poly.size
    for (i in 0 until n) {
        val x1 = poly[i].x.toDouble()
        val y1 = poly[i].y.toDouble()
        val x2 = poly[(i + 1) % n].x.toDouble()
        val y2 = poly[(i + 1) % n].y.toDouble()

        val cond = (y1 <= py && py < y2) || (y2 <= py && py < y1)
        if (cond) {
            val t = (py - y1) / (y2 - y1)
            val xIntersect = x1 + (x2 - x1) * t
            if (xIntersect > px) {
                inside = !inside
            }
        }
    }
    return inside
}
