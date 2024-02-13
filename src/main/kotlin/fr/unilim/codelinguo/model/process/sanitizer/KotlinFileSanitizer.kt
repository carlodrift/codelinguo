package fr.unilim.codelinguo.model.process.sanitizer

class KotlinFileSanitizer : JavaFileSanitizer() {

    override val regexString = "\".*?\"|\"\"\".*?\"\"\"".toRegex()
    override val reservedKeywords = loadReservedKeywords("kotlin")

}
