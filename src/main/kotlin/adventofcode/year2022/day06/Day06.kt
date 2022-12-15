package adventofcode.year2022.day06

import adventofcode.DayRunner

fun main() {
    val testInput = """
        mjqjpqmgbljsphdztnvjfqwrcgsmlb
    """.trimIndent()

    DayRunner("06", testInput)
        .run("7") { lines ->
            getLastIndexOfSequenceWithDistinctCharacters(lines.first(), 4).toString()
        }
        .run("19") { lines ->
            getLastIndexOfSequenceWithDistinctCharacters(lines.first(), 14).toString()
        }
}

private fun getLastIndexOfSequenceWithDistinctCharacters(it: String, numberOfCharacters: Int): Int {
    var lastIndex = numberOfCharacters
    while (it.toList().subList(lastIndex - numberOfCharacters, lastIndex).distinct().size < numberOfCharacters) {
        lastIndex += 1
    }
    return lastIndex
}