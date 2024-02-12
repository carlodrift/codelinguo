package fr.unilim.codelinguo.model.sanitize

import fr.unilim.codelinguo.model.Word
import java.io.File

abstract class ScriptingFileSanitizer : FileSanitizer() {

    override fun sanitizeFile(path: String): List<Word> {
        val lines = File(path).useLines { it.toList() }
        val words = mutableListOf<Word>()

        lines.forEach { line ->
            var processedLine = processLineForComments(line)
            if (processedLine.isNotBlank()) {
                processedLine = removeStringLiterals(processedLine)
                extractWords(processedLine, words)
            }
        }

        return words
    }
}
