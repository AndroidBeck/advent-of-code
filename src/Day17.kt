private fun getComputerOutput(input: String): String {
    val (regA, regB, regC) = parseRegValues(input)
    val program = parseProgram(input)
    return computeOutput(program, regA, regB, regC).joinToString(",")
}

private fun parseProgram(input: String):List<Int> {
    return Regex("Program: ([\\d,]+)").find(input)!!.groupValues[1].split(',').map { it.toInt() }
}

private fun parseRegValues(input: String): Triple<Long, Long, Long> {
    val regA = Regex("Register A: (\\d+)").find(input)!!.groupValues[1].toLong()
    val regB = Regex("Register B: (\\d+)").find(input)!!.groupValues[1].toLong()
    val regC = Regex("Register C: (\\d+)").find(input)!!.groupValues[1].toLong()
    return Triple(regA, regB, regC)
}

private fun computeOutput(program: List<Int>, ra: Long = 0L, rb: Long = 0L, rc: Long = 0L, isSingleOp: Boolean = false): List<Int> {
    val n = program.size
    var (a, b, c) = listOf(ra, rb, rc)
    val output = mutableListOf<Int>()
    fun cop(arg1: Int): Long = when(arg1) {
        in 0..3 -> arg1.toLong()
        4 -> a
        5 -> b
        6 -> c
        else -> throw Exception("Reserved")
    }
    fun adv(cop: Long): Long = a.ushr((cop % 64).toInt()) // = (a / 2.0.pow(cop.toDouble())).toLong()
    var p = 0
    while (p < n - 1) {
        val opCode = program[p]
        val arg = program[p + 1]
        when (opCode){
            0 -> a = adv(cop(arg))
            1 -> b = (b xor arg.toLong())
            2 -> b = cop(arg) % 8
            3 -> if (a == 0L || isSingleOp) break else p = arg
            4 -> b = b xor c
            5 -> output.add((cop(arg) % 8).toInt())
            6 -> b = adv(cop(arg))
            7 -> c = adv(cop(arg))
            else -> return listOf(-1)
        }
        if (opCode != 3) p += 2
    }
    return output
}

fun findRegAForProgramToCopyItself(input: String): Long {
    val program = parseProgram(input)
    val deque = ArrayDeque<Pair<Long, Int>>()
    for (rAStart in 0L..7L) deque.add(rAStart to program.lastIndex)
    while (deque.isNotEmpty()) {
        val (regA, i) = deque.removeFirst()
        val result = computeOutput(program, regA, 0, 0, isSingleOp = true).single()
        if (result == program[i]) {
            if (i == 0) return regA
            for (regANew in regA * 8L..< (regA + 1) * 8L) deque.add(regANew to i - 1) // regA_000 .. regA_111
        }
    }
    throw Exception("No solution")
}

fun main() {
    val testInput = readText("Day17_test")
    val testInput2 = readText("Day17_test2")
    val input = readText("Day17")

    computeOutput(program = listOf(2,6), rc = 9, ) // B = 1
    computeOutput(program = listOf(5,0,5,1,5,4), ra = 10, ) // output: 0,1,2
    computeOutput(program = listOf(0,1,5,4,3,0), ra = 2024, ) // A = 0, output: 4,2,5,6,7,7,7,7,3,1,0
    computeOutput(program = listOf(1,7), rb = 29, ) // B = 26
    computeOutput(program = listOf(4,0), rb = 2024, rc = 43690, ) // B = 44354

    check(getComputerOutput(testInput) == "4,6,3,5,6,3,5,2,1,0")
    getComputerOutput(input).println() // 1,2,3,1,3,2,5,3,1

    check(findRegAForProgramToCopyItself(testInput2) == 117440L) // 0,3,5,4,3,0 -> 117440L
    findRegAForProgramToCopyItself(input).println() // 2,4,1,5,7,5,1,6,0,3,4,3,5,5,3,0 -> 105706277661082
    findRegAForProgramToCopyItself(input).toBinary().println() // in binary code
}
