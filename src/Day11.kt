typealias StoneTimes = Pair<Long, Int>

private fun calcStonesAfterNBlinks(originalStones:List<Long>, blinks: Int): Long {
    val numAndTimesDeque = ArrayDeque<StoneTimes>()
    originalStones.forEach { stone ->
        numAndTimesDeque.add(StoneTimes(stone, blinks))
    }
    "Start. stones = $numAndTimesDeque".println()
    var totalStones = 0L
    while (numAndTimesDeque.isNotEmpty()) {
        val stoneTime = numAndTimesDeque.removeLastOrNull()!!
        if (stoneTime.second == 0) {
            totalStones++
            continue
        }
        val newStoneTimes = stoneTime.transform()
        newStoneTimes.forEach { numAndTimesDeque.addLast(it) }
//        "Deque.size = ${numAndTimesDeque.size}".println()
    }

//                "$stone moved to deque (${numDeque.last()} ${timesDeque.last()}".println()
//                "$stone -> $transformed".println()
//    "After transormation $i: size = ${stones.size} stones = $stones".println() //
//    "Deque.size = ${numDeque.size}".println()
//    for (i in numDeque.indices) "Deques($i): ${numDeque.removeLastOrNull()} ${timesDeque.removeLastOrNull()}".println()
    return totalStones
}

fun StoneTimes.transform(): List<StoneTimes> {
    if (second == 0) return listOf(this)
    if (first == 0L) return listOf(StoneTimes(1L, second - 1))
    val numLength = first.toString().length
    if (numLength % 2 == 0) {
        val stone1 = first.toString().substring(0, numLength / 2).toLong()
        val stone2 = first.toString().substring(numLength / 2).toLong()
        return listOf(StoneTimes(stone1, second - 1), StoneTimes(stone2, second - 1))
    }
    if (numLength == 1) {
        return this.transformSingleDigit()
    }
    return listOf(StoneTimes(first * 2024, second - 1))
}

fun StoneTimes.transformSingleDigit(): List<StoneTimes> {
    if (second > 3) {
        val newTime = second - 3
        if (first == 1L) return listOf(2L to newTime, 0L to newTime, 2L to newTime, 4L to newTime)
        if (first == 2L) return listOf(4L to newTime, 0L to newTime, 4L to newTime, 8L to newTime)
        if (first == 3L) return listOf(6L to newTime, 0L to newTime, 7L to newTime, 2L to newTime)
        if (first == 4L) return listOf(8L to newTime, 0L to newTime, 9L to newTime, 6L to newTime)
    }
    if (second > 5) {
        val newTime = second - 5
        if (first == 5L) return listOf(2L to newTime, 0L to newTime, 4L to newTime, 8L to newTime, 2L to newTime, 8L to newTime, 8L to newTime, 0L to newTime)
        if (first == 6L) return listOf(2L to newTime, 4L to newTime, 5L to newTime, 7L to newTime, 9L to newTime, 4L to newTime, 5L to newTime, 6L to newTime)
        if (first == 7L) return listOf(2L to newTime, 8L to newTime, 6L to newTime, 7L to newTime, 6L to newTime, 0L to newTime, 3L to newTime, 2L to newTime)
        if (first == 8L) return listOf(3L to newTime, 2L to newTime, 7L to newTime, 7L to newTime, 2L to newTime, 6L to newTime, 8L to newTime + 1)
        if (first == 9L) return listOf(3L to newTime, 6L to newTime, 8L to newTime, 6L to newTime, 9L to newTime, 1L to newTime, 8L to newTime, 4L to newTime)
    }
    return listOf(StoneTimes(first * 2024, second - 1))
}


fun transformStone(stone: Long): List<Long> {
    val newStones = mutableListOf<Long>()
    if (stone == 0L) newStones.add(1L)
    else if (stone.toString().length % 2 == 0) {
        val length = stone.toString().length
        val stone1 = stone.toString().substring(0, length / 2).toLong()
        val stone2 = stone.toString().substring(length / 2).toLong()
        newStones.addAll(listOf(stone1, stone2))
    } else newStones.add(stone * 2024)
    return newStones
}

fun main() {
    check(calcStonesAfterNBlinks(mutableListOf(125L, 17L), 6) == 22L)
    check(calcStonesAfterNBlinks(mutableListOf(125L, 17L), 25) == 55312L)

    val input = readInput("Day11")[0].split(" ").map { it.toLong() }
    calcStonesAfterNBlinks(input, 25).println()

    // Part 2
    calcStonesAfterNBlinks(input, 50).println()
}
