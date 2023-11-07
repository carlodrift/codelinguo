package fr.unilim.saes5.model.reader

import fr.unilim.saes5.interfaces.IAnalyze
import fr.unilim.saes5.model.Word

abstract class SingleFileReader : IAnalyze {
    override fun readOne(): List<Word> {
        return null
    }
}
