import java.io.File

fun main() {
    val lines = File("src/C25_Day06.txt").readLines()
    if (lines.isEmpty()) {
        println("Input file is empty")
        return
    }

    val grid = normalizeGrid(lines)
    val blocks = findBlocks(grid)

    val part1 = solvePart1(grid, blocks)
    val part2 = solvePart2(grid, blocks)

    println("Part 1: $part1")
    println("Part 2: $part2")
}

// Pad all lines to the same width
private fun normalizeGrid(lines: List<String>): List<String> {
    val width = lines.maxOf { it.length }
    return lines.map { it.padEnd(width, ' ') }
}

private data class Block2(val startCol: Int, val endCol: Int, val operator: Char)

// Find [start,end) column ranges for each problem + its operator
private fun findBlocks(grid: List<String>): List<Block2> {
    val height = grid.size
    val width = grid[0].length
    val bottomRow = grid.last()

    val blocks = mutableListOf<Block2>()
    var col = 0

    while (col < width) {
        // Skip separator columns (all spaces)
        val isEmptyColumn = (0 until height).all { grid[it][col] == ' ' }
        if (isEmptyColumn) {
            col++
            continue
        }

        val start = col
        var end = col
        // Extend to the right until we hit an all-space column
        while (end < width) {
            val empty = (0 until height).all { grid[it][end] == ' ' }
            if (empty) break
            end++
        }

        // Operator is on the bottom row in this block
        val opChar = bottomRow.substring(start, end)
            .first { !it.isWhitespace() }

        blocks.add(Block2(start, end, opChar))
        col = end
    }

    return blocks
}

// ---------- PART 1: original row-based numbers ----------

private fun solvePart1(grid: List<String>, blocks: List<Block2>): Long {
    val height = grid.size
    val results = mutableListOf<Long>()

    for (block in blocks) {
        val numbers = mutableListOf<Long>()

        // Each row (except last) may contain a number in this block
        for (row in 0 until height - 1) {
            val slice = grid[row].substring(block.startCol, block.endCol).trim()
            if (slice.isNotEmpty()) {
                numbers.add(slice.toLong())
            }
        }

        val result = when (block.operator) {
            '+' -> numbers.sum()
            '*' -> numbers.fold(1L) { acc, v -> acc * v }
            else -> error("Unknown operator: ${block.operator}")
        }

        results.add(result)
    }

    return results.sum()
}

// ---------- PART 2: column-based cephalopod numbers ----------

private fun solvePart2(grid: List<String>, blocks: List<Block2>): Long {
    val height = grid.size
    val results = mutableListOf<Long>()

    for (block in blocks) {
        val numbers = mutableListOf<Long>()

        // For cephalopod math, each column (right-to-left) is a number
        // Digits are read top-to-bottom (excluding operator row)
        for (col in block.endCol - 1 downTo block.startCol) {
            val sb = StringBuilder()
            for (row in 0 until height - 1) { // skip last row (operator row)
                val ch = grid[row][col]
                if (ch.isDigit()) {
                    sb.append(ch)
                }
            }
            if (sb.isNotEmpty()) {
                numbers.add(sb.toString().toLong())
            }
        }

        val result = when (block.operator) {
            '+' -> numbers.sum()
            '*' -> numbers.fold(1L) { acc, v -> acc * v }
            else -> error("Unknown operator: ${block.operator}")
        }

        results.add(result)
    }

    return results.sum()
}
