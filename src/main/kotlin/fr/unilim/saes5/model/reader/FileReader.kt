package fr.unilim.saes5.model.reader

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.sanitize.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class FileReader : IRead {
    private val supportedExtensions = listOf(".java", ".kt", ".py", ".js", ".html")

    override fun read(path: String): List<Word> {
        val words = mutableListOf<Word>()

        File(path).walk().forEach { file ->
            if (isPathValidForProcessing(file.toPath())) {
                words.addAll(readOne(file.path))
            }
        }

        return words
    }

    override fun read(paths: List<String>): List<Word> {
        val words = mutableListOf<Word>()
        for (path in paths) {
            if (isPathValidForProcessing(Path.of(path))) {
                words.addAll(readOne(path))
            }
        }

        return words
    }

    override fun readOne(path: String): List<Word> {
        return when {
            path.endsWith(".java") -> processFile(path, JavaFileSanitizer())
            path.endsWith(".kt") -> processFile(path, KotlinFileSanitizer())
            path.endsWith(".py") -> processFile(path, PythonFileSanitizer())
            path.endsWith(".js") -> processFile(path, JavascriptFileSanitizer())
            path.endsWith(".html") -> processFile(path, HtmlFileSanitizer())
            else -> emptyList()
        }
    }

    private fun isPathValidForProcessing(path: Path): Boolean {
        val pathStr = path.toString()
        return !Files.isDirectory(path)
                && supportedExtensions.any { pathStr.endsWith(it) }
                && !pathStr.contains(File.separator + "node_modules" + File.separator)
    }

    private fun processFile(path: String, sanitizer: FileSanitizer): List<Word> {
        val lines = File(path).bufferedReader().readLines()
        val words = sanitizer.sanitizeLines(lines)
        words.forEach {
            it.fileName = path.substringAfterLast("/", path).substringBeforeLast(".")
        }
        return words
    }
}
