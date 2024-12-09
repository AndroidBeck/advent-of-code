fun calcFilesystemChecksum(input: String): Int {
    var (left, right) = listOf(0, input.length -1)
    var sum = 0
    var rightVal = 0
    var leftVal = 0
    while (left < right) {
        if (left % 2 == 0) { // is file
            val fileId = left / 2
            sum += fileId * input[left].digitToIntOrNull()!!
            left++
        } else { // is empty space
            if (leftVal == 0) leftVal = input[left].digitToIntOrNull()!!
            if (right % 2 == 1) { // is empty space from right
                right--
            } else {
                val fileId = right / 2
                if (rightVal == 0) rightVal = input[right].digitToIntOrNull()!!
                if (rightVal > leftVal) {
                    sum += leftVal * fileId
                    rightVal -= leftVal
                    left++
                } else if (rightVal < leftVal){
                    sum += rightVal * fileId
                    leftVal -= rightVal
                    right--
                } else { // rightVal == leftVal
                    sum += rightVal * fileId
                    leftVal = 0
                    rightVal = 0
                    left++
                    right--
                }
            }
        }
    }
    return sum
}

fun main() {
//    check(calcFilesystemChecksum("12345") == 1928)
    calcFilesystemChecksum("2333133121414131402").println()
    check(calcFilesystemChecksum("2333133121414131402") == 1928)

//    val input = readText("Day09").trim()
//    calcFilesystemChecksum(input).println()
}
