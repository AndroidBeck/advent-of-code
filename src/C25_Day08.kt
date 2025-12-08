import java.io.File
import java.util.PriorityQueue

data class Edge(val dist2: Long, val a: Int, val b: Int)

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

fun main() {
    val file = File("src/C25_Day08.txt")
    if (!file.exists()) {
        System.err.println("Input file src/C25_Day08.txt not found")
        return
    }

    val lines = file.readLines().filter { it.isNotBlank() }
    val n = lines.size
    if (n == 0) {
        println(0)
        return
    }

    val xs = LongArray(n)
    val ys = LongArray(n)
    val zs = LongArray(n)

    for ((idx, line) in lines.withIndex()) {
        val parts = line.split(",")
        require(parts.size == 3) { "Invalid line: '$line'" }
        xs[idx] = parts[0].trim().toLong()
        ys[idx] = parts[1].trim().toLong()
        zs[idx] = parts[2].trim().toLong()
    }

    val K = 1000
    // Max-heap by dist2: head has the largest distance currently kept.
    val heap = PriorityQueue<Edge>(compareByDescending<Edge> { it.dist2 })

    // Scan all pairs, keep only the K smallest distances.
    for (i in 0 until n) {
        val xi = xs[i]
        val yi = ys[i]
        val zi = zs[i]
        for (j in i + 1 until n) {
            val dx = xi - xs[j]
            val dy = yi - ys[j]
            val dz = zi - zs[j]
            val dist2 = dx * dx + dy * dy + dz * dz

            if (heap.size < K) {
                heap.add(Edge(dist2, i, j))
            } else if (dist2 < heap.peek().dist2) {
                heap.poll()
                heap.add(Edge(dist2, i, j))
            }
        }
    }

    // Now heap contains up to K closest pairs (in no particular order).
    val edges = mutableListOf<Edge>()
    while (heap.isNotEmpty()) {
        edges.add(heap.poll())
    }
    // Process them from shortest to longest.
    edges.sortBy { it.dist2 }

    val dsu = DSU(n)
    for (e in edges) {
        // "Connecting" the pair: if they’re already in the same circuit, union does nothing.
        dsu.union(e.a, e.b)
    }

    // Collect sizes of all circuits (connected components).
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

    println(result)
}
