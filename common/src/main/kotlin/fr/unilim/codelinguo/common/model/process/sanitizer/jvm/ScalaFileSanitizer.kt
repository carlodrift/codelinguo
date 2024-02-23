package fr.unilim.codelinguo.common.model.process.sanitizer.jvm

class ScalaFileSanitizer : JVMFileSanitizer() {

    override val regexString = "\".*?\"|'''(?:.|\\n|\\r)*?'''".toRegex()
    override val reservedKeywords = loadReservedKeywords("scala", "sanitizer")

}

