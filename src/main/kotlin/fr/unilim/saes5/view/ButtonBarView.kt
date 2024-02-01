package fr.unilim.saes5.view

import fr.unilim.saes5.model.Glossary
import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.context.PrimaryContext
import fr.unilim.saes5.model.context.SecondaryContext
import fr.unilim.saes5.model.reader.FileReader
import fr.unilim.saes5.model.reader.GitProjectReader
import fr.unilim.saes5.persistence.directory.DirectoryDao
import fr.unilim.saes5.persistence.directory.JsonDirectoryDao
import fr.unilim.saes5.persistence.lang.LangDAO
import fr.unilim.saes5.service.WordAnalyticsService
import fr.unilim.saes5.view.style.ViewStyles
import fr.unilim.saes5.view.utilities.ViewUtilities
import fr.unilim.saes5.view.utilities.ViewUtilities.openWordOccurrenceView
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.util.*


class ButtonBarView(
    private val lang: LangDAO,
    private val words: ObservableList<Word>,
    private val tokenInput: TextField,
    private val primaryContextInput: TextField,
    private val secondaryContextInput: TextField,
    private val synonymInput: TextField,
    private val antonymInput: TextField,
    private val definitionInput: TextArea,
    private val wordTableView: TableView<Word>? = null,
    name: String
) : View() {

    private val directoryDao: DirectoryDao = JsonDirectoryDao()
    private var lastOpenedDirectory: File? = null

    private val defaultDirectory: File = File(System.getProperty("user.home"))

    private fun openGitProjectDialog() {
        val dialog: Dialog<String> = Dialog()
        dialog.title = lang.getMessage("git_dialog_title")
        dialog.headerText = lang.getMessage("git_dialog_header")

        val openButtonType = ButtonType("Ouvrir", ButtonBar.ButtonData.OK_DONE)
        dialog.dialogPane.buttonTypes.addAll(openButtonType, ButtonType.CANCEL)

        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0
        grid.padding = Insets(20.0, 150.0, 10.0, 10.0)

        val gitUrlField = TextField()
        gitUrlField.promptText = "https://github.com/user/repo.git"

        grid.add(Label("Git URL:"), 0, 0)
        grid.add(gitUrlField, 1, 0)

        val openButton: Node = dialog.dialogPane.lookupButton(openButtonType)
        openButton.isDisable = true

        gitUrlField.textProperty().addListener { observable, oldValue, newValue ->
            openButton.isDisable = newValue.trim().isEmpty()
        }

        dialog.dialogPane.content = grid

        Platform.runLater(gitUrlField::requestFocus)

        dialog.setResultConverter { dialogButton ->
            if (dialogButton === openButtonType) {
                return@setResultConverter gitUrlField.text
            }
            null
        }

        val result: Optional<String> = dialog.showAndWait()

        result.ifPresent { gitUrl ->
            handleGitUrl(gitUrl)
        }
    }

    private fun handleGitUrl(gitUrl: String) {
        try {
            val gitProjectReader = GitProjectReader()
            val wordsFromGit = gitProjectReader.readFromGitUrl(gitUrl, "main")

            val analytics = WordAnalyticsService()
            val wordRank = analytics.wordRank(wordsFromGit)
            val wordsInListNotInGlossary = analytics.wordsInListNotInGlossary(
                wordRank.keys.map { it },
                Glossary(words)
            )
            val glossaryRatio = analytics.glossaryRatio(wordsFromGit, Glossary(words))

            Platform.runLater {
                openWordOccurrenceView(wordRank, wordsInListNotInGlossary, glossaryRatio, lang)
            }
        } catch (e: Exception) {
            Platform.runLater {
                val alert = Alert(
                    Alert.AlertType.ERROR,
                    "Failed to open Git project: ${e.message}",
                    ButtonType.OK
                )
                alert.showAndWait()
            }
        }
    }


    init {
        val savedDirectoryPath = directoryDao.retrieve()
        lastOpenedDirectory = if (savedDirectoryPath != null && savedDirectoryPath.isNotEmpty()) {
            File(savedDirectoryPath)
        } else {
            defaultDirectory
        }
    }

    override val root = hbox(20.0) {
        paddingBottom = 20.0
        paddingHorizontal = 20.0
        alignment = Pos.BASELINE_RIGHT

        button(lang.getMessage("button_quit")) {
            addClass(ViewStyles.helpButton)
            action {
                Platform.exit()
            }
        }
        button(lang.getMessage("button_help")) {
            addClass(ViewStyles.helpButton)
            action {
                val dialog = Dialog<ButtonType>().apply {
                    initOwner(this@ButtonBarView.currentWindow)
                    title = lang.getMessage("help_title")
                    dialogPane.buttonTypes.add(ButtonType.CLOSE)
                    val closeButton = dialogPane.lookupButton(ButtonType.CLOSE)
                    closeButton.addClass(ViewStyles.helpButton)
                    val textFlow = TextFlow(
                        Text(lang.getMessage("token_label") + "\n").apply { style = "-fx-font-weight: bold" },
                        Text(lang.getMessage("statute_obligatory") + "\n" + lang.getMessage("description_token") + "\n\n"),
                        Text(lang.getMessage("definition_label") + "\n").apply {
                            style = "-fx-font-weight: bold"
                        },
                        Text(lang.getMessage("statute_facultative") + "\n " + lang.getMessage("description_definition") + "\n\n"),
                        Text(lang.getMessage("primary_context_label") + "\n").apply {
                            style = "-fx-font-weight: bold"
                        },
                        Text(lang.getMessage("statute_obligatory") + "\n" + lang.getMessage("description_primary_context") + "\n\n"),
                        Text(lang.getMessage("secondary_context_label") + "\n").apply {
                            style = "-fx-font-weight: bold"
                        },
                        Text(lang.getMessage("statute_facultative") + "\n" + lang.getMessage("description_secondary_content") + "\n\n"),
                        Text(lang.getMessage("synonym_label") + "\n").apply { style = "-fx-font-weight: bold" },
                        Text(lang.getMessage("statute_facultative") + "\n" + lang.getMessage("description_synonym") + "\n\n"),
                        Text(lang.getMessage("antonym_label") + "\n").apply { style = "-fx-font-weight: bold" },
                        Text(lang.getMessage("statute_facultative") + "\n" + lang.getMessage("description_antonym") + "\n\n")
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
        button(lang.getMessage("button_download_file")) {
            addClass(ViewStyles.downloadButtonHover)
            action {
                val fileChooser = FileChooser().apply {
                    title = lang.getMessage("choose_files")
                    initialDirectory = lastOpenedDirectory ?: defaultDirectory
                }
                val selectedFiles = fileChooser.showOpenMultipleDialog(currentWindow)
                if (selectedFiles != null) {
                    lastOpenedDirectory = selectedFiles.first().parentFile
                    directoryDao.save(lastOpenedDirectory?.absolutePath)

                    val filePaths = selectedFiles.map { it.path }
                    val analysisWords = FileReader().read(filePaths)
                    val analytics = WordAnalyticsService()
                    val wordRank = analytics.wordRank(analysisWords)
                    val wordsInListNotInGlossary =
                        analytics.wordsInListNotInGlossary(wordRank.keys.toList().map { it }, Glossary(words))
                    val glossaryRatio = analytics.glossaryRatio(analysisWords, Glossary(words))
                    openWordOccurrenceView(wordRank, wordsInListNotInGlossary, glossaryRatio, lang)
                }
            }
        }
        button(lang.getMessage("button_download_folder")) {
            addClass(ViewStyles.downloadButtonHover)
            action {
                val directoryChooser = DirectoryChooser().apply {
                    title = lang.getMessage("choose_folder")
                    initialDirectory = lastOpenedDirectory ?: defaultDirectory
                }
                directoryChooser.showDialog(currentWindow)?.let { file ->
                    lastOpenedDirectory = file
                    directoryDao.save(lastOpenedDirectory?.absolutePath)

                    val analysisWords = FileReader().read(file.toString())
                    val analytics = WordAnalyticsService()
                    val wordRank = analytics.wordRank(analysisWords)
                    val wordsInListNotInGlossary =
                        analytics.wordsInListNotInGlossary(wordRank.keys.toList().map { it }, Glossary(words))
                    val glossaryRatio = analytics.glossaryRatio(analysisWords, Glossary(words))
                    openWordOccurrenceView(wordRank, wordsInListNotInGlossary, glossaryRatio, lang)
                }
            }
        }
        button(lang.getMessage("button_open_git")) {
            addClass(ViewStyles.downloadButtonHover)
            action {
                openGitProjectDialog()
            }
        }
        button(lang.getMessage("button_add")) {
            addClass(ViewStyles.addButton)
            action {
                if (tokenInput.text.isBlank() || primaryContextInput.text.isBlank()) {
                    alert(
                        type = Alert.AlertType.WARNING,
                        header = lang.getMessage("missing_fields_header"),
                        content = lang.getMessage("missing_fields_content")
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

                    val duplicate = words.any { it.token == newWord.token }
                    if (duplicate) {
                        alert(
                            type = Alert.AlertType.WARNING,
                            header = lang.getMessage("duplicate_header"),
                            content = lang.getMessage("duplicate_content")
                        )
                    } else {
                        words.add(newWord)
                        ViewUtilities.clearInputFields(
                            tokenInput,
                            primaryContextInput,
                            secondaryContextInput,
                            synonymInput,
                            antonymInput,
                            definitionInput
                        )
                        ViewUtilities.updateJsonFile(words, name)
                        ViewUtilities.updateCompletionService(name)
                        wordTableView?.refresh()
                    }
                }
            }
        }
    }
}
