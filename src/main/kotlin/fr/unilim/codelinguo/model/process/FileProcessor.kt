package fr.unilim.codelinguo.model.process

import fr.unilim.codelinguo.model.Word
import fr.unilim.codelinguo.persistence.keyword.KeywordDao
import fr.unilim.codelinguo.persistence.keyword.TxtKeywordDao

abstract class FileProcessor {

    protected abstract val reservedKeywords: Set<String>

    abstract fun processFile(path: String): List<Word>

    fun loadReservedKeywords(language: String): Set<String> {
        val loader: KeywordDao = TxtKeywordDao()
        return loader.retrieve(language)
    }
}
