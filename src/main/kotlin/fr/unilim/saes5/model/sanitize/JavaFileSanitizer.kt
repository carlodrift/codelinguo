package fr.unilim.saes5.model.sanitize

import fr.unilim.saes5.model.Word

class JavaFileSanitizer : FileSanitizer() {

    private val JAVA_RESERVED_KEYWORDS = listOf(
    "string", "int", "abstract", "true", "false", "equals", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
    "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
    "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
    "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
    "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "println"
    )

    private val REGEX_WORD_SEPARATION = "[a-zA-Z]+"
    private val REGEX_JAVA_STRING =  "\".*\""
    override fun sanitizeLines(lines: List<String>): List<Word> {
        val words = mutableSetOf<Word>()
        var inBlockComment = false

        lines.forEach { line ->
            var processedLine = line

            if (inBlockComment) {
                if (line.contains("*/")) {
                    inBlockComment = false
                    processedLine = line.substringAfter("*/")
                } else {
                    return@forEach
                }
            }

            val stringRegex = REGEX_JAVA_STRING.toRegex()
            if (stringRegex.containsMatchIn(processedLine)) {
                processedLine = processedLine.replace(stringRegex, "")

            }


            if (processedLine.contains("/*")) {
                inBlockComment = true
                processedLine = processedLine.substringBefore("/*")
            }

            if (processedLine.contains("//")) {
                processedLine = processedLine.substringBefore("//")
            }



            REGEX_WORD_SEPARATION.toRegex().findAll(processedLine).forEach { match ->
                val word = match.value

                if (word !in JAVA_RESERVED_KEYWORDS
                    && !REGEX_JAVA_STRING.toRegex().containsMatchIn(word)) {
                    splitCamelCase(word).forEach { splitWord ->
                        words.add(Word(splitWord))
                    }
                }
            }
        }

        return words.toList()
    }

    private fun splitCamelCase(word: String): List<String> {
        return word.split("(?<!^)(?=[A-Z])".toRegex()).map { it.toLowerCase() }
    }
}