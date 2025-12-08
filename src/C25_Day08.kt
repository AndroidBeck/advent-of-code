import java.io.File
import java.util.PriorityQueue

data class Point(val x: Long, val y: Long, val z: Long)

// For priority queue and Kruskal
data class Edge(val dist2: Double, val a: Int, val b: Int)

class DSU(n: Int) {
    val parent = IntArray(n) { it }
    val size = IntArray(n) { 1 }

    fun find(x: Int): Int {
        var v = x
        while (parent[v] != v) {
            parent[v] = parent[parent[v]] // path compression
            v = parent[v]
        }
        return v
    }

    fun union(a: Int, b: Int): Boolean {
        var x = find(a)
        var y = find(b)
        if (x == y) return false
        if (size[x] < size[y]) {
            val tmp = x
            x = y
            y = tmp
        }
        parent[y] = x
        size[x] += size[y]
        return true
    }
}

fun readPoints(path: String): List<Point> {
    val file = File(path)
    if (!file.exists()) {
        error("Input file $path not found")
    }
    val lines = file.readLines().filter { it.isNotBlank() }
    val pts = ArrayList<Point>(lines.size)
    for (line in lines) {
        val parts = line.split(",")
        require(parts.size == 3) { "Invalid line: '$line'" }
        val x = parts[0].trim().toLong()
        val y = parts[1].trim().toLong()
        val z = parts[2].trim().toLong()
        pts.add(Point(x, y, z))
    }
    return pts
}

/**
 * Part 1:
 * Connect the 1000 closest pairs, then multiply sizes of the three largest circuits.
 */
fun solvePart1(points: List<Point>, kPairs: Int = 1000): Long {
    val n = points.size
    if (n == 0) return 0L

    // Max-heap by distance squared: we keep only the kPairs smallest distances
    val heap = PriorityQueue<Edge>(compareByDescending<Edge> { it.dist2 })

    for (i in 0 until n) {
        val pi = points[i]
        for (j in i + 1 until n) {
            val pj = points[j]
            val dx = (pi.x - pj.x).toDouble()
            val dy = (pi.y - pj.y).toDouble()
            val dz = (pi.z - pj.z).toDouble()
            val dist2 = dx * dx + dy * dy + dz * dz

            if (heap.size < kPairs) {
                heap.add(Edge(dist2, i, j))
            } else if (dist2 < heap.peek().dist2) {
                heap.poll()
                heap.add(Edge(dist2, i, j))
            }
        }
    }

    val edges = mutableListOf<Edge>()
    while (heap.isNotEmpty()) {
        edges.add(heap.poll())
    }
    edges.sortBy { it.dist2 } // from shortest to longest

    val dsu = DSU(n)
    for (e in edges) {
        dsu.union(e.a, e.b)
    }

    val sizes = mutableListOf<Int>()
    for (i in 0 until n) {
        if (dsu.parent[i] == i) {
            sizes.add(dsu.size[i])
        }
    }
    sizes.sortDescending()

    val take = minOf(3, sizes.size)
    var result = 1L
    for (i in 0 until take) {
        result *= sizes[i].toLong()
    }
    return result
}

/**
 * Part 2:
 * Keep connecting the closest *unconnected* pairs until all junction boxes
 * are in one circuit. Return the product of the X coordinates of the last
 * pair that actually merges two components.
 *
 * This is essentially Kruskal's algorithm on the complete graph.
 */
fun solvePart2(points: List<Point>): Long {
    val n = points.size
    if (n <= 1) return 0L

    // Build all edges (complete graph); assume n is small enough for O(n^2).
    val edges = ArrayList<Edge>()
    for (i in 0 until n) {
        val pi = points[i]
        for (j in i + 1 until n) {
            val pj = points[j]
            val dx = (pi.x - pj.x).toDouble()
            val dy = (pi.y - pj.y).toDouble()
            val dz = (pi.z - pj.z).toDouble()
            val dist2 = dx * dx + dy * dy + dz * dz
            edges.add(Edge(dist2, i, j))
        }
    }

    // Sort edges by increasing distance
    edges.sortBy { it.dist2 }

    val dsu = DSU(n)
    var components = n
    var lastEdge: Edge? = null

    for (e in edges) {
        if (dsu.union(e.a, e.b)) {
            components--
            lastEdge = e
            if (components == 1) break
        }
    }

    if (lastEdge == null) return 0L

    val a = lastEdge.a
    val b = lastEdge.b
    val xa = points[a].x
    val xb = points[b].x
    return xa * xb
}

fun main() {
    val points = readPoints("src/C25_Day08.txt")

    val part1 = solvePart1(points)
    println(part1)

    val part2 = solvePart2(points)
    println(part2)
}
