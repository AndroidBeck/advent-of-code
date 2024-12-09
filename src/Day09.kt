import java.lang.StringBuilder

private fun calcFilesystemChecksum(input: String, moveWholeFile: Boolean = false): Long {
    return if (moveWholeFile) calcCheckSumP2(getBlockIDs(input)) else calcCheckSum(getFileIDs(input))
}

private fun getFileIDs(input: String): MutableList<Int> {
    val list = mutableListOf<Int>()
    input.forEachIndexed { i, c ->
        val digit = input[i].digitToIntOrNull()!!
        val fileID = if (i % 2 == 0) i / 2 else -1
        for (i in 1..digit) list.add(fileID)
    }
    return list
}

private fun calcCheckSum(list: MutableList<Int>): Long {
    var sum = 0L
    var left = 0
    var right = list.size - 1
    while (left <= right) {
        if (list[left] != -1) {
            sum += list[left].toLong() * left
            left++
        } else if (list[right] == -1)  right--
        else {
            list[left] = list[right]
            list[right] = -1
            right--
        }
    }
    return sum
}

// Part 2
private class Block(val value: Int, var size: Int, var startsFrom: Int) {
    fun calcCheckSum(): Long {
        if (value == -1) return 0L
        var sum = 0L
        var index = startsFrom
        for (i in 1..size) {
            sum += value * index
            index++
        }
        return sum
    }
    override fun toString(): String {
        val builder = StringBuilder()
        val symbol = if (value != -1) value.toString() else "."
        if (size == 0) return "Empty($symbol)"
        for (i in 1..size) builder.append(symbol)
        return builder.toString()
    }
}

private fun getBlockIDs(input: String): MutableList<Block> {
    val blocks = mutableListOf<Block>()
    var startsFrom = 0
    input.forEachIndexed { i, c ->
        val size = input[i].digitToIntOrNull()!!
        val fileID = if (i % 2 == 0) i / 2 else -1
        blocks.add(Block(fileID, size, startsFrom))
        startsFrom += size
    }
    return blocks
}

private fun MutableList<Block>.calcStartsFromParams() {
    var left = 0
    var right = this.size - 1
    while (left <= right) {
        println("$left, $right, leftBlock = ${this[left]} rightBlock = ${this[right]}")
        if (this[left].value != -1 || this[left].size == 0) left++
        else if (this[right].value == -1) right--
        else {
            val rightBlock = this[right]
            for (k in left..< right) {
                val leftBlock = this[k]
                if (leftBlock.value == -1 && leftBlock.size >= rightBlock.size) {
                    rightBlock.startsFrom = leftBlock.startsFrom
                    leftBlock.startsFrom += rightBlock.size
                    leftBlock.size -= rightBlock.size
                    println("..Changes: left = ${this[left]} right = ${this[right]}")
                    break
                }
            }
            right--
        }
    }
}

private fun calcCheckSumP2(blocks: MutableList<Block>): Long {
    blocks.println()
    blocks.calcStartsFromParams()
    blocks.sortBy { it.startsFrom }
    blocks.forEach { block ->
        print(block)
    }
    println()
    var sum = 0L
    blocks.forEach { block ->
        if (block.value != -1) sum += block.calcCheckSum()
    }
    blocks.forEach { block ->
        println("$block, starts = ${block.startsFrom}, checkSum = ${block.calcCheckSum()}")
    }
    return sum
}

fun main() {
    check(calcFilesystemChecksum("12345") == 60L)
    check(calcFilesystemChecksum("2333133121414131402") == 1928L)

    val input = readText("Day09").trim()
    calcFilesystemChecksum(input).println()

    // Part 2
    calcFilesystemChecksum("12345", moveWholeFile = true).println()
    check(calcFilesystemChecksum("12345", moveWholeFile = true) == 132L)
    calcFilesystemChecksum("2333133121414131402", moveWholeFile = true).println()
    check(calcFilesystemChecksum("2333133121414131402", moveWholeFile = true) == 2858L)
    calcFilesystemChecksum(input, moveWholeFile = true).println()
}
