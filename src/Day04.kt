fun left(i: Int, j: Int, input: List<String>, word: String): Boolean {
    if (j < word.length - 1) return false
    for (k in word.indices) {
        if (input[i][j - k] != word[k]) return false
    }
    return true
}

fun up(i: Int, j: Int, input: List<String>, word: String): Boolean {
    if (i < word.length - 1) return false
    for (k in word.indices) {
        if (input[i - k][j] != word[k]) return false
    }
    return true
}

fun right(i: Int, j: Int, input: List<String>, word: String): Boolean {
    if (j > input.size - word.length) return false
    for (k in word.indices) {
        if (input[i][j + k] != word[k]) return false
    }
    return true
}

fun down(i: Int, j: Int, input: List<String>, word: String): Boolean {
    if (i > input.size - word.length) return false
    for (k in word.indices) {
        if (input[i + k][j] != word[k]) return false
    }
    return true
}

fun leftUp(i: Int, j: Int, input: List<String>, word: String): Boolean {
    if (j < word.length - 1) return false
    if (i < word.length - 1) return false
    for (k in word.indices) {
        if (input[i - k][j - k] != word[k]) return false
    }
    return true
}

fun rightUp(i: Int, j: Int, input: List<String>, word: String): Boolean {
    if (j > input.size - word.length) return false
    if (i < word.length - 1) return false
    for (k in word.indices) {
        if (input[i - k][j + k] != word[k]) return false
    }
    return true
}

fun leftDown(i: Int, j: Int, input: List<String>, word: String): Boolean {
    if (j < word.length - 1) return false
    if (i > input.size - word.length) return false
    for (k in word.indices) {
        if (input[i + k][j - k] != word[k]) return false
    }
    return true
}

fun rightDown(i: Int, j: Int, input: List<String>, word: String): Boolean {
    if (j > input.size - word.length) return false
    if (i > input.size - word.length) return false
    for (k in word.indices) {
        if (input[i + k][j + k] != word[k]) return false
    }
    return true
}

fun searchForXMAS(input: List<String>): Int {
    val word = "XMAS"
    var xmasNum = 0
    input.forEachIndexed { i, line ->
        line.forEachIndexed { j, c ->
            if (left(i, j, input, word)) xmasNum++
            if (up(i, j, input, word)) xmasNum++
            if (right(i, j, input, word)) xmasNum++
            if (down(i, j, input, word)) xmasNum++
            if (leftUp(i, j, input, word)) xmasNum++
            if (rightUp(i, j, input, word)) xmasNum++
            if (leftDown(i, j, input, word)) xmasNum++
            if (rightDown(i, j, input, word)) xmasNum++
        }
    }
    return xmasNum
}

fun searchForXLikeMAS(input: List<String>): Int {
    var xmasNum = 0
    input.forEachIndexed { i, line ->
        line.forEachIndexed { j, c ->
            if (c != 'A') return@forEachIndexed
            var acc = 0
            if (leftUp(i, j, input, "AM") && rightDown(i, j, input, "AS")) acc++
            else if (leftUp(i, j, input, "AS") && rightDown(i, j, input, "AM")) acc++
            if (rightUp(i, j, input, "AM") && leftDown(i, j, input, "AS")) acc++
            else if (rightUp(i, j, input, "AS") && leftDown(i, j, input, "AM")) acc++
            if (acc == 2) xmasNum++
        }
    }
    return xmasNum
}

fun main() {
    val testInput = readInput("Day04_test")
    check(searchForXMAS(testInput) == 18)

    val input = readInput("Day04")
    searchForXMAS(input).println()

    val testInput2 = readInput("Day04_test")
    check(searchForXLikeMAS(testInput2) == 9)
    searchForXLikeMAS(input).println()
}
