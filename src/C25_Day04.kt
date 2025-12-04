import java.io.File
import java.util.ArrayDeque

fun main() {
    val inputPath = "src/C25_Day04.txt"
    val lines = File(inputPath)
        .readLines()
        .filter { it.isNotBlank() }

    val part1 = countAccessibleRolls(lines)
    val part2 = countTotalRemovableRolls(lines)

    println("Part 1: $part1")
    println("Part 2: $part2")
}

// ========== PART 1 (from previous step) ==========
fun countAccessibleRolls(lines: List<String>): Int {
    if (lines.isEmpty()) return 0

    val rows = lines.size
    val cols = lines[0].length
    val grid = lines.map { it.toCharArray() }

    val directions = arrayOf(
        intArrayOf(-1, -1), intArrayOf(-1, 0), intArrayOf(-1, 1),
        intArrayOf(0, -1),                    intArrayOf(0, 1),
        intArrayOf(1, -1),  intArrayOf(1, 0), intArrayOf(1, 1)
    )

    var accessibleCount = 0

    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (grid[r][c] == '@') {
                var neighbors = 0
                for (dir in directions) {
                    val nr = r + dir[0]
                    val nc = c + dir[1]
                    if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == '@') {
                        neighbors++
                    }
                }
                if (neighbors < 4) {
                    accessibleCount++
                }
            }
        }
    }

    return accessibleCount
}

// ========== PART 2 ==========
fun countTotalRemovableRolls(lines: List<String>): Int {
    if (lines.isEmpty()) return 0

    val rows = lines.size
    val cols = lines[0].length
    val grid = Array(rows) { r -> lines[r].toCharArray() }

    val directions = arrayOf(
        intArrayOf(-1, -1), intArrayOf(-1, 0), intArrayOf(-1, 1),
        intArrayOf(0, -1),                    intArrayOf(0, 1),
        intArrayOf(1, -1),  intArrayOf(1, 0), intArrayOf(1, 1)
    )

    // neighborCount[r][c] = number of '@' neighbors around (r, c)
    val neighborCount = Array(rows) { IntArray(cols) }

    // 1) Compute initial neighbor counts for all '@'
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (grid[r][c] == '@') {
                var n = 0
                for (dir in directions) {
                    val nr = r + dir[0]
                    val nc = c + dir[1]
                    if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == '@') {
                        n++
                    }
                }
                neighborCount[r][c] = n
            }
        }
    }

    // 2) Initialize queue with all rolls that are immediately removable (neighbors < 4)
    val queue: ArrayDeque<Pair<Int, Int>> = ArrayDeque()
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (grid[r][c] == '@' && neighborCount[r][c] < 4) {
                queue.add(Pair(r, c))
            }
        }
    }

    var removed = 0

    // 3) Repeatedly remove accessible rolls, updating neighbors
    while (queue.isNotEmpty()) {
        val (r, c) = queue.removeFirst()

        // Might have been removed already via a different path
        if (grid[r][c] != '@') continue

        // Remove this roll
        grid[r][c] = '.'
        removed++

        // Update neighbors: their '@' neighbor count goes down by 1
        for (dir in directions) {
            val nr = r + dir[0]
            val nc = c + dir[1]
            if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == '@') {
                neighborCount[nr][nc]--
                // If this neighbor just became accessible (< 4), enqueue it
                if (neighborCount[nr][nc] < 4) {
                    queue.add(Pair(nr, nc))
                }
            }
        }
    }

    return removed
}
