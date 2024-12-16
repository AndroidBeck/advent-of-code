import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText
import Direction.*

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Get entire context of file as String
 */
fun readText(name: String) = Path("src/$name.txt").readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Useful class = Pair(x, y)
 */
typealias Coordinate = Pair<Int, Int>

fun Coordinate.x() = first
fun Coordinate.y() = second
fun Coordinate.plus(other: Coordinate) = Coordinate(x() + other.x(), y() + other.y())
fun Coordinate.overcome(other: Coordinate) = x() > other.x() || y() > other.y()

typealias CoordinateL = Pair<Long, Long>

fun CoordinateL.x() = first
fun CoordinateL.y() = second
fun CoordinateL.lPlus(other: CoordinateL) = CoordinateL(x() + other.x(), y() + other.y())
fun CoordinateL.lOvercome(other: CoordinateL) = x() > other.x() || y() > other.y()

/**
 * GCD — Greatest Common Divisor
 */
fun gcd(a: Long, b: Long): Long {
    return if (b == 0L) a else gcd(b, a % b)
}

/**
 * LCM - Least common multiple
 */
fun lcm(a: Long, b: Long): Long {
    return (a / gcd(a, b)) * b
}

enum class Direction { NORTH, SOUTH, EAST, WEST; }

fun Direction.turnRight(): Direction = when(this) {
    NORTH -> EAST
    EAST -> SOUTH
    SOUTH -> WEST
    WEST -> NORTH
}

fun Direction.turnLeft(): Direction = when(this) {
    NORTH -> WEST
    EAST -> NORTH
    SOUTH -> EAST
    WEST -> SOUTH
}

fun Direction.opposite(): Direction = when(this) {
    NORTH -> SOUTH
    SOUTH -> NORTH
    EAST -> WEST
    WEST -> EAST
}

fun Coordinate.moveInDir(direction: Direction): Coordinate {
    val (dx, dy) = when(direction) {
        NORTH -> Pair(0, -1)
        EAST -> Pair(1, 0)
        SOUTH -> Pair(0, 1)
        WEST -> Pair(-1, 0)
    }
    return Coordinate(this.x() + dx, this.y() + dy)
}

fun getDirection(from: Coordinate, to: Coordinate): Direction {
    val delta = Coordinate(to.x() - from.x(), to.y() - from.y())
    return when {
        delta.x() < 0 -> WEST
        delta.x() > 0 -> EAST
        delta.y() > 0 -> SOUTH
        else -> NORTH
    }
}

fun Coordinate.move(dx: Int, dy: Int): Coordinate = Coordinate(this.x() + dx, this.y() + dy)

fun Coordinate.getValueIn(matrix: List<String>): Char = matrix[this.y()][this.x()]
fun Coordinate.getValueIn(matrix: Array<IntArray>): Int = matrix[this.y()][this.x()]

//fun Coordinate.setValueIn(matrix: List<String>, value: Char) { matrix[this.y()][this.x()] == value }
//fun Coordinate.setValueIn(matrix: Array<IntArray>, value: Int) { matrix[this.y()][this.x()] == value }
