package fr.unilim.saes5.persistence.lang

fun interface LangDAO {
    fun getMessage(key: String): String
}
