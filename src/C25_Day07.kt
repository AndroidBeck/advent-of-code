import java.io.File

fun main() {
    val grid = File("src/C25_Day07.txt").readLines()

    val part1 = countSplits(grid)
    val part2 = countTimelinesQuantum(grid)

    println(part1)  // answer for Part 1
    println(part2)  // answer for Part 2
}

/**
 * Part 1: classical manifold – count how many times the beam is split.
 */
fun countSplits(grid: List<String>): Int {
    if (grid.isEmpty()) return 0

    val H = grid.size
    val W = grid[0].length

    // Find S
    var sRow = -1
    var sCol = -1
    for (r in grid.indices) {
        val c = grid[r].indexOf('S')
        if (c != -1) {
            sRow = r
            sCol = c
            break
        }
    }
    if (sRow == -1) error("No S found")

    val startRow = sRow + 1
    if (startRow >= H) return 0

    var splitCount = 0
    val usedSplitters = mutableSetOf<Pair<Int, Int>>()  // (row, col)

    // Beams at current row are a set of columns
    var beams = mutableSetOf(sCol)
    var row = startRow

    while (row < H && beams.isNotEmpty()) {
        val nextBeams = mutableSetOf<Int>()
        val spawned = mutableSetOf<Int>() // beams created by splitters

        for (col in beams) {
            if (col !in 0 until W) continue
            val ch = grid[row][col]

            if (ch == '^') {
                val splitter = row to col
                if (splitter !in usedSplitters) {
                    usedSplitters.add(splitter)
                    splitCount++
                }

                // spawn new beams on next row
                if (row + 1 < H) {
                    if (col - 1 >= 0) spawned.add(col - 1)
                    if (col + 1 < W) spawned.add(col + 1)
                }

                // original beam stops — no downward continuation
            } else {
                // continue straight down
                if (row + 1 < H) nextBeams.add(col)
            }
        }

        beams = (nextBeams + spawned).toMutableSet()
        row++
    }

    return splitCount
}

/**
 * Part 2: quantum manifold – count how many different timelines
 * a single particle can end up on (many-worlds interpretation).
 */
fun countTimelinesQuantum(grid: List<String>): Long {
    if (grid.isEmpty()) return 0L

    val H = grid.size
    val W = grid[0].length

    // Find S
    var sRow = -1
    var sCol = -1
    for (r in grid.indices) {
        val c = grid[r].indexOf('S')
        if (c != -1) {
            sRow = r
            sCol = c
            break
        }
    }
    if (sRow == -1) error("No S found")

    val startRow = sRow + 1
    // If S is on the last row, particle is effectively already "exited":
    if (startRow >= H) return 1L

    var outsideCount = 0L

    // dpRow[c] = number of timelines currently at (currentRow, c)
    var dpRow = LongArray(W)
    dpRow[sCol] = 1L
    var row = startRow

    while (row < H && dpRow.any { it != 0L }) {
        val nextDp = LongArray(W)

        for (c in 0 until W) {
            val ways = dpRow[c]
            if (ways == 0L) continue

            val ch = grid[row][c]

            if (ch == '^') {
                val nr = row + 1

                // Left branch
                val lc = c - 1
                if (nr >= H || lc !in 0 until W) {
                    outsideCount += ways
                } else {
                    nextDp[lc] += ways
                }

                // Right branch
                val rc = c + 1
                if (nr >= H || rc !in 0 until W) {
                    outsideCount += ways
                } else {
                    nextDp[rc] += ways
                }
            } else {
                // Straight down
                val nr = row + 1
                if (nr >= H) {
                    outsideCount += ways
                } else {
                    nextDp[c] += ways
                }
            }
        }

        dpRow = nextDp
        row++
    }

    return outsideCount
}
