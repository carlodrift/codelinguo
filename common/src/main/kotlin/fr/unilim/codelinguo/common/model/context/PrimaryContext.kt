package fr.unilim.codelinguo.common.model.context

import fr.unilim.codelinguo.common.model.Word
import fr.unilim.codelinguo.common.model.context.Context

class PrimaryContext(word: Word?) : Context(word!!, PRIMARY_CONTEXT_PRIORITY) {
    companion object {
        const val PRIMARY_CONTEXT_PRIORITY = 2.0f
    }
}
