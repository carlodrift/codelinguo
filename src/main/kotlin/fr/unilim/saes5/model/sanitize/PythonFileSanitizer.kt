package fr.unilim.saes5.model.sanitize

class PythonFileSanitizer : ScriptingFileSanitizer() {

    override val regexString = "\"\"\".*?\"\"\"|'''.*?'''|\".*?\"|'.*?'".toRegex()
    override val reservedKeywords = loadReservedKeywords("python")
    override var lineCommentSymbol = "#"

    override fun handleBlockCommentStart(line: String): String {
        if ((line.contains("\"\"\"") || line.contains("'''")) && !inBlockComment) {
            inBlockComment = true
            return line.substringBefore("\"\"\"").substringBefore("'''")
        } else if (inBlockComment) {
            return ""
        }
        return line
    }

    override fun handleBlockCommentEnd(line: String): String {
        if (inBlockComment && (line.contains("\"\"\"") || line.contains("'''"))) {
            inBlockComment = false
            return line.substringAfterLast("\"\"\"").substringAfterLast("'''")
        } else if (inBlockComment) {
            return ""
        }
        return line
    }
}
