fun main() {
    val testInput = readInput("Day09_test")
    val input = readInput("Day09")

    check(part1(testInput) == 114)
    part1(input).println()

    check(part2(testInput) == 2)
    part2(input).println()
}

private fun parseLine(line: String): List<Int> {
    return line.split(" ").map { it.toInt() }
}

private fun derivative(history: List<Int>): List<Int> {
    return history.windowed(2, 1).map { it[1] - it[0] }
}

private fun extrapolate(history: List<Int>): Int {
    val derivatives = mutableListOf(history.toMutableList())
    while (derivatives.last().any { it != 0 }) {
        derivatives += derivative(derivatives.last()).toMutableList()
    }

    val extrapolations = derivatives.reversed()
    extrapolations[0] += 0
    extrapolations.windowed(2, 1).forEach {
        it[1] += it[1].last() + it[0].last()
    }

    return extrapolations.last().last()
}

private fun extrapolateFront(history: List<Int>): Int {
    val derivatives = mutableListOf(history.toMutableList())
    while (derivatives.last().any { it != 0 }) {
        derivatives += derivative(derivatives.last()).toMutableList()
    }

    val extrapolations = derivatives.reversed()
    extrapolations[0].add(0, 0)
    extrapolations.windowed(2, 1).forEach {
        it[1].add(0, it[1].first() - it[0].first())
    }

    return extrapolations.last().first()
}

private fun part1(input: List<String>): Int {
    return input.map(::parseLine).sumOf { extrapolate(it) }
}

private fun part2(input: List<String>): Int {
    return input.map(::parseLine).sumOf { extrapolateFront(it) }
}