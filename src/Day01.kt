import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val n = input.size
        val first = MutableList(n) { 0 }
        val second = MutableList(n) { 0 }
        input.forEachIndexed { i, line ->
            val (a, b) = line.split("   ")
            first[i] = a.toInt()
            second[i] = b.toInt()
        }
        first.sort()
        second.sort()
        var delta = 0
        var distance = 0
        for (i in 0..< n) {
            delta = abs(second[i] - first[i])
            distance += abs(delta)
        }
        return distance
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("1000   500", "200   4000", "300   0")) == 3400)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 3400)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    // part2(input).println()
}
