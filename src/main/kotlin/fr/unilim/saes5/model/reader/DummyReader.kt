package fr.unilim.saes5.model.reader

import fr.unilim.saes5.model.interfaces.IAnalyze
import fr.unilim.saes5.model.Word

class DummyReader(var words: List<Word>) : IAnalyze {
    override fun read(): List<Word> {
        return words
    }

    override fun readOne(path: String): List<Word?>? {
        TODO("Not yet implemented")
    }
}
