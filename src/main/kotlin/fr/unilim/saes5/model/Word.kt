package fr.unilim.saes5.model

import fr.unilim.saes5.model.context.Context

class Word(var token: String?) {
    var synonyms: Set<Word>? = HashSet()
    var related: Set<Word>? = HashSet()
    var antonyms: Set<Word>? = HashSet()
    var context: Context? = null

    constructor() : this(null)
}
