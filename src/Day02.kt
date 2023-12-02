import java.lang.IllegalArgumentException

fun main() {
    val testInput = readInput("Day02_test")
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

private data class Sample(val red: Int, val green: Int, val blue: Int) {

    val power: Int
            get() = this.red * this.green * this.blue

    fun isSubsetOf(other: Sample) = this.red <= other.red && this.green <= other.green && this.blue <= other.blue
}

private data class Game(val samples: List<Sample>) {
    fun isPossibleForBag(bag: Sample) = this.samples.all { it.isSubsetOf(bag) }

    fun getMinimalBag(): Sample {
        val red = this.samples.maxOf { it.red }
        val green = this.samples.maxOf { it.green }
        val blue = this.samples.maxOf { it.blue }
        return Sample(red, green, blue)
    }
}

private val colorRegex = Regex("\\s*(\\d+)\\s*(red|green|blue)\\s*")

private fun parseGames(input: List<String>) = input.associate { line ->
    val split = line.split(":")
    val gameIdx = split[0].removePrefix("Game ").toInt()
    val samples = split[1].split(";").map { sampleString ->
        var red = 0
        var green = 0
        var blue = 0

        sampleString.split(",")
            .map { colorRegex.matchEntire(it) ?: throw IllegalArgumentException() }
            .forEach {
                when (it.groupValues[2]) {
                    "red" -> red = it.groupValues[1].toInt()
                    "green" -> green = it.groupValues[1].toInt()
                    "blue" -> blue = it.groupValues[1].toInt()
                }
            }

        Sample(red, green, blue)
    }

    gameIdx to Game(samples)
}

private fun part1(input: List<String>): Int {
    val bag = Sample(12, 13, 14)
    val games = parseGames(input)
    val possibleGames = games.filterValues { it.isPossibleForBag(bag) }
    return possibleGames.keys.sum()
}

private fun part2(input: List<String>): Int {
    return parseGames(input).values
        .map { it.getMinimalBag() }
        .sumOf { it.power }
}