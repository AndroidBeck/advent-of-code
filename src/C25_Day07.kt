import java.io.File

fun main() {
    val grid = File("src/C25_Day07.txt")
        .readLines()

    println(countSplits(grid))
}

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
