package fr.unilim.codelinguo.model.reader

import fr.unilim.codelinguo.model.Word
import fr.unilim.codelinguo.model.sanitize.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class FileReader : IRead {
    private val fileSanitizers = mapOf(
        ".java" to JavaFileSanitizer(),
        ".kt" to KotlinFileSanitizer(),
        ".py" to PythonFileSanitizer(),
        ".js" to JavascriptFileSanitizer(),
        ".html" to HtmlFileSanitizer()
    )

    override fun read(path: String): List<Word> {
        val file = File(path)
        if (file.isDirectory) {
            return file.walk()
                .filter { isPathValidForProcessing(it.toPath()) }
                .flatMap { readOne(it.path).asSequence() }
                .toList()
        }
        return if (isPathValidForProcessing(file.toPath())) readOne(path) else emptyList()
    }

    override fun read(paths: List<String>): List<Word> = paths.flatMap { read(it) }

    override fun readOne(path: String): List<Word> {
        val extension = fileSanitizers.keys.find { path.endsWith(it) } ?: return emptyList()
        val sanitizer = fileSanitizers[extension] ?: return emptyList()
        return processFile(path, sanitizer)
    }

    private fun isPathValidForProcessing(path: Path): Boolean {
        val pathStr = path.toString()
        return Files.isRegularFile(path)
                && fileSanitizers.keys.any { pathStr.endsWith(it) }
                && !pathStr.contains("${File.separator}node_modules${File.separator}")
    }

    private fun processFile(path: String, sanitizer: FileSanitizer): List<Word> {
        val lines = File(path).useLines { it.toList() }
        return sanitizer.sanitizeLines(lines).onEach {
            it.fileName = path.substringAfterLast(File.separator).substringBeforeLast(".")
        }
    }
}
