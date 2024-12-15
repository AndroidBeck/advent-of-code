private const val BOX = 'O'
private const val WALL = '#'
private const val ROBOT = '@'
private const val EMPTY = '.'

fun sumBoxesGPS(input: String): Int {
    val (first, second) = input.split("\n\r\n").map { it.lines() }
    val matrix = first.map { it.toCharArray() }
    val commands = second.joinToString("")
    val directions = listOf('<', '>', '^', 'v',)
    val (left, right, up, down) = directions
    var robotC = getCoordinate(matrix, ROBOT)

//    commands.println()
//    robotC.println()
//    matrix.print()

    commands.forEach { c ->
        robotC = when (c) {
            left -> moveLeft(robotC, matrix)
            right -> moveRight(robotC, matrix)
            up -> moveUp(robotC, matrix)
            down -> moveDown(robotC, matrix)
            else -> return -1 // Incorrect command
        }
//        commands.println()
//        robotC.println()
//        matrix.print()
    }
    return boxGPSSum(matrix)
}

fun moveLeft(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    var (j, i) = robotC
    val start = j
    var c = ROBOT
    var boxNum = 0
    while (c != EMPTY) {
        j--
        c = matrix[i][j]
        if (c == BOX) boxNum++
        else if (c == WALL) return robotC
    }
    matrix[i][j] = BOX
    matrix[i][start] = EMPTY
    matrix[i][start - 1] = ROBOT
    return Coordinate(start - 1, i)
}

fun moveRight(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    var (j, i) = robotC
    val start = j
    var c = ROBOT
    var boxNum = 0
    while (c != EMPTY) {
        j++
        c = matrix[i][j]
        if (c == BOX) boxNum++
        else if (c == WALL) return robotC
    }
    matrix[i][j] = BOX
    matrix[i][start] = EMPTY
    matrix[i][start + 1] = ROBOT
    return Coordinate(start + 1, i)
}

fun moveUp(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    var (j, i) = robotC
    val start = i
    var c = ROBOT
    var boxNum = 0
    while (c != EMPTY) {
        i--
        c = matrix[i][j]
        if (c == BOX) boxNum++
        else if (c == WALL) return robotC
    }
    matrix[i][j] = BOX
    matrix[start][j] = EMPTY
    matrix[start - 1][j] = ROBOT
    return Coordinate(j, start - 1)
}

fun moveDown(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    var (j, i) = robotC
    val start = i
    var c = ROBOT
    var boxNum = 0
    while (c != EMPTY) {
        i++
        c = matrix[i][j]
        if (c == BOX) boxNum++
        else if (c == WALL) return robotC
    }
    matrix[i][j] = BOX
    matrix[start][j] = EMPTY
    matrix[start + 1][j] = ROBOT
    return Coordinate(j, start + 1)
}

private fun getCoordinate(matrix: List<CharArray>, symbol: Char): Coordinate {
    matrix.forEachIndexed { i, line ->
        line.forEachIndexed { j, c ->
            if (c == symbol) return Coordinate(j, i)
        }
    }
    return -1 to -1
}

private fun List<CharArray>.print() {
    forEach{ array ->
        array.joinToString("").println()
    }
}

private fun boxGPSSum(matrix: List<CharArray>): Int {
    var sum = 0
    matrix.forEachIndexed { i, array ->
        array.forEachIndexed { j, c ->
//            "$i, $j $c".println()
            if (c == BOX) {
                val add = 100 * i + j
                sum += add
//                "($i, $j) add = $add sum = $sum".println()
            }
        }
    }
    return sum
}

fun main() {
    val smallTestInput = readText("Day15_small_test")
    val testInput = readText("Day15_test")
    val input = readText("Day15")

//    sumBoxesGPS("#.0.0.00@#\n\r\n<").println() // left
//    sumBoxesGPS("#.0000@#\n\r\n<").println() // left
//    sumBoxesGPS("#@00.0.0#\n\r\n>").println() // right
//    sumBoxesGPS("#\n0\n.\n0\n.\n0\n0\n@\n#\n\r\n^").println() // up
//    sumBoxesGPS("#\n@\n0\n0\n.\n0\n.\n0\n#\n\r\nv").println() // down

    sumBoxesGPS(smallTestInput).println()
    sumBoxesGPS(testInput).println()
    sumBoxesGPS(input).println()
}
