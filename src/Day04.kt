fun searchForXMAS(input: List<String>): Int {
    val n = input.size  // 140
    val xmas = "XMAS"
    var xmasNum = 0

    fun left(i: Int, j: Int): Boolean {
        if (j < xmas.length - 1) return false
        for (k in xmas.indices) {
            if (input[i][j - k] != xmas[k]) return false
        }
        return true
    }

    fun up(i: Int, j: Int): Boolean {
        if (i < xmas.length - 1) return false
        for (k in xmas.indices) {
            if (input[i - k][j] != xmas[k]) return false
        }
        return true
    }

    fun right(i: Int, j: Int): Boolean {
        if (j > n - xmas.length) return false
        for (k in xmas.indices) {
            if (input[i][j + k] != xmas[k]) return false
        }
        return true
    }

    fun down(i: Int, j: Int): Boolean {
        if (i > n - xmas.length) return false
        for (k in xmas.indices) {
            if (input[i + k][j] != xmas[k]) return false
        }
        return true
    }

    fun leftUp(i: Int, j: Int): Boolean {
        if (j < xmas.length - 1) return false
        if (i < xmas.length - 1) return false
        for (k in xmas.indices) {
            if (input[i - k][j - k] != xmas[k]) return false
        }
        return true
    }

    fun rightUp(i: Int, j: Int): Boolean {
        if (j > n - xmas.length) return false
        if (i < xmas.length - 1) return false
        for (k in xmas.indices) {
            if (input[i - k][j + k] != xmas[k]) return false
        }
        return true
    }

    fun leftDown(i: Int, j: Int): Boolean {
        if (j < xmas.length - 1) return false
        if (i > n - xmas.length) return false
        for (k in xmas.indices) {
            if (input[i + k][j - k] != xmas[k]) return false
        }
        return true
    }

    fun rightDown(i: Int, j: Int): Boolean {
        if (j > n - xmas.length) return false
        if (i > n - xmas.length) return false
        for (k in xmas.indices) {
            if (input[i + k][j + k] != xmas[k]) return false
        }
        return true
    }
    
    input.forEachIndexed { i, line ->
        line.forEachIndexed { j, c ->
            if (left(i, j)) xmasNum++
            if (up(i, j)) xmasNum++
            if (right(i, j)) xmasNum++
            if (down(i, j)) xmasNum++
            if (leftUp(i, j)) xmasNum++
            if (rightUp(i, j)) xmasNum++
            if (leftDown(i, j)) xmasNum++
            if (rightDown(i, j)) xmasNum++
        }
    }
    return xmasNum
}

fun main() {
    val testInput = readInput("Day04_test")
    check(searchForXMAS(testInput) == 18)

    val input = readInput("Day04")
    searchForXMAS(input).println()
}
