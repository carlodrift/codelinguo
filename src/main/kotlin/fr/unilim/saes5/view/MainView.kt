package fr.unilim.saes5.view

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.service.CompletionService
import javafx.event.EventHandler
import javafx.scene.control.TableView
import tornadofx.*
import java.util.*


class MainView() : View() {
    val projectName: String by param()

    companion object {
        val contextCompletionService = CompletionService()
        val lexicoCompletionService = CompletionService()
        val tokenCompletionService = CompletionService()
        val words = mutableListOf<Word>().asObservable()
    }
    private val myBundle = ResourceBundle.getBundle("Messages", Locale.getDefault())

    private var wordTableView: TableView<Word> by singleAssign()

    init {
        DataLoader.loadSavedWords(words, contextCompletionService, lexicoCompletionService, tokenCompletionService, projectName)
    }

    override val root = vbox(5.0) {

        val wordTableViewComponent = WordTableView(words, myBundle, projectName)
        wordTableView = wordTableViewComponent.wordTableView
        this += wordTableViewComponent

        val inputFormView = InputFormView(contextCompletionService, lexicoCompletionService, tokenCompletionService, myBundle)
        this += inputFormView

        val buttonBarView = ButtonBarView(
            myBundle, words, contextCompletionService, inputFormView.tokenInput,
            inputFormView.primaryContextInput, inputFormView.secondaryContextInput, inputFormView.synonymInput,
            inputFormView.antonymInput, inputFormView.definitionInput, wordTableView, projectName
        )
        this += buttonBarView
    }
}


