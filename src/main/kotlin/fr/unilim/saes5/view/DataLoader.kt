package fr.unilim.saes5.view

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.persistence.glossary.JsonGlossaryDao
import fr.unilim.saes5.service.CompletionService
import javafx.collections.ObservableList

object DataLoader {
    fun loadSavedWords(words: ObservableList<Word>, contextCompletionService: CompletionService, synonymCompletionService: CompletionService, antonymCompletionService: CompletionService) {
        val projectDao = JsonGlossaryDao("glossary.json")
        val projects = projectDao.allProjects

        projects.forEach { project ->
            project.words?.forEach { word ->
                word.context?.forEach { context ->
                    contextCompletionService.addCompletion(context.word.token ?: "")
                }
                word.synonyms?.forEach { synonym ->
                    synonymCompletionService.addCompletion(synonym.token ?: "")
                }
                word.antonyms?.forEach { antonym ->
                    antonymCompletionService.addCompletion(antonym.token ?: "")
                }
                if (!words.contains(word)) {
                    words.add(word)
                }
            }
        }
    }
}
