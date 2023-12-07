package fr.unilim.saes5.model.reader

import fr.unilim.saes5.model.Word

interface IRead {
    fun read(path: String): List<Word>?
    fun readOne(path: String): List<Word?>?


}
