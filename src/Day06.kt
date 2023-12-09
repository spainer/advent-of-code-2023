fun main() {
    val testInput = readInput("Day06_test")
    val input = readInput("Day06")

    check(part1(testInput) == 288)
    part1(input).println()

    check(part2(testInput) == 71503)
    part2(input).println()
}

private data class Race(val duration: Long, val record: Long)

private fun parseInput(input: List<String>): List<Race> {
    val durations = input[0].removePrefix("Time:")
        .split("\\s+".toRegex())
        .filter { it.isNotBlank() }
        .map { it.toLong() }
    val records = input[1].removePrefix("Distance:")
        .split("\\s+".toRegex())
        .filter { it.isNotBlank() }
        .map { it.toLong() }
    return durations.zip(records)
        .map { Race(it.first, it.second) }
}

private fun parseInput2(input: List<String>): Race {
    val duration = input[0].removePrefix("Time:")
        .replace(" ", "")
        .toLong()
    val record = input[1].removePrefix("Distance:")
        .replace(" ", "")
        .toLong()
    return Race(duration, record)
}

private val Race.winningAccelerations get() = (0..duration).filter { acceleration ->
    val travelTime = duration - acceleration
    val distance = travelTime * acceleration
    distance > record
}

private fun part1(input: List<String>): Int {
    val races = parseInput(input)
    return races
        .map { it.winningAccelerations }
        .fold(1) { acc, list -> acc * list.size }
}

private fun part2(input: List<String>): Int {
    val race = parseInput2(input)
    return race.winningAccelerations.size
}