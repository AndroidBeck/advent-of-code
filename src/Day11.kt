private fun calcStonesAfterNBlinks(originalStones:List<Long>, blinks: Int): Int {
    var stones = originalStones.toList()
    for (i in 1..blinks) {
//        println("Iteration: $i")
        val newStones = mutableListOf<Long>()
        stones.forEach { stone ->
//            println("$stone -> ${transformStone(stone)}")
            newStones.addAll(transformStone(stone))
        }
        stones = newStones.toList()
        newStones.clear()
    }
    return stones.size
}

private fun transformStone(stone: Long): List<Long> {
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
    check(calcStonesAfterNBlinks(mutableListOf(125L, 17L), 6) == 22)

    val input = readInput("Day11")[0].split(" ").map { it.toLong() }
    calcStonesAfterNBlinks(input, 25).println()
}
