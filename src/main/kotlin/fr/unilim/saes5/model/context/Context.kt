package fr.unilim.saes5.model.context

import fr.unilim.saes5.model.Word

abstract class Context protected constructor(private val word: Word, private val priority: Float)
