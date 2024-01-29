package fr.unilim.saes5.model.sanitize

class KotlinFileSanitizer : JavaFileSanitizer() {

    override val regexString = "\".*?\"|\"\"\".*?\"\"\"".toRegex()
    override val reservedKeywords = loadReservedKeywords("kotlin")

}
