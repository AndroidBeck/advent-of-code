private fun calcStonesAfterNBlinks(originalStones:List<Long>, blinks: Int): Int {
    var stones = originalStones.toList()
    for (i in 1..blinks) {
        val newStones = mutableListOf<Long>()
        stones.forEach { stone ->
            newStones.addAll(transformStone(stone))
        }
        stones = newStones.toList()
        newStones.clear()
    }
    return stones.size
}

private fun transformStone(stone: Long): List<Long> {
    if (stone == 0L) return listOf(1L)
    if (stone.toString().length % 2 == 0) {
        val length = stone.toString().length
        val stone1 = stone.toString().substring(0, length / 2).toLong()
        val stone2 = stone.toString().substring(length / 2).toLong()
        return listOf(stone1, stone2)
    }
    return listOf(stone * 2024)
}

// Part 2
private fun calcStonesWithMemory(originalStones:List<Long>, blinks: Int): Long {
    val countCache = mutableMapOf<Pair<Long, Int>, Long>() // (number, blinksRemain) -> size of resulting list
    // var iterations = 0L

    fun countRecursive(stone: Long, blinksRemain: Int): Long {
        // iterations++
        if (blinksRemain == 0) return 1L
        val countKey = stone to blinksRemain
        if (countKey in countCache) return countCache[countKey]!!

        val transformedNumbers = transformStone(stone)
        val totalCount = transformedNumbers.sumOf {
            countRecursive(it, blinksRemain - 1)
        }

        countCache[countKey] = totalCount
        // "countCache[$countKey] = $totalCount".println()
        return totalCount
    }
    val result = originalStones.sumOf { countRecursive(it, blinks) }
    // iterations.println()
    return result
}

fun main() {
    check(calcStonesAfterNBlinks(mutableListOf(125L, 17L), 6) == 22)

    val input = readInput("Day11")[0].split(" ").map { it.toLong() }
    calcStonesAfterNBlinks(input, 25).println()

    calcStonesWithMemory(input, 75).println()
}
