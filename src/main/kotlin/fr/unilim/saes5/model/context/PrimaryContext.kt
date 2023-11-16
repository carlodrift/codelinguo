package fr.unilim.saes5.model.context

import fr.unilim.saes5.model.Word

class PrimaryContext(word: Word?) : Context(word!!, PRIMARY_CONTEXT_PRIORITY) {
    companion object {
        private const val PRIMARY_CONTEXT_PRIORITY = 2.0f
    }
}
