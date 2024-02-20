package fr.unilim.codelinguo.common.persistence.lang

fun interface LangDAO {
    fun getMessage(key: String): String
}
