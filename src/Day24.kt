import LogicOp.*

private enum class LogicOp { AND, OR, XOR; }

private class OpTreeNode(val name: String, val defaultOutput: Int = -1) {
    var left: OpTreeNode? = null
    var right: OpTreeNode? = null
    var operation: LogicOp? = null

    fun calc(nameToNode: Map<String, OpTreeNode>): Int {
        return if ((left != null && right != null && operation != null)) {
            calcOpResult(left!!.calc(nameToNode), right!!.calc(nameToNode), operation!!)
        } else if (defaultOutput != -1) defaultOutput
        else error("$this: args not ready and def value not set")
    }
    override fun toString() = "$name($left, $right)"
}

private fun calcOpResult(leftArg: Int, rightArg: Int, operation: LogicOp): Int {
    return when(operation) {
        AND -> leftArg and rightArg
        OR -> leftArg or rightArg
        XOR -> leftArg xor rightArg
    }
}

fun calcDecimalOutput(input: String): Long {
    val nameToNode = mutableMapOf<String, OpTreeNode>()
    val (defaultOutputs, operationsList) = input.trim().split("\n\r\n").map { it.split("\n") }
    defaultOutputs.forEach { defOut ->
        val (name, out) = defOut.trim().split(": ")
//        "..name = $name, out = $out".println()
        nameToNode[name] = OpTreeNode(name, defaultOutput = out.toInt())
    }
    operationsList.forEach { line ->
        val (leftName, op, rightName, _, output) = line.trim().split(" ")
        val operation = when(op) {
            "AND" -> AND
            "OR" -> OR
            "XOR" -> XOR
            else -> error("Illegal operation: $op")
        }
        val node = nameToNode.computeIfAbsent(output) { OpTreeNode(name = output) }.apply { this.operation = operation }
        nameToNode.computeIfAbsent(leftName) { OpTreeNode(leftName) }.let { node.left = it }
        nameToNode.computeIfAbsent(rightName) { OpTreeNode(rightName) }.let { node.right = it }
    }

    val topNodes = nameToNode.entries.filter { it.key.startsWith("z") }.sortedBy { it.key }.map { it.value }
//    topNodes.joinToString(" ", prefix = "top nodes: ").println()
    var acc = 0L
    topNodes.forEachIndexed { i, node ->
        acc += node.calc(nameToNode).toLong().shl(i)
    }
    return acc
}

fun main() {
    val testInput = readText("Day24_test")
    val smallTestInput = readText("Day24_test_small")
    val input = readText("Day24")

    check(calcDecimalOutput(smallTestInput) == 4L) // 101
    check(calcDecimalOutput(testInput) == 2024L)
    calcDecimalOutput(input).println() // 57632654722854
}
