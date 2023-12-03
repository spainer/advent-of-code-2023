fun main() {
    val testInput = readInput("Day03_test")
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

private val numberRegex = Regex("\\d+")

private val symbolRegex = Regex("[^\\d.]")

private fun IntRange.expand() = (min() - 1)..(max() + 1)

private data class Number(val value: Int, val range: IntRange)
private data class Symbol(val symbol: String, val range: IntRange)

private class Line(val numbers: List<Number>, val symbols: List<Symbol>) {
}

private fun Line(line: String): Line {
    val numbers = numberRegex.findAll(line)
        .map { Number(it.value.toInt(), it.range) }
        .toList()
    val symbols = symbolRegex.findAll(line)
        .map { Symbol(it.value, it.range) }
        .toList()
    return Line(numbers, symbols)
}

private fun part1(input: List<String>): Int {
    val lines = input.map(::Line)
    return lines.flatMapIndexed { lineIdx, line ->
        val searchLineStart = (lineIdx - 1).coerceAtLeast(0)
        val searchLineEnd = (lineIdx + 1).coerceAtMost(lines.size - 1)
        val searchSymbols = lines.subList(searchLineStart, searchLineEnd + 1).flatMap { it.symbols }

        line.numbers.filter { number ->
            val expanded = number.range.expand()
            searchSymbols.any { it.range.intersect(expanded).isNotEmpty() }
        }
    }.sumOf { it.value }
}

private fun part2(input: List<String>): Int {
    val lines = input.map(::Line)
    return lines.flatMapIndexed { lineIdx, line ->
        val searchLineStart = (lineIdx - 1).coerceAtLeast(0)
        val searchLineEnd = (lineIdx + 1).coerceAtMost(lines.size - 1)
        val searchNumbers = lines.subList(searchLineStart, searchLineEnd + 1).flatMap { it.numbers }

        line.symbols
            .filter { it.symbol == "*" }
            .map { gear ->
                val expanded = gear.range.expand()
                searchNumbers.filter { it.range.intersect(expanded).isNotEmpty() }
            }
            .filter { it.size == 2 }
            .map { it[0].value * it[1].value }
    }.sumOf { it }
}