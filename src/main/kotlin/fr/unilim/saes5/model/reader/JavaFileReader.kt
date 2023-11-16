package fr.unilim.saes5.model.reader

import fr.unilim.saes5.model.interfaces.IRead
import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.sanitize.JavaFileSanitizer
import java.io.File

class JavaFileReader : IRead {
    override fun read(): List<Word>? {
        TODO("Not yet implemented")
    }

    override fun readOne(path:String): List<Word> {
        val lines = File(path).bufferedReader().readLines();
        return JavaFileSanitizer().sanitizeLines(lines);
    }
}
