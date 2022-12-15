package adventofcode.year2022.day07

import adventofcode.DayRunner
import java.lang.IllegalStateException

fun main() {
    val testInput = """
        ${'$'} cd /
        ${'$'} ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        ${'$'} cd a
        ${'$'} ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        ${'$'} cd e
        ${'$'} ls
        584 i
        ${'$'} cd ..
        ${'$'} cd ..
        ${'$'} cd d
        ${'$'} ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k
    """.trimIndent()

    DayRunner("07", testInput)
        .run("95437") { lines ->
            val commandPattern = Regex("""\$ ([^ ]+) (.*)""")
            val outputPattern = Regex("""([^$ ]+) (.*)""")
            val root = Directory("/")
            var currentDirectory = root
            lines.forEach { line ->
                val commandParts = commandPattern.matchEntire(line)
                if (commandParts == null) {
                    val outputParts = outputPattern.matchEntire(line)
                    val qualifier = commandParts.groups[1]?.value
                    val name =
                        commandParts.groups[2]?.value ?: throw IllegalStateException("name not found in $line")
                    if (qualifier == "dir") {
                        val newDirectory = Directory(name, currentDirectory)
                        currentDirectory.subDirectories += newDirectory
                    } else {
                        val newFile = File(
                            name,
                            qualifier?.toInt() ?: throw IllegalStateException("invalid size in $line")
                        )
                        currentDirectory.files += newFile
                    }
                } else {
                    val command = commandParts.groups[1]?.value
                        ?: throw IllegalStateException("group name not found for cd command")
                    when (command) {
                        "cd" -> {
                            val directoryName = commandParts.groups[2]?.value
                            currentDirectory = when (directoryName) {
                                "/" -> {
                                    root
                                }
                                ".." -> {
                                    currentDirectory.parentDirectory
                                        ?: throw IllegalStateException("no parent directory")
                                }
                                else -> {
                                    currentDirectory.subDirectories.first { directory -> directory.name == directoryName }
                                }
                            }
                        }
                        "ls" -> {
                            // no op
                        }
                        else -> {
                        }
                    }
                }
            }
            getTotalOfSmallSubdirectories(root).toString()
        }
        .run("24933642") { lines ->
            ""
        }
}

fun getTotalOfSmallSubdirectories(directory: Directory): Int {
    val totalOfSmallSubdirectories = directory.subDirectories.filter { it.totalSize <= 100_000 }.sumOf { it.totalSize }
    val totalOfTraversedSubdirectories = directory.subDirectories.sumOf { getTotalOfSmallSubdirectories(it) }
    return totalOfSmallSubdirectories + totalOfTraversedSubdirectories
}

data class Command(val name: String, val qualifier: String)

data class File(val name: String, val size: Int)

data class Directory(
    val name: String,
    val parentDirectory: Directory? = null,
    val subDirectories: MutableList<Directory> = mutableListOf(),
    val files: MutableList<File> = mutableListOf()
) {
    override fun toString(): String {
        return "Directory(name='$name', parentDirectory=${parentDirectory?.name}, subDirectories=$subDirectories, files=$files)"
    }

    val totalSize: Int
        get() = files.sumOf { it.size } + subDirectories.sumOf { it.totalSize }
}