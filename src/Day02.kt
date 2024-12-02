fun calcSafeReports(input: List<String>): Int {
    var safeReportsNum = 0
    fun isReportSafe(report: List<Int>): Boolean {
        val n = report.size
        if (n < 2) return  true

        val firstDelta = report[1] - report[0]
        if (firstDelta == 0) return false
        val isIncreasing = firstDelta > 0
        if (isIncreasing) {
            for (i in 1..< n) {
                val delta = report[i] - report[i - 1]
                if (delta < 1 || delta > 3) return false
            }
        } else {
            for (i in 1..< n) {
                val delta = report[i - 1] - report[i]
                if (delta < 1 || delta > 3) return false
            }
        }
        return true
    }

    input.forEach { line ->
        val report = line.split(" ").map { it.toInt() }
        if (isReportSafe(report)) safeReportsNum++
    }
    return safeReportsNum
}

fun main() {
    check(calcSafeReports(listOf("1 2 3 4 5")) == 1)
    check(calcSafeReports(listOf("1 2 3 2 5")) == 0)
    check(calcSafeReports(listOf("1 2 3 4 8")) == 0)
    check(calcSafeReports(listOf("1 2")) == 1)
    check(calcSafeReports(listOf("2 1")) == 1)
    val testInput = readInput("Day02_test")
    check(calcSafeReports(testInput) == 3)

    val input = readInput("Day02")
    println(calcSafeReports(input))
}
