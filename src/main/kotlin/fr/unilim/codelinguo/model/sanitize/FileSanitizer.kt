package fr.unilim.codelinguo.model.sanitize

import fr.unilim.codelinguo.model.Word
import fr.unilim.codelinguo.persistence.keyword.KeywordDao
import fr.unilim.codelinguo.persistence.keyword.TxtKeywordDao
import java.util.*

abstract class FileSanitizer {
    abstract val regexString: Regex
    abstract val reservedKeywords: Set<String>
    abstract val lineCommentSymbol: String
    abstract var inBlockComment: Boolean

    private val regexCamelCase = "(?<!^)(?=[A-Z])".toRegex()
    private val regexWordSeparation = "[a-zA-Z]+".toRegex()

    abstract fun sanitizeLines(lines: List<String>, path: String): List<Word>
    abstract fun handleBlockCommentEnd(line: String): String
    abstract fun handleBlockCommentStart(line: String): String

    fun loadReservedKeywords(language: String): Set<String> {
        val loader: KeywordDao = TxtKeywordDao()
        return loader.retrieve(language)
    }

    fun removeStringLiterals(line: String): String = line.replace(regexString, "")

    fun extractWords(line: String, words: MutableList<Word>) {
        regexWordSeparation.findAll(line).forEach { match ->
            val word = match.value
            splitCamelCase(word).forEach { splitWord ->
                if (splitWord !in reservedKeywords) {
                    words.add(Word(splitWord))
                }
            }
        }
    }

    fun processLineForComments(line: String): String {
        if (inBlockComment) {
            return handleBlockCommentEnd(line)
        }

        val processedLine = handleBlockCommentStart(line)

        return handleLineComment(processedLine)
    }

    private fun handleLineComment(line: String): String {
        if (!inBlockComment && line.contains(lineCommentSymbol)) {
            return line.substringBefore(lineCommentSymbol)
        }
        return line
    }

    private fun splitCamelCase(word: String): List<String> {
        return word.split(regexCamelCase)
            .map { it.lowercase(Locale.getDefault()) }
            .filter { it.length > 2 }
    }
}