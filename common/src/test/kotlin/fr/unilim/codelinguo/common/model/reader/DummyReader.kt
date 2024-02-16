package fr.unilim.codelinguo.common.model.reader

import fr.unilim.codelinguo.common.model.Word

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
