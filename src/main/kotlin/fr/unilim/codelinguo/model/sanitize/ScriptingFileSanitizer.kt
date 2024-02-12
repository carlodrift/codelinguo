package fr.unilim.codelinguo.model.sanitize

import fr.unilim.codelinguo.model.Word

abstract class ScriptingFileSanitizer : FileSanitizer() {

    override var inBlockComment = false

    override fun sanitizeLines(lines: List<String>, path: String): List<Word> {
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
