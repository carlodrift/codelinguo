package fr.unilim.saes5.view

import fr.unilim.saes5.model.Glossary
import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.context.PrimaryContext
import fr.unilim.saes5.model.context.SecondaryContext
import fr.unilim.saes5.model.reader.JavaFileReader
import fr.unilim.saes5.persistence.JsonGlossaryDao
import fr.unilim.saes5.service.CompletionService
import fr.unilim.saes5.service.WordAnalyticsService
import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.util.Callback
import tornadofx.*
import java.util.*


class MainView : View() {
    private val completionService = CompletionService()
    private val activeContextMenus = mutableMapOf<TextField, ContextMenu>()

    private val words = mutableListOf<Word>().asObservable()
    private val myBundle = ResourceBundle.getBundle("Messages", Locale.getDefault())

    private val motInput: TextField = textfield {
        addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_joyeux")
    }
    private val synonymeInput: TextField = textfield {
        addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_heureux")
    }
    private val definitionInput: TextArea = textarea {
        addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_definition")
        prefHeight = 145.0
    }
    private val primaryContextInput: TextField = textfield {
        addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_joueur")
    }
    private val antonymeInput: TextField = textfield {
        addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_aigri")
    }
    private val secondaryContextInput: TextField = textfield {
        addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_psychologie")
    }

    init {
        loadSavedWords()
    }

    private fun loadSavedWords() {
        val projectDao = JsonGlossaryDao("glossary.json")
        val projects = projectDao.allProjects

        projects.forEach { project ->
            project.words?.forEach { word ->
                word.context?.forEach { context ->
                    completionService.addCompletion(context.word.token ?: "")
                }
                if (!words.contains(word)) {
                    words.add(word)
                }
            }
        }
    }

    override val root = vbox(5.0) {
        primaryContextInput.textProperty().addListener { _, _, _ ->
            updateAutoCompletion(primaryContextInput)
        }
        secondaryContextInput.textProperty().addListener { _, _, _ ->
            updateAutoCompletion(secondaryContextInput)
        }
        tableview(words) {
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
            addClass(Styles.customTableView)
            readonlyColumn(myBundle.getString("token_label"), Word::token)
            readonlyColumn(myBundle.getString("definition_label"), Word::definition)

            column(myBundle.getString("primary_context_label"), Word::context).cellFormat { cell ->
                text = cell?.filterIsInstance<PrimaryContext>()?.joinToString { it.word.token ?: "" } ?: ""
            }
            column(myBundle.getString("secondary_context_label"), Word::context).cellFormat { cell ->
                text = cell?.filterIsInstance<SecondaryContext>()?.joinToString { it.word.token ?: "" } ?: ""
            }
            column(myBundle.getString("synonym_label"), Word::synonyms).cellFormat { cell ->
                text = cell?.joinToString { it.token ?: "" } ?: ""
            }
            column(myBundle.getString("antonym_label"), Word::antonyms).cellFormat { cell ->
                text = cell?.joinToString { it.token ?: "" } ?: ""
            }
            val removeColumn = TableColumn<Word, Word>(myBundle.getString("actions_label"))
            removeColumn.cellValueFactory = Callback { cellData -> ReadOnlyObjectWrapper(cellData.value) }
            removeColumn.cellFactory = Callback {
                object : TableCell<Word, Word>() {
                    private val button = Button("X").apply {
                        action {
                            val item = tableRow.item
                            item?.let {
                                words.remove(it)
                                updateJsonFile()
                            }
                        }
                    }

                    override fun updateItem(item: Word?, empty: Boolean) {
                        super.updateItem(item, empty)
                        if (empty) {
                            graphic = null
                        } else {
                            graphic = button
                        }
                    }
                }
            }
            columns.add(removeColumn)
            prefHeight = 350.0
        }

        hbox(10.0) {
            form {
                fieldset {
                    field {
                        vbox {
                            label(myBundle.getString("token_label") + myBundle.getString("required_field"))
                            this += motInput
                        }
                    }
                    field {
                        vbox {
                            label(myBundle.getString("primary_context_label") + myBundle.getString("required_field"))
                            this += primaryContextInput
                        }
                    }
                    field {
                        vbox {
                            label(myBundle.getString("secondary_context_label"))
                            this += secondaryContextInput
                        }
                    }
                }
            }
            form {
                fieldset {
                    field {
                        vbox {
                            label(myBundle.getString("synonym_label"))
                            this += synonymeInput
                        }
                    }
                    field {
                        vbox {
                            label(myBundle.getString("antonym_label"))
                            this += antonymeInput
                        }
                    }
                }
            }
            form {
                fieldset {
                    field {
                        vbox {
                            label(myBundle.getString("definition_label"))
                            this += definitionInput
                        }
                    }
                }
            }
        }

        hbox(20.0) {
            paddingBottom = 20.0
            paddingHorizontal = 20.0
            alignment = Pos.BASELINE_RIGHT

            button(myBundle.getString("button_help")) {
                addClass(Styles.helpButton)
                action {
                    val dialog = Dialog<ButtonType>().apply {
                        initOwner(this@MainView.currentWindow)
                        title = myBundle.getString("help_title")
                        dialogPane.buttonTypes.add(ButtonType.CLOSE)

                        val textFlow = TextFlow(
                            Text(myBundle.getString("token_label") + "\n").apply { style = "-fx-font-weight: bold" },
                            Text(myBundle.getString("statute_obligatory") + "\n" + myBundle.getString("description_token") + "\n\n"),
                            Text(myBundle.getString("definition_label") + "\n").apply {
                                style = "-fx-font-weight: bold"
                            },
                            Text(myBundle.getString("statute_facultative") + "\n " + myBundle.getString("description_definition") + "\n\n"),
                            Text(myBundle.getString("primary_context_label") + "\n").apply {
                                style = "-fx-font-weight: bold"
                            },
                            Text(myBundle.getString("statute_obligatory") + "\n" + myBundle.getString("description_primary_context") + "\n\n"),
                            Text(myBundle.getString("secondary_context_label") + "\n").apply {
                                style = "-fx-font-weight: bold"
                            },
                            Text(myBundle.getString("statute_facultative") + "\n" + myBundle.getString("description_secondary_content") + "\n\n"),
                            Text(myBundle.getString("synonym_label") + "\n").apply { style = "-fx-font-weight: bold" },
                            Text(myBundle.getString("statute_facultative") + "\n" + myBundle.getString("description_synonym") + "\n\n"),
                            Text(myBundle.getString("antonym_label") + "\n").apply { style = "-fx-font-weight: bold" },
                            Text(myBundle.getString("statute_facultative") + "\n" + myBundle.getString("description_antonym") + "\n\n")
                        )

                        dialogPane.content = textFlow
                        dialogPane.prefWidth = primaryStage.width * 0.8 // 80% de la largeur de la fenêtre principale
                        dialogPane.prefHeight = primaryStage.height * 0.5 // 50% de la hauteur de la fenêtre principale

                    }

                    val owner = this@MainView.currentWindow

                    Platform.runLater {
                        if (owner != null) {
                            val scene = owner.scene
                            val x = owner.x + scene.x + (scene.width - dialog.dialogPane.width) / 2
                            val y = owner.y + scene.y + (scene.height - dialog.dialogPane.height) / 2
                            dialog.x = x
                            dialog.y = y
                        }
                    }


                    dialog.showAndWait()
                }
            }
            button(myBundle.getString("button_download_file")) {
                addClass(Styles.downloadButton)
                action {
                    val fileChooser = FileChooser().apply {
                        title = "Choisir des fichiers"
                        extensionFilters.addAll(
                            FileChooser.ExtensionFilter("Fichiers Java", "*.java"),
                        )
                    }
                    val selectedFiles = fileChooser.showOpenMultipleDialog(currentWindow)
                    if (selectedFiles != null) {
                        selectedFiles.forEach { file ->
                            val words = JavaFileReader().readOne(file.toString())
                            val analytics = WordAnalyticsService()
                            val wordRank = analytics.wordRank(words).mapKeys { it.key.token ?: "" }
                            openWordOccurrenceView(wordRank)
                        }
                    }
                }
            }
            button(myBundle.getString("button_download_folder")) {
                addClass(Styles.downloadButton)
                action {
                    val directoryChooser = DirectoryChooser().apply {
                        title = "Choisir un dossier"
                    }
                    val selectedDirectory = directoryChooser.showDialog(currentWindow)
                    if (selectedDirectory != null) {
                        selectedDirectory.let {
                            val words = JavaFileReader().read(it.toString())
                            val analytics = WordAnalyticsService()
                            val wordRank = analytics.wordRank(words).mapKeys { it.key.token ?: "" }
                            openWordOccurrenceView(wordRank)
                        }
                    }
                }
            }
            button(myBundle.getString("button_add")) {
                addClass(Styles.addButton)
                action {
                    if (motInput.text.isBlank() || primaryContextInput.text.isBlank()) {
                        alert(
                            type = Alert.AlertType.WARNING,
                            header = myBundle.getString("missing_fields_header"),
                            content = myBundle.getString("missing_fields_content")
                        )
                    } else {
                        val newWord = Word(motInput.text).apply {
                            definition = definitionInput.text
                            context = listOf(
                                PrimaryContext(Word(primaryContextInput.text)),
                                SecondaryContext(Word(secondaryContextInput.text))
                            )
                            synonyms = setOf(Word(synonymeInput.text))
                            antonyms = setOf(Word(antonymeInput.text))
                        }

                        val duplicate = words.any { it == newWord }
                        if (duplicate) {
                            alert(
                                type = Alert.AlertType.WARNING,
                                header = myBundle.getString("duplicate_header"),
                                content = myBundle.getString("duplicate_content")
                            )
                        } else {
                            words.add(newWord)
                            updateCompletionService(newWord)
                            clearInputFields()
                            updateJsonFile()
                        }
                    }
                }
            }

        }
    }

    private fun updateJsonFile() {
        val projectDao = JsonGlossaryDao("glossary.json")
        val glossary = Glossary(words.toList())
        projectDao.saveProject(glossary)
    }


    private fun openWordOccurrenceView(wordRank: Map<String, Int>) {
        val view = WordOccurrenceView(wordRank)
        view.openWindow(owner = null, escapeClosesWindow = true)
    }

    private fun updateAutoCompletion(textField: TextField) {
        val suggestions = completionService.suggestCompletions(textField.text)
        if (textField.text.isBlank()) {
            activeContextMenus[textField]?.hide()
            return
        }
        showSuggestions(textField, suggestions)
    }


    private fun showSuggestions(textField: TextField, suggestions: Set<String>) {
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

    private fun updateCompletionService(word: Word) {
        word.context?.forEach { context ->
            completionService.addCompletion(context.word.token ?: "")
        }
    }

    private fun clearInputFields() {
        motInput.clear()
        synonymeInput.clear()
        definitionInput.clear()
        primaryContextInput.clear()
        antonymeInput.clear()
        secondaryContextInput.clear()
    }
}