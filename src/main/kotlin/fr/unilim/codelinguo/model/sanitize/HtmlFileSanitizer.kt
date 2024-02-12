package fr.unilim.codelinguo.model.sanitize

import fr.unilim.codelinguo.model.Word
import java.util.regex.Pattern

class HtmlFileSanitizer : JavascriptFileSanitizer() {

    private val scriptTagPattern = Pattern.compile(
        "<script\\b[^>]*>([\\s\\S]*?)</script>",
        Pattern.CASE_INSENSITIVE
    )

    override fun sanitizeLines(lines: List<String>, path: String): List<Word> {
        val words = mutableListOf<Word>()
        val scriptContents = extractScriptContents(lines.joinToString("\n"))

        scriptContents.forEach { scriptContent ->
            super.sanitizeLines(scriptContent.split("\n"), path).forEach { word ->
                words.add(word)
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
