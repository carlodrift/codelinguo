package fr.unilim.codelinguo.common.model.reader

import fr.unilim.codelinguo.common.model.Word
import fr.unilim.codelinguo.common.model.process.FileProcessor
import fr.unilim.codelinguo.common.model.process.parser.JavaFileParser
import fr.unilim.codelinguo.common.model.process.sanitizer.jvm.KotlinFileSanitizer
import fr.unilim.codelinguo.common.model.process.sanitizer.jvm.ScalaFileSanitizer
import fr.unilim.codelinguo.common.model.process.sanitizer.scripting.HtmlFileSanitizer
import fr.unilim.codelinguo.common.model.process.sanitizer.scripting.JavascriptFileSanitizer
import fr.unilim.codelinguo.common.model.process.sanitizer.scripting.PythonFileSanitizer
import kotlinx.coroutines.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class FileReader : IRead {
    private val fileSanitizers = mapOf(
        ".java" to JavaFileParser(),
        ".kt" to KotlinFileSanitizer(),
        ".py" to PythonFileSanitizer(),
        ".js" to JavascriptFileSanitizer(),
        ".html" to HtmlFileSanitizer(),
        ".scala" to ScalaFileSanitizer(),
    )

    override fun read(path: String): List<Word> = runBlocking {
        val file = File(path)
        if (file.isDirectory) {
            file.walk()
                .filter { isPathValidForProcessing(it.toPath()) }
                .toList()
                .parallelMap { readOne(it.path) }
                .flatten()
        } else {
            if (isPathValidForProcessing(file.toPath())) readOne(path) else emptyList()
        }
    }

    override fun read(paths: List<String>): List<Word> = runBlocking {
        paths.parallelMap { read(it) }.flatten()
    }

    private suspend fun <A, B> Iterable<A>.parallelMap(f: suspend (A) -> B): List<B> = coroutineScope {
        map { async(Dispatchers.IO) { f(it) } }.awaitAll()
    }

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
