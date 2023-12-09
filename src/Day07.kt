fun main() {
    val testInput = readInput("Day07_test")
    val input = readInput("Day07")

//    check(part1(testInput) == 6440L)
//    part1(input).println()

    check(part2(testInput) == 5905L)
    part2(input).println()
}

private enum class CamelCard {
    CARD_J,
    CARD_2,
    CARD_3,
    CARD_4,
    CARD_5,
    CARD_6,
    CARD_7,
    CARD_8,
    CARD_9,
    CARD_T,
    CARD_Q,
    CARD_K,
    CARD_A
}

private enum class CamelCardHandType {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND
}

private val regex = Regex("^(?<hand>[2-9TJQKA]+) (?<bid>\\d+)$")

private data class CamelCardHand(val cards: List<CamelCard>, val bid: Long) : Comparable<CamelCardHand> {

    val type: CamelCardHandType

    init {
        val groups = cards.groupBy { it }
            .mapValues { it.value.size }
        val jokers = groups[CamelCard.CARD_J] ?: 0
        if (jokers == 5) {
            type = CamelCardHandType.FIVE_OF_A_KIND
        } else {
            val groupsWithoutJoker = groups.filter { it.key != CamelCard.CARD_J }
            type = when (groupsWithoutJoker.maxOf { it.value } + jokers) {
                5 -> CamelCardHandType.FIVE_OF_A_KIND
                4 -> CamelCardHandType.FOUR_OF_A_KIND
//                3 -> if (groups.any { it.value == 2 }) CamelCardHandType.FULL_HOUSE else CamelCardHandType.THREE_OF_A_KIND
//                2 -> if (groups.filter { it.value == 2 }.size == 2) CamelCardHandType.TWO_PAIR else CamelCardHandType.ONE_PAIR
                3 -> {
                    when (jokers) {
                        2 -> CamelCardHandType.THREE_OF_A_KIND
                        1 -> if (groupsWithoutJoker.count { it.value == 2 } == 2) CamelCardHandType.FULL_HOUSE else CamelCardHandType.THREE_OF_A_KIND
                        else -> if (groupsWithoutJoker.any { it.value == 2 }) CamelCardHandType.FULL_HOUSE else CamelCardHandType.THREE_OF_A_KIND
                    }
                }
                2 -> {
                    when (jokers) {
                        1 -> CamelCardHandType.ONE_PAIR
                        else -> if (groups.count{ it.value == 2 } == 2) CamelCardHandType.TWO_PAIR else CamelCardHandType.ONE_PAIR
                    }
                }
                else -> CamelCardHandType.HIGH_CARD
            }
        }
    }

    override fun compareTo(other: CamelCardHand): Int {
        if (this.type > other.type) {
            return 1
        } else if (this.type < other.type) {
            return -1
        } else {
            return cards.zip(other.cards).asSequence()
                .map { it.first.compareTo(it.second) }
                .filter { it != 0 }
                .elementAtOrElse(0) { 0 }
        }
    }

}

private fun parseInput(input: List<String>) = input.map { line ->
    val match = regex.find(line) ?: throw IllegalArgumentException(line)
    val cards = match.groups["hand"]!!.value.toCharArray().map { card ->
        CamelCard.entries.first { it.name == "CARD_$card" }
    }
    val bid = match.groups["bid"]!!.value.toLong()
    CamelCardHand(cards, bid)
}

private fun part1(input: List<String>): Long {
    return parseInput(input)
        .sorted()
        .withIndex()
        .sumOf {
            (it.index + 1) * it.value.bid
        }
}

private fun part2(input: List<String>): Long {
    return parseInput(input)
        .sorted()
        .withIndex()
        .sumOf {
            (it.index + 1) * it.value.bid
        }
}