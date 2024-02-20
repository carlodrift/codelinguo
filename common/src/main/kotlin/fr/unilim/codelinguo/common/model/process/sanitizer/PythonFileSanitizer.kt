package fr.unilim.codelinguo.common.model.process.sanitizer

class PythonFileSanitizer : ScriptingFileSanitizer() {

    companion object {
        private const val BLOCK_COMMENT = "\"\"\""
        private const val ALTERNATE_BLOCK_COMMENT = "'''"
    }

    override val regexString =
        "$BLOCK_COMMENT.*?$BLOCK_COMMENT|$ALTERNATE_BLOCK_COMMENT.*?$ALTERNATE_BLOCK_COMMENT|\".*?\"|'.*?'".toRegex()
    override val reservedKeywords = loadReservedKeywords("python", "sanitizer")
    override var lineCommentSymbol = "#"

    override fun handleBlockCommentStart(line: String): String {
        if ((line.contains(BLOCK_COMMENT) || line.contains(ALTERNATE_BLOCK_COMMENT)) && !inBlockComment) {
            inBlockComment = true
            return line.substringBefore(BLOCK_COMMENT).substringBefore(ALTERNATE_BLOCK_COMMENT)
        } else if (inBlockComment) {
            return ""
        }
        return line
    }

    override fun handleBlockCommentEnd(line: String): String {
        if (inBlockComment && (line.contains(BLOCK_COMMENT) || line.contains(ALTERNATE_BLOCK_COMMENT))) {
            inBlockComment = false
            return line.substringAfterLast(BLOCK_COMMENT).substringAfterLast(ALTERNATE_BLOCK_COMMENT)
        } else if (inBlockComment) {
            return ""
        }
        return line
    }
}
