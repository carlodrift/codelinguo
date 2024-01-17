package fr.unilim.saes5.view

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.persistence.glossary.JsonGlossaryDao
import fr.unilim.saes5.service.CompletionService
import javafx.collections.ObservableList

object DataLoader {
    fun loadSavedWords(words: ObservableList<Word>, contextCompletionService: CompletionService, lexicoCompletionService: CompletionService, tokenCompletionService: CompletionService, projectName: String) {
        val projectDao = JsonGlossaryDao()
        val projects = projectDao.allProjects

        contextCompletionService.clearCompletions()
        lexicoCompletionService.clearCompletions()
        tokenCompletionService.clearCompletions()

        val project = projects.find { it.name == projectName }

        if (project != null) {
            project.getWords()?.forEach { word ->
                word.context?.forEach { context ->
                    contextCompletionService.addCompletion(context.word.token ?: "")
                }
                word.synonyms?.forEach { synonym ->
                    lexicoCompletionService.addCompletion(synonym.token ?: "")
                    tokenCompletionService.addCompletion(synonym.token ?: "")
                }
                word.antonyms?.forEach { antonym ->
                    lexicoCompletionService.addCompletion(antonym.token ?: "")
                    tokenCompletionService.addCompletion(antonym.token ?: "")
                }
                lexicoCompletionService.addCompletion(word.token ?: "")
                if (!words.contains(word)) {
                    words.add(word)
                }
            }
        }
    }
}
