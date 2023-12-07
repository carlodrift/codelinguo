package fr.unilim.saes5.view

import fr.unilim.saes5.model.Project
import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.context.PrimaryContext
import fr.unilim.saes5.model.context.SecondaryContext
import fr.unilim.saes5.model.reader.JavaFileReader
import fr.unilim.saes5.persistence.JsonProjectDao
import fr.unilim.saes5.service.WordAnalyticsService
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.util.*

class MainView : View() {

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
        val projectDao = JsonProjectDao("glossary.json")
        val projects = projectDao.allProjects

        projects.forEach { project ->
            project.words?.forEach { word ->
                if (!words.contains(word)) {
                    words.add(word)
                }
            }
        }
    }

    override val root = vbox(5.0) {
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
                        for (file in selectedFiles) {
                            val words = JavaFileReader().readOne(file.toString())
                            println(words.size.toString() + " mots ont été trouvés dans ${file.name}:")

                            println("---------------------------------------------")

                            val analytics = WordAnalyticsService()
                            val wordRank = analytics.wordRank(words)

                            println("Voici la liste des mots trouvés par occurence dans ${file.name}")
                            for ((word, count) in wordRank) {
                                println("$word  $count")
                            }

                            val projectDao = JsonProjectDao("projects.json")
                            val project = Project(words.map { it }.toList())
                            projectDao.saveProject(project)
                            println(file)
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
                        val words = JavaFileReader().read(selectedDirectory.toString())
                        println(words.size.toString() + " mots ont été trouvés :")

                        println("---------------------------------------------")

                        val analytics = WordAnalyticsService()
                        val wordRank = analytics.wordRank(words)

                        println("Voici la liste des mots trouvés par occurence")
                        for ((word, count) in wordRank) {
                            println("$word  $count")
                        }

                        val projectDao = JsonProjectDao("projects.json")
                        val project = Project(words.map { it }.toList())
                        projectDao.saveProject(project)
                        println(selectedDirectory)
                    }
                }
            }
            val addButton = button(myBundle.getString("button_add")) {
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
                            motInput.clear()
                            synonymeInput.clear()
                            definitionInput.clear()
                            primaryContextInput.clear()
                            antonymeInput.clear()
                            secondaryContextInput.clear()

                            val projectDao = JsonProjectDao("glossary.json")
                            val project = Project(words.toList())
                            projectDao.saveProject(project)
                        }
                    }
                }

            }
        }
    }
}
