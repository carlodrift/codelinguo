package fr.unilim.codelinguo.model.sanitize

import fr.unilim.codelinguo.model.Word
import fr.unilim.codelinguo.persistence.keyword.KeywordDao
import fr.unilim.codelinguo.persistence.keyword.TxtKeywordDao
import java.util.*

abstract class FileSanitizer {
    protected open val regexString: Regex = "".toRegex()
    protected open val lineCommentSymbol: String = ""
    protected open var inBlockComment: Boolean = false

    abstract val reservedKeywords: Set<String>

    private val regexCamelCase = "(?<!^)(?=[A-Z])".toRegex()
    private val regexWordSeparation = "[a-zA-Z]+".toRegex()

    abstract fun sanitizeFile(path: String): List<Word>
    protected open fun handleBlockCommentEnd(line: String): String {
        return ""
    }

    protected open fun handleBlockCommentStart(line: String): String {
        return ""
    }

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