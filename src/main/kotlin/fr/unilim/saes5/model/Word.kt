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
        if (javaClass != other?.javaClass) return false

        other as Word

        if (token != other.token) return false
        if (synonyms != other.synonyms) return false
        if (related != other.related) return false
        if (antonyms != other.antonyms) return false
        if (context != other.context) return false

        return true
    }


    override fun toString(): String {
        return "$token"
    }

    override fun hashCode(): Int {
        var result = token?.hashCode() ?: 0
        result = 31 * result + (synonyms?.hashCode() ?: 0)
        result = 31 * result + (related?.hashCode() ?: 0)
        result = 31 * result + (antonyms?.hashCode() ?: 0)
        result = 31 * result + (context?.hashCode() ?: 0)
        return result
    }

}
