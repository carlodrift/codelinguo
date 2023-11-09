package fr.unilim.saes5.model.sanitize

import fr.unilim.saes5.model.Word

abstract class FileSanitizer {
    abstract fun sanitizeLines(lines: List<String>): List<Word>
}