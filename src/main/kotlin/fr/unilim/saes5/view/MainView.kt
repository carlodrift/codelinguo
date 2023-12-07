package fr.unilim.saes5.view

import fr.unilim.saes5.model.Project
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

    private val glossaryEntries = mutableListOf<GlossaryEntry>().asObservable()
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

    override val root = vbox(5.0) {
        tableview(glossaryEntries) {
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
            addClass(Styles.customTableView)
            readonlyColumn(myBundle.getString("token_label"), GlossaryEntry::mot)
            readonlyColumn(myBundle.getString("definition_label"), GlossaryEntry::definition)
            readonlyColumn(myBundle.getString("primary_context_label"), GlossaryEntry::primaryContext)
            readonlyColumn(myBundle.getString("secondary_context_label"), GlossaryEntry::secondaryContext)
            readonlyColumn(myBundle.getString("synonym_label"), GlossaryEntry::synonym)
            readonlyColumn(myBundle.getString("antonym_label"), GlossaryEntry::antonym)
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
                        dialogPane.setPrefSize(600.0, 350.0)
                    }

                    val owner = this@MainView.currentWindow

                    // Utilise Platform.runLater pour s'assurer que le calcul de la position se fait après le rendu de la boîte de dialogue
                    Platform.runLater {
                        if (owner != null) {
                            val scene = owner.scene
                            val x = owner.x + scene.x + (scene.width - dialog.dialogPane.width) / 2
                            val y = owner.y + scene.y + (scene.height - dialog.dialogPane.height) / 2
                            dialog.x = x
                            dialog.y = y
                        }
                    }

                    // Affiche la boîte de dialogue et attend que l'utilisateur la ferme
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
                    val newEntry = GlossaryEntry(
                        mot = motInput.text,
                        definition = definitionInput.text,
                        primaryContext = primaryContextInput.text,
                        secondaryContext = secondaryContextInput.text,
                        synonym = synonymeInput.text,
                        antonym = antonymeInput.text
                    )

                    val duplicate = glossaryEntries.any { it.mot == newEntry.mot }
                    if (duplicate) {
                        alert(
                            type = Alert.AlertType.WARNING,
                            header = myBundle.getString("duplicate_header"),
                            content = myBundle.getString("duplicate_content")
                        )
                    } else {
                        glossaryEntries.add(newEntry)
                        
                        motInput.clear()
                        synonymeInput.clear()
                        definitionInput.clear()
                        primaryContextInput.clear()
                        antonymeInput.clear()
                        secondaryContextInput.clear()
                    }
                }
            }

            // Lier la propriété 'disable' à la condition de validation
            addButton.disableProperty().bind(
                motInput.textProperty().isBlank()
                    .or(primaryContextInput.textProperty().isBlank())
            )
        }
    }
}
