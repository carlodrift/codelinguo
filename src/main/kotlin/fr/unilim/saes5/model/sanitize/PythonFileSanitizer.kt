package fr.unilim.saes5.model.sanitize

import fr.unilim.saes5.model.Word

class PythonFileSanitizer : FileSanitizer() {

    override val regexString = "\"\"\".*?\"\"\"|'''.*?'''|\".*?\"|'.*?'".toRegex()
    override val regexWordSeparation = "[a-zA-Z]+".toRegex()
    override val regexCamelCase = "(?<!^)(?=[A-Z])".toRegex()
    override val reservedKeywords = loadReservedKeywords("python")
    override var lineCommentSymbol = "#"
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

    override fun handleBlockCommentStart(line: String): String {
        if ((line.contains("\"\"\"") || line.contains("'''")) && !inBlockComment) {
            inBlockComment = true
            return line.substringBefore("\"\"\"").substringBefore("'''")
        } else if (inBlockComment) {
            return ""
        }
        return line
    }

    override fun handleBlockCommentEnd(line: String): String {
        if (inBlockComment && (line.contains("\"\"\"") || line.contains("'''"))) {
            inBlockComment = false
            return line.substringAfterLast("\"\"\"").substringAfterLast("'''")
        } else if (inBlockComment) {
            return ""
        }
        return line
    }
}
