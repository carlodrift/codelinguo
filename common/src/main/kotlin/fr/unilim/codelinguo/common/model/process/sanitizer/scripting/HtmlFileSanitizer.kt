package fr.unilim.codelinguo.common.model.process.sanitizer.scripting

import fr.unilim.codelinguo.common.model.Word
import java.io.File
import java.util.regex.Pattern

class HtmlFileSanitizer : JavascriptFileSanitizer() {

    private val scriptTagPattern = Pattern.compile(
        "<script\\b[^>]*>([\\s\\S]*?)</script>",
        Pattern.CASE_INSENSITIVE
    )

    override fun processFile(path: String): List<Word> {
        val lines = File(path).useLines { it.toList() }
        val words = mutableListOf<Word>()
        val scriptContents = extractScriptContents(lines.joinToString("\n"))

        scriptContents.forEach { scriptContent ->
            val scriptLines = scriptContent.split("\n")
            scriptLines.forEach { line ->
                var processedLine = processLineForComments(line)
                if (processedLine.isNotBlank()) {
                    processedLine = removeStringLiterals(processedLine)
                    extractWords(processedLine, words)
                }
            }
        }

        return words
    }

    private fun extractScriptContents(html: String): List<String> {
        val matcher = scriptTagPattern.matcher(html)
        val scripts = mutableListOf<String>()

        while (matcher.find()) {
            scripts.add(matcher.group(1))
        }

        return scripts
    }
}
