package fr.unilim.saes5.model.sanitize

import fr.unilim.saes5.model.Word
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class JavaFileSanitizer : FileSanitizer() {

    companion object {
        private val JAVA_RESERVED_KEYWORDS = loadJavaReservedKeywords()

        private fun loadJavaReservedKeywords(): Set<String> {
            val path = Paths.get(KEYWORDS_FILE_PATH)
            if (Files.notExists(path)) {
                Files.createFile(path)
            }
            return Files.readAllLines(path, StandardCharsets.UTF_8).toSet()
        }

        private val REGEX_WORD_SEPARATION = "[a-zA-Z]+".toRegex()
        private val REGEX_JAVA_STRING = "\".*\"".toRegex()
        private val REGEX_CAMEL_CASE = "(?<!^)(?=[A-Z])".toRegex()
        private const val JAVA_PACKAGE_DECLARATION = "package "
        private const val KEYWORDS_FILE_PATH = "java_reserved_keywords.txt"
    }

    private var inBlockComment = false

    override fun sanitizeLines(lines: List<String>): List<Word> {
        val words = mutableListOf<Word>()

        lines.forEach { line ->
            var processedLine = processLineForComments(line)
            if (processedLine.isNotBlank()
                && !checkPackageDeclaration(processedLine))
            {
                processedLine = removeStringLiterals(processedLine)
                extractWords(processedLine, words)
            }
        }

        return words
    }

    private fun checkPackageDeclaration(line: String): Boolean {
        return line.startsWith(JAVA_PACKAGE_DECLARATION)
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

    private fun removeStringLiterals(line: String): String = line.replace(REGEX_JAVA_STRING, "")

    private fun extractWords(line: String, words: MutableList<Word>) {
        REGEX_WORD_SEPARATION.findAll(line).forEach { match ->
            val word = match.value
            splitCamelCase(word).forEach { splitWord ->
                if (splitWord !in JAVA_RESERVED_KEYWORDS) {
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