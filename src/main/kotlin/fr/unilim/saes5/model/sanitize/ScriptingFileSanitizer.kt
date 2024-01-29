package fr.unilim.saes5.model.sanitize

import fr.unilim.saes5.model.Word

abstract class ScriptingFileSanitizer : FileSanitizer() {

    override var inBlockComment = false

    override fun sanitizeLines(lines: List<String>): List<Word> {
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
