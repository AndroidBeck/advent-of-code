private const val BOX = '0'
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

    commands.println()
    robotC.println()
    matrix.print()

    commands.forEach { c ->
        robotC = when (c) {
            left -> moveLeft(robotC, matrix)
            right -> moveRight(robotC, matrix)
            up -> moveUp(robotC, matrix)
            down -> moveDown(robotC, matrix)
            else -> return -1 // Incorrect command
        }
        c.println()
        robotC.println()
        matrix.print()
    }

    return boxGPSSum(matrix)
}

fun moveLeft(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    var (j, i) = robotC
    val end = j
    var c = ROBOT
    var emptyNum = 0
    var boxNum = 0
    while (c != WALL) {
        j--
        c = matrix[i][j]
        if (c == EMPTY) emptyNum++
        if (c == BOX) boxNum++
    }
    if (emptyNum == 0) return robotC
    val start = j
    for (k in start + 1.. start + boxNum) matrix[i][k] = BOX
    j = start + boxNum + 1
    matrix[i][j] = ROBOT
    for (k in j + 1 .. end) matrix[i][k] = EMPTY
    "start = $start end = $end emptyNum = $emptyNum boxNum = $boxNum".println()
    return Coordinate(end - emptyNum, i)
}

fun moveRight(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    var (j, i) = robotC
    val start = j
    var c = ROBOT
    var emptyNum = 0
    var boxNum = 0
    while (c != WALL) {
        j++
        c = matrix[i][j]
        if (c == EMPTY) emptyNum++
        if (c == BOX) boxNum++
    }
    if (emptyNum == 0) return robotC
    val end = j
    for (k in start..< start + emptyNum) matrix[i][k] = EMPTY
    j = start + emptyNum
    matrix[i][j] = ROBOT
    for (k in j + 1 ..< end) matrix[i][k] = BOX
    "start = $start end = $end emptyNum = $emptyNum boxNum = $boxNum".println()
    return Coordinate(start + emptyNum, i)
}

fun moveUp(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    var (j, i) = robotC
    val end = i
    var c = ROBOT
    var emptyNum = 0
    var boxNum = 0
    while (c != WALL) {
        i--
        c = matrix[i][j]
        if (c == EMPTY) emptyNum++
        if (c == BOX) boxNum++
    }
    if (emptyNum == 0) return robotC
    val start = i
    for (k in start + 1.. start + boxNum) matrix[k][j] = BOX
    i = start + boxNum + 1
    matrix[i][j] = ROBOT
    for (k in i + 1 .. end) matrix[k][j] = EMPTY
    "start = $start end = $end emptyNum = $emptyNum boxNum = $boxNum".println()
    return Coordinate(i, end - emptyNum)
}

fun moveDown(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    var (j, i) = robotC
    val start = i
    var c = ROBOT
    var emptyNum = 0
    var boxNum = 0
    while (c != WALL) {
        i++
        c = matrix[i][j]
        if (c == EMPTY) emptyNum++
        if (c == BOX) boxNum++
    }
    if (emptyNum == 0) return robotC
    val end = i
    for (k in start..< start + emptyNum) matrix[k][j] = EMPTY
    i = start + emptyNum
    matrix[i][j] = ROBOT
    for (k in i + 1 ..< end) matrix[k][j] = BOX
    "start = $start end = $end emptyNum = $emptyNum boxNum = $boxNum".println()
    return Coordinate(j, start + emptyNum)
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
    matrix.forEachIndexed { i, line ->
        line.forEachIndexed { j, c ->
            if (c == BOX) sum += 100 * i + j
        }
    }
    return sum
}

fun main() {
    val smallTestInput = readText("Day15_small_test")
    val testInput = readText("Day15_test")
    val input = readText("Day15")

//    sumBoxesGPS("#..0@#\n\r\n<").println() // left
//    sumBoxesGPS("#.0.0.0.0@#\n\r\n<").println() // left

//    sumBoxesGPS("#@0..#\n\r\n>").println() // right
//    sumBoxesGPS("#@.0.0.0.0#\n\r\n>").println() // right

//    sumBoxesGPS("#\n.\n0\n.\n.\n@\n#\n\r\n^").println() // up
//    sumBoxesGPS("#\n0\n.\n0\n.\n0\n.\n0\n.\n@\n#\n\r\n^").println() // up

//    sumBoxesGPS("#\n@\n0\n.\n.\n0\n#\n\r\nv").println() // down
//    sumBoxesGPS("#\n@\n.\n0\n.\n0\n.\n0\n.\n0\n#\n\r\nv").println() // down

    sumBoxesGPS(smallTestInput).println()
}
