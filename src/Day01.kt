import java.lang.IllegalArgumentException

fun main() {
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

private val digits = listOf(
    "1", "2", "3", "4", "5", "6", "7", "8", "9",
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
)

private fun part1(input: List<String>): Int {
    return input.sumOf<String> { line ->
        val firstDigit = line.first { it.isDigit() }.digitToInt()
        val lastDigit = line.last { it.isDigit() }.digitToInt()
        firstDigit * 10 + lastDigit
    }
}

private fun part2(input: List<String>): Int {
    return input.sumOf { line ->
        val firstDigit = line.findAnyOf(digits)?.second?.convertName() ?: throw IllegalArgumentException()
        val lastDigit = line.findLastAnyOf(digits)?.second?.convertName() ?: throw IllegalArgumentException()
        firstDigit * 10 + lastDigit
    }
}

private fun String.convertName(): Int = (digits.indexOf(this) % 9) + 1