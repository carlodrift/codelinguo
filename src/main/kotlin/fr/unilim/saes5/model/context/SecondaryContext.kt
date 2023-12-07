package fr.unilim.saes5.model.context

import fr.unilim.saes5.model.Word

class SecondaryContext(word: Word?) : Context(word!!, SECONDARY_CONTEXT_PRIORITY) {
    companion object {
        const val SECONDARY_CONTEXT_PRIORITY = 1.0f
    }
}
