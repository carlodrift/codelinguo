package fr.unilim.codelinguo.common.model.reader

import fr.unilim.codelinguo.common.model.Word
import fr.unilim.codelinguo.common.model.process.FileProcessor
import fr.unilim.codelinguo.common.model.process.parser.JavaFileParser
import fr.unilim.codelinguo.common.model.process.sanitizer.HtmlFileSanitizer
import fr.unilim.codelinguo.common.model.process.sanitizer.JavascriptFileSanitizer
import fr.unilim.codelinguo.common.model.process.sanitizer.KotlinFileSanitizer
import fr.unilim.codelinguo.common.model.process.sanitizer.PythonFileSanitizer
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class FileReader : IRead {
    private val fileSanitizers = mapOf(
        ".java" to JavaFileParser(),
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
                && !pathStr.endsWith("module-info.java")
                && !pathStr.endsWith("package-info.java")
    }

    private fun processFile(path: String, sanitizer: FileProcessor): List<Word> {
        return sanitizer.processFile(path).onEach {
            it.fileName = path.substringAfterLast(File.separator).substringBeforeLast(".")
        }
    }
}