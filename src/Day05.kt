fun main() {
    val testInput = readInput("Day05_test")
    val input = readInput("Day05")

    check(part1(testInput) == 35L)
    part1(input).println()

    check(part2(testInput) == 46L)
    part2(input).println()
}

private val seedsRegex = Regex("^seeds:(?<seeds>(?:\\s*\\d+)+)$", RegexOption.MULTILINE)

private val mapRegex = Regex("^(?<from>[a-z]+)-to-(?<to>[a-z]+) map:$(?<content>.+?)(?<=\n)$", setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL))

private val mappingRegex = Regex("^(?<dst>\\d+) (?<src>\\d+) (?<len>\\d+)$", RegexOption.MULTILINE)

private enum class Type {
    SEED, SOIL, FERTILIZER, WATER, LIGHT, TEMPERATURE, HUMIDITY, LOCATION;
}

private fun getTypeByText(text: String): Type {
    return Type.entries.find { it.name == text.uppercase() } ?: throw IllegalArgumentException(text)
}

private data class Range(val source: Long, val destination: Long, val length: Long) : Comparable<Range> {

    val shift = destination - source

    val last = source + length - 1

    operator fun contains(i: Long) = i >= source && (i - source) < length

    operator fun get(i: Long) = destination + (i - source)
    override fun compareTo(other: Range): Int {
        return source.compareTo(other.source)
    }

    fun map(range: LongRange): LongRange? {
        if (range.first >= source + length) return null
        if (range.last < source) return null

        val start = range.first.coerceAtLeast(source)
        val end = range.last.coerceAtMost(source + length - 1)
        return (start + shift)..(end + shift)
    }

}

private class RangeMap(rawMappings: List<Range>) {

    val mappings: List<Range> = buildList {
        var lastMapping = Range(0, 0, rawMappings.first().source)
        add(lastMapping)
        for (m in rawMappings) {
            if (lastMapping.last < m.source - 1) {
                add(Range(lastMapping.last + 1, lastMapping.last + 1, m.source - lastMapping.last))
            }
            add(m)
            lastMapping = m
        }
        add(Range(lastMapping.last + 1, lastMapping.last + 1, Long.MAX_VALUE - lastMapping.last - 1))
    }

    operator fun get(i: Long): Long {
        var mapping: Range? = null
        for (m in mappings) {
            if (i in m) {
                mapping = m
                break
            } else if (i < m.source) {
                break
            }
        }
        return if (mapping != null) mapping[i] else i
    }

    fun mapRange(range: LongRange): List<LongRange> {
        return mappings.mapNotNull { it.map(range) }
    }

}

private data class Data(val seeds: List<Long>, val maps: Map<Pair<Type, Type>, RangeMap>)

private fun Data(input: List<String>): Data {
    val fullInput = input.joinToString("\n")

    val seeds = seedsRegex.findAll(fullInput)
        .first().groups["seeds"]?.value
        ?.split(" ")
        ?.filter { it.isNotBlank() }
        ?.map { it.toLong() } ?: throw IllegalArgumentException()

    val maps = mapRegex.findAll(fullInput).associate { result ->
        val from = result.groups["from"]?.value?.run { getTypeByText(this) } ?: throw IllegalArgumentException()
        val to = result.groups["to"]?.value?.run { getTypeByText(this) } ?: throw IllegalArgumentException()

        val content = result.groups["content"]?.value ?: throw IllegalArgumentException()
        val mappings = mappingRegex.findAll(content).map {
            val src = it.groups["src"]?.value?.run { toLong() } ?: throw IllegalArgumentException()
            val dst = it.groups["dst"]?.value?.run { toLong() } ?: throw IllegalArgumentException()
            val len = it.groups["len"]?.value?.run { toLong() } ?: throw IllegalArgumentException()
            Range(src, dst, len)
        }.toList().sorted()

        (from to to) to RangeMap(mappings)
    }

    return Data(seeds, maps)
}

private val mappingOrder = listOf(
    Type.SEED to Type.SOIL,
    Type.SOIL to Type.FERTILIZER,
    Type.FERTILIZER to Type.WATER,
    Type.WATER to Type.LIGHT,
    Type.LIGHT to Type.TEMPERATURE,
    Type.TEMPERATURE to Type.HUMIDITY,
    Type.HUMIDITY to Type.LOCATION
)

private fun Data.getLocation(seed: Long): Long {
    val soil = maps[Type.SEED to Type.SOIL]!![seed]
    val fertilizer = maps[Type.SOIL to Type.FERTILIZER]!![soil]
    val water = maps[Type.FERTILIZER to Type.WATER]!![fertilizer]
    val light = maps[Type.WATER to Type.LIGHT]!![water]
    val temperature = maps[Type.LIGHT to Type.TEMPERATURE]!![light]
    val humidity = maps[Type.TEMPERATURE to Type.HUMIDITY]!![temperature]
    val location = maps[Type.HUMIDITY to Type.LOCATION]!![humidity]
    return location
}

private fun Data.getLocations(seeds: LongRange): List<LongRange> {
    var currentList = listOf(seeds)
    for (m in mappingOrder) {
        val map = maps[m]!!
        currentList = currentList
            .flatMap { map.mapRange(it) }
    }
    return currentList
}

private fun part1(input: List<String>): Long {
    val data = Data(input)
    return data.seeds.minOf { data.getLocation(it) }
}

private fun part2(input: List<String>): Long {
    var i = 0
    val data = Data(input)
    return data.seeds.windowed(2, 2).minOf { rawRange ->
        val range = rawRange[0]..<rawRange[0] + rawRange[1]
        data.getLocations(range).minOf { it.first }
    }
}