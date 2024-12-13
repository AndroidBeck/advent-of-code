import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

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
