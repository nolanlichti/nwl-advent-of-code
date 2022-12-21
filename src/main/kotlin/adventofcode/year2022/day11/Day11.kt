package adventofcode.year2022.day11

import adventofcode.DayRunner

fun main() {
    DayRunner("11", testInput)
        .run("10605") { lines ->
            val mutableLines = lines.toMutableList()
            val monkeys = mutableListOf<Monkey>()
            while (mutableLines.size > 6) {
                monkeys += mutableLines.getNextMonkey()
            }
            monkeys.toString()
        }
}

data class Monkey(
    val items: MutableList<Int>,
    val operation: (old: Int) -> Int,
    val test: (value: Int) -> Boolean,
    val targetIfTestPasses: Int,
    val targetIfTestFails: Int
)

fun MutableList<String>.getNextMonkey(): Monkey {
    while (this.first().isBlank()) {
        this.removeFirst()
    }

    val monkeyNumber = Regex("""Monkey (\d+):""")
        .matchEntire(this.removeFirst().trim())!!
        .groupValues[1]
        .toInt()

    val startingItems = Regex("""Starting items: ([0-9, ]+)""")
        .matchEntire(this.removeFirst().trim())!!
        .groupValues[1]
        .split(", ")
        .map { it.toInt() }

    val operation = Regex("""Operation: new = old ([*+]) (old|\d+)""")
        .matchEntire(this.removeFirst().trim())!!
        .destructured
        .let { (operator, operand) ->
            { value: Int ->
                val actualOperand = if (operand == "old") value else operand.toInt()
                if (operator == "*") value * actualOperand else value + actualOperand
            }
        }

    val test = Regex("""Test: divisible by (\d+)""")
        .matchEntire(this.removeFirst().trim())!!
        .groupValues[1]
        .toInt()
        .let {
            { value: Int ->
                (value % it) == 0
            }
        }

    val targetIfTrue = Regex("""If true: throw to monkey (\d+)""")
        .matchEntire(this.removeFirst().trim())!!
        .groupValues[1]
        .toInt()

    val targetIfFalse = Regex("""If false: throw to monkey (\d+)""")
        .matchEntire(this.removeFirst().trim())!!
        .groupValues[1]
        .toInt()

    return Monkey(
        items = startingItems.toMutableList(),
        operation = operation,
        test = test,
        targetIfTestPasses = targetIfTrue,
        targetIfTestFails = targetIfFalse
    )
}

val testInput = """
Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1
""".trimIndent()