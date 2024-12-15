private const val BOX = 'O'
private const val WALL = '#'
private const val ROBOT = '@'
private const val EMPTY = '.'
private const val BOX_LEFT = '['
private const val BOX_RIGHT = ']'

fun sumBoxesGPS(input: String, p2: Boolean = false): Int {
    val (first, second) = input.split("\n\r\n").map { it.lines() }
    val matrix = first.map { it.toCharArray() }.let { if (p2) expandMatrix(it) else it }
    val commands = second.joinToString("")
    val directions = listOf('<', '>', '^', 'v',)
    val (left, right, up, down) = directions
    var robotC = getCoordinate(matrix, ROBOT)

    // commands.println()
    // printDebugInfo(robotC, matrix)
    commands.forEachIndexed { i, c ->
        robotC = when (c) {
            left -> if (p2) moveLeftP2(robotC, matrix) else moveLeft(robotC, matrix)
            right -> if (p2) moveRightP2(robotC, matrix) else moveRight(robotC, matrix)
            up -> if (p2) moveUpP2(robotC, matrix) else moveUp(robotC, matrix)
            down -> if (p2) moveDownP2(robotC, matrix) else moveDown(robotC, matrix)
            else -> return -1 // Incorrect command
        }
        // if (i in 600..800) printDebugInfo(robotC, matrix, c, i)
    }
    val symbol = if (p2) BOX_LEFT else BOX
    return boxGPSSum(matrix, symbol)
}

private fun boxGPSSum(matrix: List<CharArray>, symbol: Char): Int {
    var sum = 0
    matrix.forEachIndexed { i, array ->
        array.forEachIndexed { j, c ->
            if (c == symbol) sum += 100 * i + j
        }
    }
    return sum
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

private fun printDebugInfo(robotC: Coordinate, matrix: List<CharArray>, command: Char = '_', i: Int = 0) {
    " i= $i, $command".println()
    robotC.println()
    matrix.print()
}

private fun moveLeft(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
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

private fun moveRight(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
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

private fun moveUp(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
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

private fun moveDown(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
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

// Part 2
private fun expandMatrix(matrix: List<CharArray>): List<CharArray> {
    val replacementMap = mapOf(
        WALL to "##",
        BOX to "[]",
        EMPTY to "..",
        ROBOT to "@."
    )
    return matrix.map { row ->
        row.joinToString("") { replacementMap[it] ?: "$it" }.toCharArray()
    }
}

private fun moveLeftP2(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    var (j, i) = robotC
    val start = j
    var c = ROBOT
    var boxNum = 0
    while (c != EMPTY) {
        j--
        c = matrix[i][j]
        if (c == BOX_LEFT) boxNum++
        else if (c == WALL) return robotC
    }
    matrix[i][start] = EMPTY
    matrix[i][start - 1] = ROBOT
    for (k in j .. start - 2) {
        if (matrix[i][k] == BOX_LEFT) matrix[i][k] = BOX_RIGHT else matrix[i][k] = BOX_LEFT
    }
    return Coordinate(start - 1, i)
}

private fun moveRightP2(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    var (j, i) = robotC
    val start = j
    var c = ROBOT
    var boxNum = 0
    while (c != EMPTY) {
        j++
        c = matrix[i][j]
        if (c == BOX_RIGHT) boxNum++
        else if (c == WALL) return robotC
    }
    matrix[i][start] = EMPTY
    matrix[i][start + 1] = ROBOT
    for (k in start + 2 .. j) {
        if (matrix[i][k] == BOX_RIGHT) matrix[i][k] = BOX_LEFT else matrix[i][k] = BOX_RIGHT
    }
    return Coordinate(start + 1, i)
}

private fun moveUpP2(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    val (startX, startY) = robotC
    val levelsMap = mutableMapOf<Int, MutableSet<Int>>() // level (y) to set of positions (x)
    var i = startY
    levelsMap[i] = mutableSetOf(startX)
    while (true) {
        i-- // level up
        levelsMap[i] = mutableSetOf()
        levelsMap[i + 1]!!.forEach { j ->
            if (matrix[i][j] == WALL) return robotC
            else if (matrix[i][j] == BOX_LEFT) levelsMap[i]!!.addAll(listOf(j, j + 1))
            else if (matrix[i][j] == BOX_RIGHT) levelsMap[i]!!.addAll(listOf(j, j - 1))
        }
        if (levelsMap[i]!!.size == 0) break
    }

    for (i in startY - levelsMap.size + 1 ..< startY - 1) {
        (levelsMap[i + 1]!!).forEach { j ->
            matrix[i][j] = matrix[i + 1][j]
            if (matrix[i][j] == BOX_LEFT && matrix[i][j - 1] == BOX_LEFT) matrix[i][j - 1] = EMPTY
            if (matrix[i][j] == BOX_RIGHT && matrix[i][j + 1] == BOX_RIGHT) matrix[i][j + 1] = EMPTY
        }
    }
    matrix[startY - 1][startX] = ROBOT
    if (matrix[startY - 1][startX - 1] == BOX_LEFT) matrix[startY - 1][startX - 1] = EMPTY
    if (matrix[startY - 1][startX + 1] == BOX_RIGHT) matrix[startY - 1][startX + 1] = EMPTY
    matrix[startY][startX] = EMPTY
    return Coordinate(startX, startY - 1)
}

private fun moveDownP2(robotC: Coordinate, matrix: List<CharArray>): Coordinate {
    val (startX, startY) = robotC
    val levelsMap = mutableMapOf<Int, MutableSet<Int>>() // level (y) to set of positions (x)
    var i = startY
    levelsMap[i] = mutableSetOf(startX)
    while (true) {
        i++ // level down
        levelsMap[i] = mutableSetOf()
        levelsMap[i - 1]!!.forEach { j ->
            if (matrix[i][j] == WALL) return robotC
            else if (matrix[i][j] == BOX_LEFT) levelsMap[i]!!.addAll(listOf(j, j + 1))
            else if (matrix[i][j] == BOX_RIGHT) levelsMap[i]!!.addAll(listOf(j, j - 1))
        }
        if (levelsMap[i]!!.size == 0) break
    }

    for (i in startY + levelsMap.size - 2 downTo  startY + 1) {
        levelsMap[i]!!.forEach { j ->
            matrix[i + 1][j] = matrix[i][j]
            if (matrix[i + 1][j] == BOX_LEFT && matrix[i + 1][j - 1] == BOX_LEFT) matrix[i + 1][j - 1] = EMPTY
            if (matrix[i + 1][j] == BOX_RIGHT && matrix[i + 1][j + 1] == BOX_RIGHT) matrix[i + 1][j + 1] = EMPTY
        }
    }
    matrix[startY + 1][startX] = ROBOT
    if (matrix[startY + 1][startX - 1] == BOX_LEFT) matrix[startY + 1][startX - 1] = EMPTY
    if (matrix[startY + 1][startX + 1] == BOX_RIGHT) matrix[startY + 1][startX + 1] = EMPTY
    matrix[startY][startX] = EMPTY
    return Coordinate(startX, startY + 1)
}

fun main() {
    val smallTestInput = readText("Day15_small_test")
    val testInput = readText("Day15_test")
    val input = readText("Day15")

    check(sumBoxesGPS(smallTestInput) == 2028)
    check(sumBoxesGPS(testInput) == 10092)
    sumBoxesGPS(input).println() // 1577255

    val smallTestP2Input = readText("Day15_small_test_p2")
    check(sumBoxesGPS(smallTestP2Input, p2 = true) == 618)
    check(sumBoxesGPS(smallTestInput, p2 = true) == 1751)
    check(sumBoxesGPS(testInput, p2 = true) == 9021)
    sumBoxesGPS(input, p2 = true).println() // 1597035
}
