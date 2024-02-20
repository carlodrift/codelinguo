package fr.unilim.codelinguo.common.model.process

import fr.unilim.codelinguo.common.model.Word
import fr.unilim.codelinguo.common.persistence.keyword.KeywordDao
import fr.unilim.codelinguo.common.persistence.keyword.TxtKeywordDao
import java.io.File

abstract class FileProcessor {

    protected abstract val reservedKeywords: Set<String>

    abstract fun processFile(path: String): List<Word>

    fun loadReservedKeywords(language: String, type: String): Set<String> {
        val loader: KeywordDao = TxtKeywordDao()
        return loader.retrieve(type + File.separator + language)
    }
}
