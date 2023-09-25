package fr.unilim.saes5.model

import fr.unilim.saes5.model.context.Context

class Word(var token: String?) {
    var synonyms: Set<Word>? = HashSet()
    var related: Set<Word>? = HashSet()
    var antonyms: Set<Word>? = HashSet()
    var context: Context? = null

    constructor() : this(null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Word

        return token == other.token
    }

    override fun hashCode(): Int {
        return token?.hashCode() ?: 0
    }
}
