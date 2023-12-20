package fr.unilim.saes5.model

import fr.unilim.saes5.model.context.Context

class Word(var token: String?) {
    var fileName: String? = null
    var synonyms: Set<Word>? = HashSet()
    var related: Set<Word>? = HashSet()
    var antonyms: Set<Word>? = HashSet()
    var context: List<Context>? = null
    var definition: String? = null


    constructor() : this(null)



    override fun toString(): String {
        return "$token"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Word

        if (token != other.token) return false
        if (fileName != other.fileName) return false
        if (synonyms != other.synonyms) return false
        if (related != other.related) return false
        if (antonyms != other.antonyms) return false
        if (context != other.context) return false
        if (definition != other.definition) return false

        return true
    }

    override fun hashCode(): Int {
        var result = token?.hashCode() ?: 0
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (synonyms?.hashCode() ?: 0)
        result = 31 * result + (related?.hashCode() ?: 0)
        result = 31 * result + (antonyms?.hashCode() ?: 0)
        result = 31 * result + (context?.hashCode() ?: 0)
        result = 31 * result + (definition?.hashCode() ?: 0)
        return result
    }


}
