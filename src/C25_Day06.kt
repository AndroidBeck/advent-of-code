import java.io.File

fun main() {
    val file = File("src/C25_Day06.txt")
    val lines = file.readLines()

    if (lines.isEmpty()) {
        println("Input file is empty!")
        return
    }

    val height = lines.size
    val width = lines.maxOf { it.length }

    // Normalize lines to same width (pad with spaces)
    val grid = lines.map { it.padEnd(width, ' ') }

    val results = mutableListOf<Long>()

    var col = 0
    while (col < width) {

        // Skip empty separator columns
        val isEmptyColumn = (0 until height).all { grid[it][col] == ' ' }
        if (isEmptyColumn) {
            col++
            continue
        }

        // Identify full block width for this problem
        var start = col
        var end = col
        while (end < width) {
            val empty = (0 until height).all { grid[it][end] == ' ' }
            if (empty) break
            end++
        }

        // Now parse vertical numbers in the block
        val numbers = mutableListOf<Long>()
        for (row in 0 until height - 1) { // except last row (operator row)
            val slice = grid[row].substring(start, end).trim()
            if (slice.isNotEmpty()) {
                numbers.add(slice.toLong())
            }
        }

        // Operation symbol (bottom row inside block)
        val opSlice = grid[height - 1].substring(start, end).trim()
        if (opSlice.isEmpty()) {
            throw IllegalStateException("Missing operator for block [$start,$end)")
        }
        val operator = opSlice.first()

        // Compute value
        val result = when (operator) {
            '+' -> numbers.sum()
            '*' -> numbers.fold(1L) { acc, v -> acc * v }
            else -> error("Unknown operator: $operator")
        }

        results.add(result)

        col = end // jump to next block
    }

    val grandTotal = results.sum()
    println(grandTotal)
}
