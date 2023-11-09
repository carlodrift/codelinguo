package fr.unilim.saes5.model.sanitize

import fr.unilim.saes5.model.Word

class JavaFileSanitizer : FileSanitizer() {

    val JAVA_RESERVED_KEYWORDS = listOf(
    "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
    "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
    "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
    "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
    "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
    )
    override fun sanitizeLines(lines: List<String>): List<Word> {
        val words = mutableListOf<Word>()
        val regex = Regex("[a-zA-Z]+")

        for (line in lines) {
            val matches = regex.findAll(line)
            for (match in matches) {
                val word = match.value
                if (!JAVA_RESERVED_KEYWORDS.contains(word)) {
                    words.add(Word(word))
                }
            }
        }
        return words
    }
}