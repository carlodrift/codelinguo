package fr.unilim.saes5.view

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.context.PrimaryContext
import fr.unilim.saes5.model.context.SecondaryContext
import fr.unilim.saes5.model.reader.JavaFileReader
import fr.unilim.saes5.service.CompletionService
import fr.unilim.saes5.service.WordAnalyticsService
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.util.*

class ButtonBarView(
    private val myBundle: ResourceBundle,
    private val words: ObservableList<Word>,
    private val completionService: CompletionService,
    private val tokenInput: TextField,
    private val primaryContextInput: TextField,
    private val secondaryContextInput: TextField,
    private val synonymInput: TextField,
    private val antonymInput: TextField,
    private val definitionInput: TextArea,
    private val wordTableView: TableView<Word>? = null
) : View() {

    override val root = hbox(20.0) {
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
                    initOwner(this@ButtonBarView.currentWindow)
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
                val owner = this@ButtonBarView.currentWindow
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
            addClass(ViewStyles.downloadButtonHover)
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
            addClass(ViewStyles.downloadButtonHover)
            action {
                val directoryChooser = DirectoryChooser().apply {
                    title = "Choisir un dossier"
                }
                directoryChooser.showDialog(currentWindow)?.let { file ->
                    val words = JavaFileReader().read(file.toString())
                    val analytics = WordAnalyticsService()
                    val wordRank = analytics.wordRank(words).mapKeys { it.key.token ?: "" }
                    ViewUtilities.openWordOccurrenceView(wordRank, myBundle)
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
                        ViewUtilities.clearInputFields(
                            tokenInput,
                            primaryContextInput,
                            secondaryContextInput,
                            synonymInput,
                            antonymInput,
                            definitionInput
                        )
                        ViewUtilities.updateJsonFile(words)
                        wordTableView?.refresh()
                    }
                }
            }
        }
    }
}
