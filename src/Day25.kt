fun locksAndKeys(input: String): Int {
    val blocks = input.split("\n\r\n").map { it.trim().lines() }
    val locks = mutableListOf<IntArray>()
    val keys = mutableListOf<IntArray>()

    val fullRow = "#####"
    val emptyRow = "....."

    blocks.forEach { block ->
        val newEl = IntArray(5) { 0 }
        val isLock = block.first() == fullRow
        var i = if (isLock) 1 else block.size - 2
        do {
            val row = block[i]
            row.forEachIndexed { j, c ->
                if (c == '#') newEl[j]++
            }
            if (isLock) i++ else i--
        } while (row != emptyRow)
        if (isLock) locks.add(newEl) else keys.add(newEl)
    }

//    locks.forEach { it.joinToString(", ", prefix = "locks: ").println() }
//    keys.forEach { it.joinToString(", ", prefix = "keys: ").println() }

    var fitted = 0
    locks.forEach { lock ->
        keys.forEach { key ->
            if (fits(lock, key)) fitted++
        }
    }
    return fitted
}

private fun fits(lock: IntArray, key: IntArray): Boolean {
    for (i in lock.indices) {
        if (lock[i] + key[i] > 5) return false
    }
    return true
}

fun main() {
    val testInput = readText("Day25_test")
    val input = readText("Day25")

    check(locksAndKeys(testInput) == 3)
    locksAndKeys(input).println()
}
