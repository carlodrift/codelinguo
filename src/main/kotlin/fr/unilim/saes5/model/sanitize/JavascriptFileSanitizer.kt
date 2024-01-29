package fr.unilim.saes5.model.sanitize

open class JavascriptFileSanitizer : ScriptingFileSanitizer() {

    override val regexString = """".*?"|'.*?'""".toRegex()
    override val reservedKeywords = loadReservedKeywords("javascript")
    override var lineCommentSymbol = "//"

    override fun handleBlockCommentStart(line: String): String {
        if (line.contains("/*")) {
            inBlockComment = true
            val processedLine = line.substringBefore("/*")
            if (line.contains("*/")) {
                inBlockComment = false
                return processedLine + line.substringAfter("*/")
            }
            return processedLine
        }
        return line
    }

    override fun handleBlockCommentEnd(line: String): String {
        if (inBlockComment) {
            if (line.contains("*/")) {
                inBlockComment = false
                return line.substringAfter("*/")
            }
            return ""
        }
        return line
    }
}
