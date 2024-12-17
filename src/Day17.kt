import kotlin.math.pow

fun getComputerOutput(input: String): String {
    val regA = Regex("Register A: (\\d+)").find(input)!!.groupValues[1].toInt()
    val regB = Regex("Register B: (\\d+)").find(input)!!.groupValues[1].toInt()
    val regC = Regex("Register C: (\\d+)").find(input)!!.groupValues[1].toInt()
    val program = Regex("Program: ([\\d,]+)").find(input)!!.groupValues[1].split(',').map { it.toInt() }
    return computeOutput(program, regA, regB, regC).joinToString(",")
}

private fun computeOutput(program: List<Int>, ra: Int = 0, rb: Int = 0, rc: Int = 0, debug: Boolean = false): List<Int> {
    val n = program.size
    var (a, b, c) = listOf(ra, rb, rc)
    val output = mutableListOf<Int>()

    fun cop(arg1: Int): Int = when(arg1) {
        in 0..3 -> arg1
        4 -> a
        5 -> b
        6 -> c
        else -> throw Exception("Reserved")
    }
    fun adv(arg1: Int) = (a / 2.0.pow(cop(arg1).toDouble())).toInt()

    if (debug) "program = ${program.joinToString(",")}\nA = $a, B = $b, C = $c, output = $output".println()

    var p = 0
    while (p < n - 1) {
        val opCode = program[p]
        val arg = program[p + 1]
        var jump = false
        when (opCode){
            0 -> a = adv(arg) // ??
            1 -> b = b xor arg
            2 -> b = cop(arg) % 8
            3 -> if (a != 0) {
                p = arg
                jump = true
            }
            4 -> b = b xor c
            5 -> output.add(cop(arg) % 8)
            6 -> b = adv(arg)
            7 -> c = adv(arg)
            else -> return listOf(-1)
        }
        if (debug) "opCode = $opCode, arg = $arg: A = $a, B = $b, C = $c, p = $p, jump = $jump, output = $output".println()
        if (!jump) p += 2
    }

    return output
}

fun main() {
    val debug = false
    val testInput = readText("Day17_test")
    val input = readText("Day17")

    computeOutput(program = listOf(2,6), rc = 9, debug = debug) // B = 1
    computeOutput(program = listOf(5,0,5,1,5,4), ra = 10, debug = debug) // output: 0,1,2
    computeOutput(program = listOf(0,1,5,4,3,0), ra = 2024, debug = debug) // A = 0, output: 4,2,5,6,7,7,7,7,3,1,0
    computeOutput(program = listOf(1,7), rb = 29, debug = debug) // B = 26
    computeOutput(program = listOf(4,0), rb = 2024, rc = 43690, debug = debug) // B = 44354

    check(getComputerOutput(testInput) == "4,6,3,5,6,3,5,2,1,0")
    getComputerOutput(input).println() // 1,2,3,1,3,2,5,3,1
}
