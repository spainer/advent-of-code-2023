import kotlin.math.exp

fun main() {
    val testInput = readInput("Day10_test")
    val input = readInput("Day10")

//    check(part1(testInput) == 8)
    part1(input).println()

    check(part2(testInput) == 10)
    part2(input).println()
}

private enum class PipeConnection {
    NS, EW, NE, NW, SW, SE, START
}

private fun parseLine(line: String): List<PipeConnection?> {
    return line.map { when(it) {
        '|' -> PipeConnection.NS
        '-' -> PipeConnection.EW
        'L' -> PipeConnection.NE
        'J' -> PipeConnection.NW
        '7' -> PipeConnection.SW
        'F' -> PipeConnection.SE
        'S' -> PipeConnection.START
        '.' -> null
        else -> throw IllegalArgumentException(it.toString())
    }}
}

private fun replaceStart(grid: List<MutableList<PipeConnection?>>): Pair<Int, Int> {
    val startCoord = grid.mapIndexed { rowIdx, rowData ->
        val colIdx = rowData.withIndex().find { it.value == PipeConnection.START }?.index
        if (colIdx != null) rowIdx to colIdx else null
    }.first { it != null } ?: throw IllegalArgumentException()

    val north = startCoord.first > 0 && when(grid[startCoord.first - 1][startCoord.second]) {
        PipeConnection.NS, PipeConnection.SE, PipeConnection.SW -> true
        else -> false
    }
    val south = startCoord.first < grid.size - 1 && when(grid[startCoord.first + 1][startCoord.second]) {
        PipeConnection.NS, PipeConnection.NW, PipeConnection.NE -> true
        else -> false
    }
    val east = startCoord.second < grid[startCoord.first].size - 1 && when(grid[startCoord.first][startCoord.second + 1]) {
        PipeConnection.EW, PipeConnection.NW, PipeConnection.SW -> true
        else -> false
    }
    val west = startCoord.second > 0 && when(grid[startCoord.first][startCoord.second - 1]) {
        PipeConnection.EW, PipeConnection.SE, PipeConnection.NE -> true
        else -> false
    }
    val realStart = when {
        north && east -> PipeConnection.NE
        north && south -> PipeConnection.NS
        north && west -> PipeConnection.NW
        east && south -> PipeConnection.SE
        east && west -> PipeConnection.EW
        south && west -> PipeConnection.SW
        else -> throw IllegalArgumentException()
    }
    grid[startCoord.first][startCoord.second] = realStart

    return startCoord
}

private fun findLoop(grid: List<List<PipeConnection?>>, start: Pair<Int, Int>): List<IntArray> {
    val rowRange = 0..<grid.size
    val colRange = 0..<grid[0].size

    val maxSteps = grid.map { it.map { Int.MAX_VALUE }.toIntArray() }
    maxSteps[start.first][start.second] = 0
    val paths = mutableListOf(listOf(start))

    while (paths.isNotEmpty()) {
        val path = paths.removeFirst()
        val currentPos = path.last()
        val pipe = grid[currentPos.first][currentPos.second] ?: throw IllegalArgumentException()

        val possiblePaths = when(pipe) {
            PipeConnection.NE -> listOf(currentPos.first - 1 to currentPos.second, currentPos.first to currentPos.second + 1)
            PipeConnection.NS -> listOf(currentPos.first - 1 to currentPos.second, currentPos.first + 1 to currentPos.second)
            PipeConnection.NW -> listOf(currentPos.first - 1 to currentPos.second, currentPos.first to currentPos.second - 1)
            PipeConnection.SE -> listOf(currentPos.first + 1 to currentPos.second, currentPos.first to currentPos.second + 1)
            PipeConnection.EW -> listOf(currentPos.first to currentPos.second - 1, currentPos.first to currentPos.second + 1)
            PipeConnection.SW -> listOf(currentPos.first + 1 to currentPos.second, currentPos.first to currentPos.second - 1)
            else -> throw IllegalArgumentException()
        }
            .filter { it.first in rowRange && it.second in colRange }
            .filter { it !in path }
            .filter { maxSteps[it.first][it.second] > path.size }
            .map { path + it }

        for (p in possiblePaths) {
            val last = p.last()
            maxSteps[last.first][last.second] = p.size - 1
            paths += p
        }
    }

    return maxSteps
}

private fun Pair<Int,Int>.toExpanded() = this.first * 2 to this.second * 2 + 1

private fun Pair<Int,Int>.toOriginal(): Pair<Int,Int>? {
    if (this.first % 2 != 0 || this.second % 2 != 1) return null
    return this.first / 2 to (this.second - 1) / 2
}

private fun part1(input: List<String>): Int {
    val startCoord: Pair<Int, Int>
    val grid = buildList {
        val raw = input.map { parseLine(it).toMutableList() }
        startCoord = replaceStart(raw)
        addAll(raw.map { it.toList() })
    }

    val maxSteps = findLoop(grid, startCoord)

    return maxSteps.mapNotNull { row ->
        row.filter { it != Int.MAX_VALUE }.maxOrNull()
    }.maxOrNull() ?: throw IllegalArgumentException()
}

private fun part2(input: List<String>): Int {
    val startCoord: Pair<Int, Int>
    val grid = buildList {
        val raw = input.map { parseLine(it).toMutableList() }
        startCoord = replaceStart(raw)
        addAll(raw.map { it.toList() })
    }
    val rowRange = 0..<grid.size
    val colRange = 0..<grid[0].size

    val maxSteps = findLoop(grid, startCoord)
    val cleanedGrid = grid.mapIndexed { rowIdx, row ->
        row.mapIndexed { colIdx, col ->
            if (maxSteps[rowIdx][colIdx] == Int.MAX_VALUE) null else col
        }
    }
    cleanedGrid.forEach {
        println(it.map { if (it != null) '#' else '.' })
    }
    println()

    val expandedGrid = cleanedGrid.flatMap { row -> listOf(
        row.flatMap { when(it) {
            PipeConnection.NE -> listOf(null, PipeConnection.NE)
            PipeConnection.NS -> listOf(null, PipeConnection.NS)
            PipeConnection.NW -> listOf(PipeConnection.EW, PipeConnection.NW)
            PipeConnection.SE -> listOf(null, PipeConnection.SE)
            PipeConnection.EW -> listOf(PipeConnection.EW, PipeConnection.EW)
            PipeConnection.SW -> listOf(PipeConnection.EW, PipeConnection.SW)
            else -> listOf(null, null)
        } },
        row.flatMap { when(it) {
            PipeConnection.NE -> listOf(null, null)
            PipeConnection.NS -> listOf(null, PipeConnection.NS)
            PipeConnection.NW -> listOf(null, null)
            PipeConnection.SE -> listOf(null, PipeConnection.NS)
            PipeConnection.EW -> listOf(null, null)
            PipeConnection.SW -> listOf(null, PipeConnection.NS)
            else -> listOf(null, null)
        } }
    )}

    expandedGrid.forEach { line ->
        println(line.map { if (it != null) '#' else '.' })
    }
    val expandedRowRange = 0..<expandedGrid.size
    val expandedColRange = 0..<expandedGrid[0].size

    val emptyFields = cleanedGrid.flatMapIndexed { rowIdx, row ->
        row.withIndex().filter { it.value == null }.map { (rowIdx to it.index).toExpanded() }
    }.toMutableList()

    val travelled = mutableSetOf<Pair<Int, Int>>()
    val inner = mutableSetOf<Pair<Int, Int>>()
    val outer = mutableSetOf<Pair<Int, Int>>()
    outer@ while (emptyFields.isNotEmpty()) {
        val start = emptyFields.removeFirst()
        if (start in travelled) continue

        val reached = mutableSetOf(start)
        val next = mutableListOf(start)
        while (next.isNotEmpty()) {
            val pt = next.removeFirst()
            var possibleNext = listOf(
                    pt.first - 1 to pt.second,
                    pt.first + 1 to pt.second,
                    pt.first to pt.second - 1,
                    pt.first to pt.second + 1
                ).filter { it !in reached }

            if (possibleNext.any { it.first !in expandedRowRange || it.second !in expandedColRange }) {
                outer.addAll(reached)
                travelled.addAll(reached)
                continue@outer
            }

            possibleNext = possibleNext.filter { expandedGrid[it.first][it.second] == null }

            if (possibleNext.any { it in outer }) {
                outer.addAll(reached)
                outer.addAll(possibleNext)
                travelled.addAll(reached)
                travelled.addAll(possibleNext)
                continue@outer
            } else if (possibleNext.any { it in inner }) {
                inner.addAll(reached)
                inner.addAll(possibleNext)
                travelled.addAll(reached)
                travelled.addAll(possibleNext)
                continue@outer
            }

            reached.addAll(possibleNext)
            next.addAll(possibleNext)
        }

        inner.addAll(reached)
        travelled.addAll(reached)
    }

    val filteredInner = inner.mapNotNull { it.toOriginal() }
        .filter { it.first in rowRange && it.second in colRange }
    return filteredInner.size
}