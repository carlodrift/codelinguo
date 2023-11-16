package fr.unilim.saes5.model.context

import fr.unilim.saes5.model.Word

abstract class Context protected constructor(val word: Word, val priority: Float)
