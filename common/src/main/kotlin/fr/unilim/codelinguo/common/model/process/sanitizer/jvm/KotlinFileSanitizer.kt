package fr.unilim.codelinguo.common.model.process.sanitizer.jvm

class KotlinFileSanitizer : JVMFileSanitizer() {

    override val regexString = "\".*?\"|\"\"\".*?\"\"\"".toRegex()
    override val reservedKeywords = loadReservedKeywords("kotlin", "sanitizer")

}
