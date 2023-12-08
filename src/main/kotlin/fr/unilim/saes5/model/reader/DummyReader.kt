package fr.unilim.saes5.model.reader

import fr.unilim.saes5.model.Word

class DummyReader(private var words: List<Word>) : IRead {
    override fun read(path: String): List<Word> {
        return words
    }

    override fun read(paths: List<String>): List<Word?>? {
        TODO("Not yet implemented")
    }

    override fun readOne(path: String): List<Word> {
        TODO("Not yet implemented")
    }
}
