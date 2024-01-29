package fr.unilim.saes5.model.sanitize

import fr.unilim.saes5.model.Word
import java.util.regex.Pattern

class HtmlFileSanitizer : JavascriptFileSanitizer() {

    private val scriptTagPattern = Pattern.compile(
        "<script\\b[^>]*>([\\s\\S]*?)</script>",
        Pattern.CASE_INSENSITIVE
    )

    override fun sanitizeLines(lines: List<String>): List<Word> {
        val words = mutableListOf<Word>()
        val scriptContents = extractScriptContents(lines.joinToString("\n"))

        scriptContents.forEach { scriptContent ->
            super.sanitizeLines(scriptContent.split("\n")).forEach { word ->
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
