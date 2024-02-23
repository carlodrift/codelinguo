package fr.unilim.codelinguo.common.model.process.sanitizer.jvm

class JavaFileSanitizer : JVMFileSanitizer() {

    override val regexString = "\".*\"".toRegex()
    override val reservedKeywords = loadReservedKeywords("java", "sanitizer")

}
