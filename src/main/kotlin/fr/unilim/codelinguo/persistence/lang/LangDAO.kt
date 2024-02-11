package fr.unilim.codelinguo.persistence.lang

fun interface LangDAO {
    fun getMessage(key: String): String
}
