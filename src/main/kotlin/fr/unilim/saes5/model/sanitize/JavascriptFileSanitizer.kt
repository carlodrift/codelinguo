package fr.unilim.saes5.model.sanitize

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.persistence.keyword.KeywordDao
import fr.unilim.saes5.persistence.keyword.TxtKeywordDao
import java.util.*

class JavascriptFileSanitizer : FileSanitizer() {

    override val regexString = """".*?"|'.*?'""".toRegex()
    override val regexWordSeparation = "[a-zA-Z]+".toRegex()
    override val regexCamelCase = "(?<!^)(?=[A-Z])".toRegex()
    override val reservedKeywords = loadReservedKeywords("javascript")
    override var lineCommentSymbol = "//"
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
        if (line.contains("/*")) {
            inBlockComment = true
            val processedLine = line.substringBefore("/*")
            return if (line.contains("*/")) {
                inBlockComment = false
                processedLine + line.substringAfter("*/")
            } else {
                processedLine
            }
        }
        return line
    }

    override fun handleBlockCommentEnd(line: String): String {
        if (line.contains("*/")) {
            inBlockComment = false
            return line.substringAfter("*/")
        }
        return ""
    }
}
