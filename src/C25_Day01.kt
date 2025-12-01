import java.io.File

fun main() {
    val file = File("src/C25_Day01.txt")
    if (!file.exists()) {
        error("Input file C25_Day01.txt not found!")
    }

    val rotations = file.readLines()
        .filter { it.isNotBlank() }

    val password = computePassword(rotations)
    println(password)
}

fun computePassword(rotations: List<String>): Int {
    var position = 50
    var zeroCount = 0

    for (instr in rotations) {
        val direction = instr[0]
        val value = instr.substring(1).toInt()

        position = when (direction) {
            'R' -> (position + value) % 100
            'L' -> (position - value).floorMod(100)
            else -> error("Unknown direction $direction in $instr")
        }

        if (position == 0) zeroCount++
    }

    return zeroCount
}

// Correct modulo for negative numbers
fun Int.floorMod(mod: Int): Int {
    val r = this % mod
    return if (r < 0) r + mod else r
}
