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
            val root = Directory("/")
            root.buildTree(lines)
            getTotalOfSmallSubdirectories(root).toString()
        }
        .run("24933642") { lines ->
            val root = Directory("/")
            root.buildTree(lines)
            val unusedSpace = 70000000 - root.totalSize
            val spaceNeeded = 30000000 - unusedSpace
            val directoryList = flattenDirectories(root)
            println(spaceNeeded)
            directoryList.filter { it.totalSize >= spaceNeeded }
                .onEach { println(it) }
                .minOf { it.totalSize }
                .toString()
        }
}

fun getTotalOfSmallSubdirectories(directory: Directory): Int {
    val totalOfSmallSubdirectories = directory.subDirectories.filter { it.totalSize <= 100_000 }.sumOf { it.totalSize }
    val totalOfTraversedSubdirectories = directory.subDirectories.sumOf { getTotalOfSmallSubdirectories(it) }
    return totalOfSmallSubdirectories + totalOfTraversedSubdirectories
}

fun flattenDirectories(directory: Directory): List<Directory> {
    if (directory.subDirectories.isEmpty()) {
        return listOf(directory)
    } else {
        return directory.subDirectories.map { flattenDirectories(it) }
            .flatten() + directory
    }
}

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

    fun printTree(depth: Int = 0) {
        val indent = "\t".repeat(depth) + "|-"
        println("$indent ${this.name} ${this.totalSize}")
        this.subDirectories.forEach {
            it.printTree(depth + 1)
        }
    }

    fun buildTree(lines: List<String>) {
        val commandPattern = Regex("""^\$ ([^ ]+) ?(.*)$""")
        val directoryPattern = Regex("""^dir (.+)$""")
        val filePattern = Regex("""^(\d+) (.+)$""")

        var currentDirectory = this
        lines.forEach { line ->
            val commandMatch = commandPattern.matchEntire(line)
            val directoryMatch = if (commandMatch == null) directoryPattern.matchEntire((line)) else null
            val fileMatch =
                if (commandMatch == null && directoryMatch == null) filePattern.matchEntire(line) else null

            if (commandMatch != null) {
                val command =
                    commandMatch.groups[1]?.value ?: throw IllegalStateException("command name not found in $line")
                when (command) {
                    "cd" -> {
                        val directoryName = commandMatch.groups[2]?.value
                        currentDirectory = when (directoryName) {
                            "/" -> this
                            ".." -> currentDirectory.parentDirectory
                                ?: throw IllegalStateException("${currentDirectory.name} has no parent directory")
                            else -> currentDirectory.subDirectories.first { directory -> directory.name == directoryName }
                        }
                    }
                    else -> { // no op }
                    }
                }
            } else if (directoryMatch != null) {
                val name = directoryMatch.groups[1]?.value ?: throw IllegalStateException("name not found in $line")
                val newDirectory = Directory(name, currentDirectory)
                currentDirectory.subDirectories += newDirectory
            } else if (fileMatch != null) {
                val name = fileMatch.groups[2]?.value ?: throw IllegalStateException("name not found in $line")
                val size =
                    fileMatch.groups[1]?.value?.toInt() ?: throw IllegalStateException("size not found in $line")
                val newFile = File(name, size)
                currentDirectory.files += newFile
            } else {
                throw IllegalStateException("unable to parse $line")
            }
        }
    }

    val totalSize: Int
        get() = files.sumOf { it.size } + subDirectories.sumOf { it.totalSize }
}