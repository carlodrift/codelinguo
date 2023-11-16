package fr.unilim.saes5.model.interfaces

import fr.unilim.saes5.model.Word

interface IRead {
    fun read(): List<Word>?
    fun readOne(path:String): List<Word?>?


}
