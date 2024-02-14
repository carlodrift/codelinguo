package fr.unilim.codelinguo.common.model.process.sanitizer

class KotlinFileSanitizer : JavaFileSanitizer() {

    override val regexString = "\".*?\"|\"\"\".*?\"\"\"".toRegex()
    override val reservedKeywords = loadReservedKeywords("kotlin", "sanitizer")

}
