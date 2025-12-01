import java.io.File

fun main() {
    val file = File("src/C25_Day01.txt")
    if (!file.exists()) {
        error("Input file C25_Day01.txt not found!")
    }

    val rotations = file.readLines()
        .filter { it.isNotBlank() }

    val part1 = computePassword(rotations)
    val part2 = computePasswordClickMethod(rotations)

    println("Part 1 password (end-of-rotation zeros): $part1")
    println("Part 2 password (every click that lands on 0): $part2")
}

// Part 1: count times the dial points at 0 AFTER each rotation
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

// Part 2: method 0x434C49434B – count ALL clicks that land on 0
fun computePasswordClickMethod(rotations: List<String>): Int {
    var position = 50
    var zeroCount = 0

    for (instr in rotations) {
        val direction = instr[0]
        val value = instr.substring(1).toInt()

        repeat(value) {
            position = when (direction) {
                'R' -> (position + 1) % 100
                'L' -> (position - 1).floorMod(100)
                else -> error("Unknown direction $direction in $instr")
            }

            if (position == 0) {
                zeroCount++
            }
        }
    }

    return zeroCount
}

// Correct modulo for negative numbers
fun Int.floorMod(mod: Int): Int {
    val r = this % mod
    return if (r < 0) r + mod else r
}
