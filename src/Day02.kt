fun calcSafeReports(input: List<String>): Int {
    var safeReportsNum = 0
    input.forEach { line ->
        val report = line.split(" ").map { it.toInt() }
        if (isReportSafe(report)) safeReportsNum++
    }
    return safeReportsNum
}

fun calcSafeReportsWithTolerance(input: List<String>): Int {
    var safeReportsNum = 0
    input.forEach { line ->
        val report = line.split(" ").map { it.toInt() }
        if (isReportSafeWithTolerance(report)) safeReportsNum++
    }
    return safeReportsNum
}

private fun isReportSafe(report: List<Int>): Boolean {
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

private fun isReportSafeWithTolerance(report: List<Int>): Boolean {
    val n = report.size
    if (n < 3) return  true

    val firstDelta = report[1] - report[0]
    val isIncreasing = firstDelta > 0
    var canTolerate = true
    var i = 1
    while (i < n) {
        val delta = if (isIncreasing) report[i] - report[i - 1] else report[i - 1] - report[i]
        if (delta < 1 || delta > 3) {
            canTolerate = false
            break
        }
        i++
    }
    if (canTolerate) return true
    var newList = report.filterIndexed { index, _ -> index != i - 1 }
    if (isReportSafe(newList)) return true
    newList = report.filterIndexed { index, _ -> index != i }
    if (isReportSafe(newList)) return true
    if (i < 2) return false
    newList = report.filterIndexed { index, _ -> index != i - 2 }
    return isReportSafe(newList)
}

fun main() {
    check(calcSafeReports(listOf("1 2 3 4 5")) == 1)
    check(calcSafeReports(listOf("1 2 3 2 5")) == 0)
    check(calcSafeReports(listOf("1 2 3 4 8")) == 0)
    check(calcSafeReports(listOf("1 2")) == 1)
    check(calcSafeReports(listOf("2 1")) == 1)

    check(calcSafeReportsWithTolerance(listOf("2 1")) == 1)
    check(calcSafeReportsWithTolerance(listOf("2 1 2 3")) == 1)
    check(calcSafeReportsWithTolerance(listOf("2 19 3 5")) == 1)
    check(calcSafeReportsWithTolerance(listOf("1 2 3 500")) == 1)
    check(calcSafeReportsWithTolerance(listOf("500 2 3 6")) == 1)
    check(calcSafeReportsWithTolerance(listOf("6 3 2 500")) == 1)
    check(calcSafeReportsWithTolerance(listOf("88 89 91 91 92 94 98")) == 0)
    check(calcSafeReportsWithTolerance(listOf("88 89 91 91 92 94 97")) == 1)
    check(calcSafeReportsWithTolerance(listOf("1 3 2 1")) == 1)

    val testInput = readInput("Day02_test")
    check(calcSafeReports(testInput) == 3)
    check(calcSafeReportsWithTolerance(testInput) == 4)

    val input = readInput("Day02")
    calcSafeReports(input).println()
    calcSafeReportsWithTolerance(input).println()
}
