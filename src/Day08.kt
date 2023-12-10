fun main() {
    val testInput = readInput("Day08_test")
    val input = readInput("Day08")

//    check(part1(testInput) == 6)
//    part1(input).println()

//    check(part2(testInput) == 6)
    part2(input).println()
}

private val nodeRegex = Regex("^(?<node>[A-Z0-9]{3}) = \\((?<left>[A-Z0-9]{3}), (?<right>[A-Z0-9]{3})\\)$")

private fun parseInput(input: List<String>): Pair<Sequence<IndexedValue<Char>>, Map<String, Pair<String, String>>> {
    val steps = sequence {
        while (true) {
            yieldAll(input[0].asSequence().withIndex())
        }
    }

    val nodes = input.mapNotNull { nodeRegex.matchEntire(it) }.associate { match ->
        val node = match.groups["node"]!!.value
        val left = match.groups["left"]!!.value
        val right = match.groups["right"]!!.value
        node to (left to right)
    }

    return steps to nodes
}

private fun part1(input: List<String>): Int {
    val (steps, nodes) = parseInput(input)
    return steps.runningFold("AAA") { node, dir ->
        when (dir.value) {
            'L' -> nodes[node]!!.first
            'R' -> nodes[node]!!.second
            else -> throw IllegalArgumentException()
        }
    }.takeWhile { it != "ZZZ" }.count()
}

private fun part2(input: List<String>): Int {
    val (steps, nodes) = parseInput(input)

    fun createEndIndicesSequence(startNode: String): Sequence<Int> {
        val visited = mutableSetOf<Pair<String, Int>>()
        val loopLength = steps.runningFold(startNode) { node, dir ->
            if (!visited.add(node to dir.index)) return@runningFold ""
            when (dir.value) {
                'L' -> nodes[node]!!.first
                'R' -> nodes[node]!!.second
                else -> throw IllegalArgumentException()
            }
        }.takeWhile { it.isNotEmpty() }.count()
        println("$startNode has loop size $loopLength")

        val endNodes = steps.take(loopLength).runningFold(startNode) { node, dir ->
                when (dir.value) {
                    'L' -> nodes[node]!!.first
                    'R' -> nodes[node]!!.second
                    else -> throw IllegalArgumentException()
                }
            }
            .withIndex()
            .filter { it.value.endsWith('Z') }
            .map { it.index }
            .toList()
        println("$startNode has ${endNodes.size} end nodes: $endNodes")

        return sequence {
            var factor = 1
            while (true) {
                yieldAll(endNodes.map { it * factor })
                factor += 1
            }
        }
    }

    val pathEnds = nodes.keys.filter { it.endsWith("A") }.map { createEndIndicesSequence(it).iterator() }
    val firstPath = pathEnds[0]
    val otherPaths = pathEnds.subList(1, pathEnds.size)
    val lastIndices = otherPaths.map { -1 }.toIntArray()

    var result = -1
    outer@ for (idx in firstPath) {
        for ((itIdx, it) in otherPaths.withIndex()) {
            var i = lastIndices[itIdx]
            while (i < idx) {
                i = it.next()
                lastIndices[itIdx] = i
            }
            if (i != idx) {
                continue@outer
            }
        }

        result = idx
        break
    }
    return result

//    val iterators = startNodes.map { node ->
//        steps.runningFold(node) { currentNode, dir ->
//                when (dir.value) {
//                    'L' -> nodes[currentNode]!!.first
//                    'R' -> nodes[currentNode]!!.second
//                    else -> throw IllegalArgumentException()
//                }
//            }
//            .mapIndexedNotNull { index, currentNode -> if (currentNode.endsWith("Z")) index else null }
//            .iterator()
//    }
//    val firstIterator = iterators[0]
//    val otherIterators = iterators.subList(1, iterators.size)
//    val lastIndices = otherIterators.map { -1 }.toIntArray()
//
//    var result = -1
//    outer@ for (idx in firstIterator) {
//        for ((itIdx, it) in otherIterators.withIndex()) {
//            var i = lastIndices[itIdx]
//            while (i < idx) {
//                i = it.next()
//                lastIndices[itIdx] = i
//            }
//            if (i != idx) {
//                continue@outer
//            }
//        }
//
//        result = idx
//        break
//    }
//    return result
}
