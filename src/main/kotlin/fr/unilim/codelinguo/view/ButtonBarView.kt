package fr.unilim.codelinguo.view

import fr.unilim.codelinguo.model.Glossary
import fr.unilim.codelinguo.model.Word
import fr.unilim.codelinguo.model.context.PrimaryContext
import fr.unilim.codelinguo.model.context.SecondaryContext
import fr.unilim.codelinguo.model.reader.FileReader
import fr.unilim.codelinguo.model.reader.GitProjectReader
import fr.unilim.codelinguo.persistence.directory.DirectoryDao
import fr.unilim.codelinguo.persistence.directory.JsonDirectoryDao
import fr.unilim.codelinguo.persistence.lang.LangDAO
import fr.unilim.codelinguo.persistence.recent_git_url.JsonRecentGitURLDAO
import fr.unilim.codelinguo.service.WordAnalyticsService
import fr.unilim.codelinguo.view.style.ViewStyles
import fr.unilim.codelinguo.view.utilities.ViewUtilities
import fr.unilim.codelinguo.view.utilities.ViewUtilities.openWordOccurrenceView
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import org.controlsfx.control.PopOver
import tornadofx.*
import java.io.File


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
    private val name: String
) : View() {

    private val directoryDao: DirectoryDao = JsonDirectoryDao()
    private var lastOpenedDirectory: File? = null

    private val defaultDirectory: File = File(System.getProperty("user.home"))

    private fun setupAnalyzePopOver(): PopOver {
        val popOver = PopOver()
        popOver.arrowLocation = PopOver.ArrowLocation.BOTTOM_CENTER

        val vbox = VBox(10.0).apply {
            padding = Insets(10.0)
            children.addAll(
                Button(lang.getMessage("button_download_file")).apply {
                    addClass(ViewStyles.downloadButtonHover)
                    maxWidth = Double.MAX_VALUE
                    action {
                        popOver.hide()
                        handleFileSelection()
                    }
                },
                Button(lang.getMessage("button_download_folder")).apply {
                    addClass(ViewStyles.downloadButtonHover)
                    maxWidth = Double.MAX_VALUE
                    action {
                        popOver.hide()
                        handleFolderSelection()
                    }
                },
                Button(lang.getMessage("button_open_git")).apply {
                    addClass(ViewStyles.downloadButtonHover)
                    maxWidth = Double.MAX_VALUE
                    action {
                        popOver.hide()
                        handleGitSelection()
                    }
                }
            )
        }

        popOver.contentNode = vbox
        return popOver
    }

    private fun handleFileSelection() {
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
            val fileName = if (selectedFiles.size == 1) {
                selectedFiles.first().name
            } else {
                lastOpenedDirectory?.name ?: ""
            }
            openWordOccurrenceView(wordRank, wordsInListNotInGlossary, glossaryRatio, lang, name, fileName)
        }
    }

    private fun handleFolderSelection() {
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
            openWordOccurrenceView(wordRank, wordsInListNotInGlossary, glossaryRatio, lang, name, file.name)
        }
    }

    private fun handleGitSelection() {
        val dialog: Dialog<Pair<String, String>> = Dialog()

        val openButtonType = ButtonType(lang.getMessage("button_open"), ButtonBar.ButtonData.OK_DONE)

        dialog.dialogPane.buttonTypes.addAll(openButtonType, ButtonType.CANCEL)

        val grid = GridPane().apply {
            hgap = 10.0
            vgap = 10.0
            padding = Insets(20.0, 150.0, 10.0, 10.0)
        }

        val gitUrlComboBox = ComboBox<String>().apply {
            isEditable = true
            items.addAll(JsonRecentGitURLDAO().retrieve())
        }

        val branchField = TextField().apply {
            promptText = lang.getMessage("branch_default_name")
            text = lang.getMessage("branch_default_name")
        }

        grid.add(Label(lang.getMessage("url_label")), 0, 0)
        grid.add(gitUrlComboBox, 1, 0)
        grid.add(Label(lang.getMessage("branch_label")), 0, 1)
        grid.add(branchField, 1, 1)

        val openButton: Node = dialog.dialogPane.lookupButton(openButtonType)
        openButton.isDisable = true

        gitUrlComboBox.editor.textProperty().addListener { _, _, newValue ->
            openButton.isDisable = newValue.trim().isEmpty()
        }

        dialog.dialogPane.content = grid

        Platform.runLater { gitUrlComboBox.requestFocus() }

        dialog.setResultConverter { dialogButton ->
            if (dialogButton == openButtonType) {
                Pair(gitUrlComboBox.value ?: gitUrlComboBox.editor.text, branchField.text)
            } else null
        }

        val result = dialog.showAndWait()

        result.ifPresent { gitUrlBranchPair ->
            handleGitUrl(gitUrlBranchPair.first, gitUrlBranchPair.second)
        }
    }

    private fun handleGitUrl(gitUrl: String, branchName: String) {
        try {
            val gitProjectReader = GitProjectReader()
            val wordsFromGit = gitProjectReader.readFromGitUrl(gitUrl, branchName)

            val analytics = WordAnalyticsService()
            val wordRank = analytics.wordRank(wordsFromGit)
            val wordsInListNotInGlossary = analytics.wordsInListNotInGlossary(
                wordRank.keys.map { it },
                Glossary(words)
            )
            val glossaryRatio = analytics.glossaryRatio(wordsFromGit, Glossary(words))

            val fileName = gitUrl.substringAfterLast("/").substringBefore(".")
            Platform.runLater {
                openWordOccurrenceView(wordRank, wordsInListNotInGlossary, glossaryRatio, lang, name, fileName)
            }

            JsonRecentGitURLDAO().add(gitUrl)
        } catch (e: Exception) {
            Platform.runLater {
                val alert = Alert(
                    Alert.AlertType.ERROR,
                    "${e.message}",
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
        val analyzeButton = Button(lang.getMessage("button_analyze")).apply {
            addClass(ViewStyles.downloadButtonHover)
            val popOver = setupAnalyzePopOver()
            action {
                if (!popOver.isShowing) {
                    popOver.show(this)
                } else {
                    popOver.hide()
                }
            }
        }

        this += analyzeButton
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
