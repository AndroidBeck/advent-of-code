import java.io.File

fun main() {
    val inputPath = "src/C25_Day04.txt"
    val lines = File(inputPath)
        .readLines()
        .filter { it.isNotBlank() }

    val result = countAccessibleRolls(lines)
    println(result)
}

fun countAccessibleRolls(lines: List<String>): Int {
    if (lines.isEmpty()) return 0

    val rows = lines.size
    val cols = lines[0].length
    val grid = lines.map { it.toCharArray() }

    // 8 neighboring directions: (dr, dc)
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
