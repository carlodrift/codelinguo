package fr.unilim.saes5.view

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.persistence.lang.JsonLangDao
import fr.unilim.saes5.persistence.lang.LangDAO
import fr.unilim.saes5.service.CompletionService
import fr.unilim.saes5.view.utilities.DataLoader
import fr.unilim.saes5.view.utilities.ViewUtilities
import javafx.scene.control.TableView
import tornadofx.*


class MainView : View() {
    val projectName: String by param()

    companion object {
        val contextCompletionService = CompletionService()
        val lexicoCompletionService = CompletionService()
        val tokenCompletionService = CompletionService()
        val words = mutableListOf<Word>().asObservable()
    }

    private val lang: LangDAO = JsonLangDao()

    private var wordTableView: TableView<Word> by singleAssign()

    init {
        DataLoader.loadSavedWords(
            words,
            contextCompletionService,
            lexicoCompletionService,
            tokenCompletionService,
            projectName
        )
        ViewUtilities.updateJsonFile(words, projectName)
    }

    override val root = vbox(5.0) {

        val wordTableViewComponent = WordTableView(words, lang, projectName)
        wordTableView = wordTableViewComponent.wordTableView
        this += wordTableViewComponent

        val inputFormView =
            InputFormView(contextCompletionService, lexicoCompletionService, tokenCompletionService, lang)
        this += inputFormView

        val buttonBarView = ButtonBarView(
            lang, words, inputFormView.tokenInput, inputFormView.primaryContextInput,
            inputFormView.secondaryContextInput, inputFormView.synonymInput, inputFormView.antonymInput,
            inputFormView.definitionInput, wordTableView, projectName
        )
        this += buttonBarView
    }
}


