package fr.unilim.saes5.view

import fr.unilim.saes5.model.Glossary
import fr.unilim.saes5.model.Word
import fr.unilim.saes5.persistence.glossary.JsonGlossaryDao
import fr.unilim.saes5.service.CompletionService
import javafx.collections.ObservableList
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import java.util.*

object ViewUtilities {
    fun updateJsonFile(words: ObservableList<Word>) {
        val projectDao = JsonGlossaryDao("glossary.json")
        val glossary = Glossary(words.toList())
        projectDao.saveProject(glossary)
    }

    fun openWordOccurrenceView(wordRank: Map<Word, Int>, wordsInListNotInGlossary: List<Word>, glossaryRatio: Float, myBundle: ResourceBundle) {
        val view = WordOccurrenceView(wordRank, wordsInListNotInGlossary, glossaryRatio, myBundle)
        view.openWindow(owner = null, escapeClosesWindow = true)
    }

    fun updateAutoCompletion(
        textField: TextField,
        completionService: CompletionService,
        activeContextMenus: MutableMap<TextField, ContextMenu>
    ) {
        val suggestions = completionService.suggestCompletions(textField.text)
        if (textField.text.isBlank()) {
            activeContextMenus[textField]?.hide()
            return
        }
        showSuggestions(textField, suggestions, activeContextMenus)
    }

    private fun showSuggestions(
        textField: TextField,
        suggestions: Set<String>,
        activeContextMenus: MutableMap<TextField, ContextMenu>
    ) {
        var contextMenu = activeContextMenus[textField]

        if (contextMenu == null) {
            contextMenu = ContextMenu()
            activeContextMenus[textField] = contextMenu
        } else {
            contextMenu.items.clear()
        }

        suggestions.forEach { suggestion ->
            val menuItem = MenuItem(suggestion)
            menuItem.setOnAction {
                textField.text = suggestion
                contextMenu.hide()
            }
            contextMenu.items.add(menuItem)
        }

        if (contextMenu.items.isNotEmpty()) {
            if (!contextMenu.isShowing) {
                contextMenu.show(textField, Side.BOTTOM, 0.0, 0.0)
            }
        } else {
            contextMenu.hide()
        }
    }

    fun updateCompletionService() {
        DataLoader.loadSavedWords(MainView.words, MainView.contextCompletionService, MainView.lexicoCompletionService, MainView.tokenCompletionService)
    }

    fun clearInputFields(vararg inputs: TextInputControl) {
        inputs.forEach { it.clear() }
    }
}
