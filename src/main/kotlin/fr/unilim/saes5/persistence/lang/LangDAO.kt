package fr.unilim.saes5.persistence.lang

interface LangDAO {
    fun getMessage(key: String): String
}
