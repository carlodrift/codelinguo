package fr.unilim.saes5.view

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.context.PrimaryContext
import fr.unilim.saes5.model.context.SecondaryContext
import fr.unilim.saes5.model.reader.JavaFileReader
import fr.unilim.saes5.service.CompletionService
import fr.unilim.saes5.service.WordAnalyticsService
import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.geometry.Pos
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
    private var wordTableView: TableView<Word> by singleAssign()

    private val words = mutableListOf<Word>().asObservable()
    private val myBundle = ResourceBundle.getBundle("Messages", Locale.getDefault())

    private val tokenInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_token")
    }
    private val synonymInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_synonym")
    }
    private val definitionInput: TextArea = textarea {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_definition")
        prefHeight = 145.0
    }
    private val primaryContextInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_primary_context")
    }
    private val antonymInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_antonym")
    }
    private val secondaryContextInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_secondary_context")
    }

    init {
        DataLoader.loadSavedWords(words, completionService)
    }

    override val root = vbox(5.0) {
        primaryContextInput.textProperty().addListener { _, _, _ ->
            ViewUtilities.updateAutoCompletion(primaryContextInput, completionService, activeContextMenus)
        }
        secondaryContextInput.textProperty().addListener { _, _, _ ->
            ViewUtilities.updateAutoCompletion(secondaryContextInput, completionService, activeContextMenus)
        }
        wordTableView = tableview(words) {
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
            addClass(ViewStyles.customTableView)
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
            val removeColumn = TableColumn<Word, Word>("")
            removeColumn.cellValueFactory = Callback { cellData -> ReadOnlyObjectWrapper(cellData.value) }
            removeColumn.cellFactory = Callback {
                object : TableCell<Word, Word>() {
                    private val button = Button("X").apply {
                        addClass(ViewStyles.removeButton)
                        action {
                            val item = tableRow.item
                            item?.let {
                                words.remove(it)
                                ViewUtilities.updateJsonFile(words)
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
                            this += tokenInput
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
                            this += synonymInput
                        }
                    }
                    field {
                        vbox {
                            label(myBundle.getString("antonym_label"))
                            this += antonymInput
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

            button(myBundle.getString("button_quit")) {
                addClass(ViewStyles.helpButton)
                action {
                    Platform.exit()
                }
            }
            button(myBundle.getString("button_help")) {
                addClass(ViewStyles.helpButton)
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
                        dialogPane.prefWidth = primaryStage.width * 0.8
                        dialogPane.prefHeight = primaryStage.height * 0.5

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
                addClass(ViewStyles.downloadButton)
                action {
                    val fileChooser = FileChooser().apply {
                        title = "Choisir des fichiers"
                        extensionFilters.addAll(
                            FileChooser.ExtensionFilter("Fichiers Java", "*.java"),
                        )
                    }
                    val selectedFiles = fileChooser.showOpenMultipleDialog(currentWindow)
                    if (selectedFiles != null) {
                        val filePaths = selectedFiles.map { it.path }
                        val words = JavaFileReader().read(filePaths)
                        val analytics = WordAnalyticsService()
                        val wordRank = analytics.wordRank(words).mapKeys { it.key.token ?: "" }
                        ViewUtilities.openWordOccurrenceView(wordRank, myBundle)
                    }
                }
            }
            button(myBundle.getString("button_download_folder")) {
                addClass(ViewStyles.downloadButton)
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
                            ViewUtilities.openWordOccurrenceView(wordRank, myBundle)
                        }
                    }
                }
            }
            button(myBundle.getString("button_add")) {
                addClass(ViewStyles.addButton)
                action {
                    if (tokenInput.text.isBlank() || primaryContextInput.text.isBlank()) {
                        alert(
                            type = Alert.AlertType.WARNING,
                            header = myBundle.getString("missing_fields_header"),
                            content = myBundle.getString("missing_fields_content")
                        )
                    } else {
                        val newWord = Word(tokenInput.text).apply {
                            definition = definitionInput.text
                            context = listOf(
                                PrimaryContext(Word(primaryContextInput.text)),
                                SecondaryContext(Word(secondaryContextInput.text))
                            )
                            synonyms = setOf(Word(synonymInput.text))
                            antonyms = setOf(Word(antonymInput.text))
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
                            ViewUtilities.updateCompletionService(newWord, completionService)
                            ViewUtilities.clearInputFields()
                            ViewUtilities.updateJsonFile(words)
                            if (wordTableView != null) {
                                wordTableView.refresh()
                            }
                        }
                    }
                }
            }

        }
    }


}
