package adventofcode

import java.io.FileReader

class DayRunner(val day: String, val testInput: String) {
    fun run(expectedTestResult: String, result: (List<String>) -> String): DayRunner {
        val testResult = result(testInput.lines())
        check(testResult == expectedTestResult) { "$testResult is not $expectedTestResult" }
        println("test passed")
        val input = FileReader("src/main/kotlin/adventofcode/year2022/day$day/input.txt").readLines()
        println(result(input))
        return this
    }

}