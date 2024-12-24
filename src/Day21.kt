/**
 *
 * This task is in process.. AoC is Finished and I decided to stop here at a meaningful number of 42 solves tasks!
 *
 * +---+---+---+
 * | 7 | 8 | 9 |
 * +---+---+---+
 * | 4 | 5 | 6 |
 * +---+---+---+
 * | 1 | 2 | 3 |
 * +---+---+---+
 *     | 0 | A |
 *     +---+---+
 *
 *     +---+---+
 *     | ^ | A |
 * +---+---+---+
 * | < | v | > |
 * +---+---+---+
 */

fun sumCodesComplexities(input: List<String>): Int {
    val digitalKMatrix = listOf("#####", "#789#", "#456#", "#123#", "##0A#", "#####")
    val digitalKSet = setOf('0'..'9', 'A')
    val symbolToCoordinateInDigitalK = mutableMapOf<Char, Coordinate>()
    val directionalKMatrix = listOf("#####", "##^A#", "#<v>#", "#####")
    val shortestWaysMap = mutableMapOf<Pair<Char, Char>, String>()
    digitalKMatrix.forEachIndexed { i, line ->
        line.forEachIndexed { j, c ->
            if (c in digitalKSet) symbolToCoordinateInDigitalK[c] = j to i
        }
    }
    var from = Coordinate(0,0)
    var to = Coordinate(0, 0)
    fun dfs(c: Coordinate, str: String) {
        val symbol = digitalKMatrix.get(c)
        if (c == to) {
            val fromC = digitalKMatrix.get(from)
            val toC = digitalKMatrix.get(to)
            shortestWaysMap[Pair(fromC, toC)] = str.plus(symbol)
            return
        }
        getDirections(c, to).forEach { dir ->
            val newC = c.moveInDir(dir)
            if (newC.inBorders(digitalKMatrix[0].length, digitalKMatrix.size)) {
                dfs(newC, str.plus(symbol))
            }
        }
    }

    var acc = 0
    input.forEach { code ->
        val sequence1 = calcSequenceOnNumericKeypad(code) // 1 robot - numeric keypad
        val sequence2 = calcSequenceOnDirectionalKeypad(sequence1) // 2 robot - directional keypad - control 1 robot
        val sequence3 = calcSequenceOnDirectionalKeypad(sequence2) // 3 robot - directional keypad - control 2 robot
        val  sequence4 = calcSequenceOnDirectionalKeypad(sequence3) // we program 3d robot on directional keypad
        val codeNumber = code.take(3).toInt()
        val delta = sequence4.length * codeNumber
        acc += delta
//        "code = $code: number = $codeNumber, length = ${sequence.length}, delta= $delta, acc = $acc, sequence = $sequence, ".println()
    }
    return 0
}

fun calcSequenceOnNumericKeypad(code: String): String {
    val start = 'A'
    return ""
}

fun calcSequenceOnDirectionalKeypad(code: String): String {
    return ""
}

fun main() {
    val testInput = readInput("Day21_test")
    val input = readInput("Day21")

//    029A: <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
//    980A: <v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A
//    179A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
//    456A: <v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A
//    379A: <v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A

    sumCodesComplexities(testInput).println()
//    check(sumCodesComplexities(testInput) == 126384)
//    sumCodesComplexities(input).println()
}
