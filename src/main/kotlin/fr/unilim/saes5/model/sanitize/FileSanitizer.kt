package fr.unilim.saes5.model

abstract class FileSanitizer {
    abstract fun sanitizeLines(lines: List<String>): List<Word>
}