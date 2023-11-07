package fr.unilim.saes5.interfaces

import fr.unilim.saes5.model.Word

interface IAnalyze {
    fun read(): List<Word>?
    fun readOne(path:String): List<Word?>?


}
