package fr.unilim.codelinguo.model.sanitize

import fr.unilim.codelinguo.model.Word
import java.io.File


open class JavaFileSanitizer : FileSanitizer() {

    override val regexString = "\".*\"".toRegex()
    override val reservedKeywords = loadReservedKeywords("java")
    override val lineCommentSymbol = "//"

    companion object {
        private const val PACKAGE_DECLARATION = "package"
        private const val IMPORT_DECLARATION = "import"
        private const val BLOCK_COMMENT_START = "/*"
        private const val BLOCK_COMMENT_END = "*/"
    }

    override fun sanitizeFile(path: String): List<Word> {
        val lines = File(path).useLines { it.toList() }
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

    override fun handleBlockCommentStart(line: String): String {
        if (line.contains(BLOCK_COMMENT_START)) {
            inBlockComment = true
            val processedLine = line.substringBefore(BLOCK_COMMENT_START)
            return if (line.contains(BLOCK_COMMENT_END)) {
                inBlockComment = false
                processedLine + line.substringAfter(BLOCK_COMMENT_END)
            } else {
                processedLine
            }
        }
        return line
    }

    override fun handleBlockCommentEnd(line: String): String {
        if (line.contains(BLOCK_COMMENT_END)) {
            inBlockComment = false
            return line.substringAfter(BLOCK_COMMENT_END)
        }
        return ""
    }
}