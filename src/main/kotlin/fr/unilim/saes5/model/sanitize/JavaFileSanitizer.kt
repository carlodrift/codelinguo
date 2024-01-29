package fr.unilim.saes5.model.sanitize

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.persistence.keyword.KeywordDao
import fr.unilim.saes5.persistence.keyword.TxtKeywordDao
import java.util.*


class JavaFileSanitizer : FileSanitizer() {

    companion object {
        private val RESERVED_KEYWORDS = loadReservedKeywords()

        private fun loadReservedKeywords(): Set<String> {
            val loader: KeywordDao = TxtKeywordDao()
            return loader.retrieve("java")
        }

        private val REGEX_WORD_SEPARATION = "[a-zA-Z]+".toRegex()
        private val REGEX_STRING = "\".*\"".toRegex()
        private val REGEX_CAMEL_CASE = "(?<!^)(?=[A-Z])".toRegex()
        private const val PACKAGE_DECLARATION = "package"
        private const val IMPORT_DECLARATION = "import"
    }

    private var inBlockComment = false

    override fun sanitizeLines(lines: List<String>): List<Word> {
        val words = mutableListOf<Word>()

        lines.forEach { line ->
            var processedLine = processLineForComments(line)
            if (processedLine.isNotBlank()
                && !checkPackageDeclaration(processedLine) && !checkImportDeclaration(processedLine)
            ) {
                processedLine = removeStringLiterals(processedLine)
                extractWords(processedLine, words)
            }
        }

        return words
    }

    private fun checkPackageDeclaration(line: String): Boolean {
        return line.startsWith("$PACKAGE_DECLARATION ")
    }

    private fun checkImportDeclaration(line: String): Boolean {
        return line.startsWith("$IMPORT_DECLARATION ")
    }

    private fun processLineForComments(line: String): String {
        if (inBlockComment) {
            return handleBlockCommentEnd(line)
        }

        val processedLine = handleBlockCommentStart(line)

        return handleLineComment(processedLine)
    }

    private fun handleBlockCommentStart(line: String): String {
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

    private fun handleBlockCommentEnd(line: String): String {
        if (line.contains("*/")) {
            inBlockComment = false
            return line.substringAfter("*/")
        }
        return ""
    }

    private fun handleLineComment(line: String): String {
        if (!inBlockComment && line.contains("//")) {
            return line.substringBefore("//")
        }
        return line
    }

    private fun removeStringLiterals(line: String): String = line.replace(REGEX_STRING, "")

    private fun extractWords(line: String, words: MutableList<Word>) {
        REGEX_WORD_SEPARATION.findAll(line).forEach { match ->
            val word = match.value
            splitCamelCase(word).forEach { splitWord ->
                if (splitWord !in RESERVED_KEYWORDS) {
                    words.add(Word(splitWord))
                }
            }
        }
    }

    private fun splitCamelCase(word: String): List<String> {
        return word.split(REGEX_CAMEL_CASE)
            .map { it.lowercase(Locale.getDefault()) }
            .filter { it.length > 2 }
    }
}