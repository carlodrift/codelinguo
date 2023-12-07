package fr.unilim.saes5.model.context

import fr.unilim.saes5.model.Word

open class Context protected constructor(val word: Word, val priority: Float) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Context

        if (word != other.word) return false
        if (priority != other.priority) return false

        return true
    }

    override fun hashCode(): Int {
        var result = word.hashCode()
        result = 31 * result + priority.hashCode()
        return result
    }
}
