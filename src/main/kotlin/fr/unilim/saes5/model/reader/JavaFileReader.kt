package fr.unilim.saes5.model.reader

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.interfaces.IRead
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

    override fun readOne(path: String): List<Word> {
        println(path)

        if (path.endsWith(".java")) {
            val lines = File(path).bufferedReader().readLines();
            return JavaFileSanitizer().sanitizeLines(lines);
        }

        println("Mauvais format de fichier !")
        return emptyList<Word>()
    }
}