package fr.unilim.codelinguo.common.model.process.sanitizer

open class JavascriptFileSanitizer : ScriptingFileSanitizer() {

    companion object {
        private const val BLOCK_COMMENT_START = "/*"
        private const val BLOCK_COMMENT_END = "*/"
    }

    override val regexString = """".*?"|'.*?'""".toRegex()
    override val reservedKeywords = loadReservedKeywords("javascript")
    override var lineCommentSymbol = "//"

    override fun handleBlockCommentStart(line: String): String {
        if (line.contains(BLOCK_COMMENT_START)) {
            inBlockComment = true
            val processedLine = line.substringBefore(BLOCK_COMMENT_START)
            if (line.contains(BLOCK_COMMENT_END)) {
                inBlockComment = false
                return processedLine + line.substringAfter(BLOCK_COMMENT_END)
            }
            return processedLine
        }
        return line
    }

    override fun handleBlockCommentEnd(line: String): String {
        if (inBlockComment) {
            if (line.contains(BLOCK_COMMENT_END)) {
                inBlockComment = false
                return line.substringAfter(BLOCK_COMMENT_END)
            }
            return ""
        }
        return line
    }
}
