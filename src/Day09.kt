fun calcFilesystemChecksum(input: String): Long {
    val list = getFileSystemString(input)
    return calcCheckSum(list)
}

private fun getFileSystemString(input: String): MutableList<Int> {
    val list = mutableListOf<Int>()
    input.forEachIndexed { i, c ->
        val digit = input[i].digitToIntOrNull()!!
        if (i % 2 == 0) {
            val fileID = i / 2
            for (i in 1..digit) {
                list.add(fileID)
            }
        } else {
            for (i in 1..digit) list.add(-1)
        }
    }
    return list
}

private fun calcCheckSum(list: MutableList<Int>): Long {
//    val output = BufferedWriter(FileWriter("output.txt"))
    var sum = 0L
    var left = 0
    var right = list.size - 1
    while (left <= right) {
        if (list[left] != -1) {
            sum += list[left].toLong() * left
            left++
        } else if (list[right] == -1)  right--
        else {
            list[left] = list[right]
            list[right] = -1
            right--
        }
//         output.appendLine("sum = $sum")
    }
//    output.flush()
    return sum
}

fun main() {
    check(calcFilesystemChecksum("12345") == 60L)
    check(calcFilesystemChecksum("2333133121414131402") == 1928L)

    val input = readText("Day09").trim()
    calcFilesystemChecksum(input).println()
}
