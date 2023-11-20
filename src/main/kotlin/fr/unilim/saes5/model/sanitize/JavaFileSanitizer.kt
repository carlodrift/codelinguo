package fr.unilim.saes5.model.sanitize

import fr.unilim.saes5.model.Word
import java.util.*

class JavaFileSanitizer : FileSanitizer() {

    companion object {
        private val JAVA_RESERVED_KEYWORDS = setOf(
            "String", "i", "j", "null", "int", "abstract", "true", "false", "equals", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "println"
        )

        private val REGEX_WORD_SEPARATION = "[a-zA-Z]+".toRegex()
        private val REGEX_JAVA_STRING = "\".*\"".toRegex()
        private val REGEX_CAMEL_CASE = "(?<!^)(?=[A-Z])".toRegex()
    }

    private var inBlockComment = false

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

    private fun processLineForComments(line: String): String {
        var processedLine = line

        when {
            inBlockComment && line.contains("*/") -> {
                inBlockComment = false
                processedLine = line.substringAfter("*/")
            }
            line.contains("/*") -> {
                inBlockComment = true
                processedLine = line.substringBefore("/*")
            }
            line.contains("//") -> processedLine = line.substringBefore("//")
        }

        return processedLine
    }

    private fun removeStringLiterals(line: String): String = line.replace(REGEX_JAVA_STRING, "")

    private fun extractWords(line: String, words: MutableList<Word>) {
        REGEX_WORD_SEPARATION.findAll(line).forEach { match ->
            val word = match.value
            if (word !in JAVA_RESERVED_KEYWORDS) {
                splitCamelCase(word).forEach { splitWord ->
                    words.add(Word(splitWord))
                }
            }
        }
    }

    private fun splitCamelCase(word: String): List<String> =
        word.split(REGEX_CAMEL_CASE).map { it.lowercase(Locale.getDefault()) }
}