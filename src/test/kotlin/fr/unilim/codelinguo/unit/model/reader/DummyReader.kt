package fr.unilim.codelinguo.unit.model.reader

import fr.unilim.codelinguo.model.Word
import fr.unilim.codelinguo.model.reader.IRead

class DummyReader(private var words: List<Word>) : IRead {
    override fun read(path: String): List<Word> {
        return words
    }

    override fun read(paths: List<String>): List<Word?> {
        return words
    }

    override fun readOne(path: String): List<Word> {
        return words
    }
}
