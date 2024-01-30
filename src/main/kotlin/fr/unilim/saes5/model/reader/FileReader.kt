package fr.unilim.saes5.model.reader

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.sanitize.*
import java.io.File
import java.nio.file.Files

class FileReader : IRead {
    override fun read(path: String): List<Word> {
        val words = mutableListOf<Word>()

        File(path).walk().forEach { file ->
            if (!Files.isDirectory(file.toPath()) && !file.path.contains("/node_modules/")) {
                words.addAll(readOne(file.path))
            }
        }

        return words
    }

    override fun read(paths: List<String>): List<Word> {
        val words = mutableListOf<Word>()
        for (path in paths) {
            words.addAll(readOne(path))
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

    private fun processFile(path: String, sanitizer: FileSanitizer): List<Word> {
        val lines = File(path).bufferedReader().readLines()
        val words = sanitizer.sanitizeLines(lines)
        words.forEach {
            it.fileName = path.substringAfterLast("/", path).substringBeforeLast(".")
        }
        return words
    }
}
