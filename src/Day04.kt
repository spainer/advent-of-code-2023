fun main() {
    val testInput = readInput("Day04_test")
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

private infix fun Int.pow(power: Int): Int {
    if (power < 0) return 0

    var result = 1
    for (i in 0..<power) {
        result *= this
    }
    return result
}

private data class Card(val idx: Int, val winning: Set<Int>, val present: Set<Int>) {

    val matching by lazy { winning.intersect(present).size }

    val value by lazy { 2 pow (matching - 1) }

}

private fun Card(line: String): Card {
    val (idxString, numbers) = line.split(':')
    val idx = idxString.removePrefix("Card").trim().toInt()
    val (winningStr, presentStr) = numbers.split('|')
    val winning = winningStr
        .split(' ')
        .filter { it.isNotBlank() }
        .map { it.toInt() }
        .toSet()
    val present = presentStr
        .split(' ')
        .filter { it.isNotBlank() }
        .map { it.toInt() }
        .toSet()
    return Card(idx, winning, present)
}

private fun part1(input: List<String>): Int {
    val cards = input.map(::Card).associateBy { it.idx }
    return cards.values.sumOf { it.value }
}

private fun part2(input: List<String>): Int {
    val cards = input.map(::Card).associateBy { it.idx }
    val maxIdx = cards.keys.max()

    val result = mutableListOf<Card>()
    val queue = ArrayDeque(cards.values)
    while (queue.isNotEmpty()) {
        val card = queue.removeFirst()
        result += card

        val firstIdx = card.idx + 1
        val lastIdx = (firstIdx + card.matching - 1).coerceAtMost(maxIdx)
        for (idx in firstIdx..lastIdx) {
            queue.add(cards[idx]!!)
        }
    }
    return result.size
}