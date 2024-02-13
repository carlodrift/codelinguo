package fr.unilim.codelinguo.common.model.reader

import fr.unilim.codelinguo.common.model.Word

interface IRead {
    fun read(path: String): List<Word>?
    fun readOne(path: String): List<Word?>?

    fun read(paths: List<String>): List<Word?>?


}
