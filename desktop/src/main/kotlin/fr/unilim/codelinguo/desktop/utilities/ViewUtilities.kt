package fr.unilim.codelinguo.desktop.utilities

import fr.unilim.codelinguo.common.model.Glossary
import fr.unilim.codelinguo.common.model.Word
import fr.unilim.codelinguo.common.persistence.lang.LangDAO
import fr.unilim.codelinguo.common.persistence.project.JsonProjectDao
import fr.unilim.codelinguo.common.persistence.project.ProjectDao
import fr.unilim.codelinguo.common.service.CompletionService
import fr.unilim.codelinguo.desktop.MainView
import fr.unilim.codelinguo.desktop.WordOccurrenceView
import javafx.collections.ObservableList
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl

object ViewUtilities {
    fun updateJsonFile(words: ObservableList<Word>, name: String) {
        val projectDao: ProjectDao = JsonProjectDao()
        val glossary = Glossary(words.toList())
        projectDao.save(glossary, name)
    }

    fun openWordOccurrenceView(
        wordRank: Map<Word, Int>,
        wordsInListNotInGlossary: List<Word>,
        glossaryRatio: Float,
        lang: LangDAO,
        projectName: String,
        fileName: String,
        rawWordRank: Map<Word, Int>
    ) {
        val view = WordOccurrenceView(wordRank, wordsInListNotInGlossary, glossaryRatio, lang, projectName, fileName, rawWordRank)
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

    fun updateCompletionService(projectName: String) {
        DataLoader.loadSavedWords(
            MainView.words,
            MainView.contextCompletionService,
            MainView.lexicoCompletionService,
            MainView.tokenCompletionService,
            projectName
        )
    }

    fun clearInputFields(vararg inputs: TextInputControl) {
        inputs.forEach { it.clear() }
    }
}
