package fr.unilim.saes5.model.reader

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.sanitize.JavaFileSanitizer
import java.io.File
import java.nio.file.Files

class JavaFileReader : IRead {
    override fun read(path: String): List<Word> {

        val words = mutableListOf<Word>()

        File(path).walk().forEach {
            if (!Files.isDirectory(it.toPath())) {
                words.addAll(readOne(it.path))
            }
        }

        return words

    }

    override fun read(paths: List<String>): List<Word> {
        val words = mutableListOf<Word>()
        for (path in paths) {
            words.addAll(readOne(path));
        }

        return words;
    }

    override fun readOne(path: String): List<Word> {
        if (path.endsWith(".java")) {
            val lines = File(path).bufferedReader().readLines()
            val words = JavaFileSanitizer().sanitizeLines(lines)
            words.forEach { it.fileName = path.substringAfterLast("/", path) }
            return words
        }

        return emptyList()
    }
}