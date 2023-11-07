package fr.unilim.saes5.model.reader

import fr.unilim.saes5.interfaces.IAnalyze
import fr.unilim.saes5.model.Word

class TestReader(var words: List<Word>) : IAnalyze {
    override fun read(): List<Word> {
        return words
    }
}
