package fr.unilim.codelinguo.model.process.sanitizer

import fr.unilim.codelinguo.model.Word
import fr.unilim.codelinguo.model.process.FileProcessor
import java.util.*

abstract class FileSanitizer : FileProcessor() {
    protected open val regexString: Regex = "".toRegex()
    protected open val lineCommentSymbol: String = ""
    protected open var inBlockComment: Boolean = false

    private val regexCamelCase = "(?<!^)(?=[A-Z])".toRegex()
    private val regexWordSeparation = "[a-zA-Z]+".toRegex()

    protected open fun handleBlockCommentEnd(line: String): String {
        return ""
    }

    protected open fun handleBlockCommentStart(line: String): String {
        return ""
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