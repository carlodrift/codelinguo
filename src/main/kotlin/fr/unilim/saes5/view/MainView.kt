package fr.unilim.saes5.view

import fr.unilim.saes5.model.Project
import fr.unilim.saes5.model.reader.JavaFileReader
import fr.unilim.saes5.persistence.JsonProjectDao
import fr.unilim.saes5.service.WordAnalyticsService
import fr.unilim.saes5.view.Styles
import javafx.scene.control.*
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.util.*

class MainView : View() {

    private val glossaryEntries = mutableListOf<GlossaryEntry>().observable()
    private val myBundle = ResourceBundle.getBundle("Messages", Locale.getDefault())

    private val motInput: TextField = textfield { addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_joyeux")}
    private val synonymeInput: TextField = textfield { addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_heureux") }
    private val definitionInput: TextArea = textarea { addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_definition") }
    private val primaryContextInput: TextField = textfield { addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_joueur") }
    private val antonymeInput: TextField = textfield { addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_aigri") }
    private val secondaryContextInput: TextField = textfield { addClass(Styles.customTextField)
        promptText = myBundle.getString("prompt_psychologie") }

    override val root = vbox(10.0) {
        paddingAll = 20.0

        tableview(glossaryEntries) {
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            addClass(Styles.customTableView)
            readonlyColumn(myBundle.getString("token_label"), GlossaryEntry::mot)
            readonlyColumn(myBundle.getString("definition_label"), GlossaryEntry::definition)
            readonlyColumn(myBundle.getString("primary_context_label"), GlossaryEntry::primaryContext)
            readonlyColumn(myBundle.getString("secondary_context_label"), GlossaryEntry::secondaryContext)
            readonlyColumn(myBundle.getString("synonym_label"), GlossaryEntry::synonym)
            readonlyColumn(myBundle.getString("antonym_label"), GlossaryEntry::antonym)
            prefHeight = 200.0
        }
        form {
            fieldset {
                field(myBundle.getString("token_label")) { this += motInput }
                field(myBundle.getString("synonym_label")) { this += synonymeInput }
                field(myBundle.getString("definition_label")) { this += definitionInput }
                field(myBundle.getString("primary_context_label")) { this += primaryContextInput }
                field(myBundle.getString("antonym_label")) { this += antonymeInput }
                field(myBundle.getString("secondary_context_label")) { this += secondaryContextInput }
            }
        }

        hbox(20.0) {
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
                            Text(myBundle.getString("definition_label") + "\n").apply { style = "-fx-font-weight: bold" },
                            Text(myBundle.getString("statute_facultative") + "\n " + myBundle.getString("description_definition") + "\n\n"),
                            Text(myBundle.getString("primary_context_label") + "\n").apply { style = "-fx-font-weight: bold" },
                            Text(myBundle.getString("statute_obligatory") + "\n" + myBundle.getString("description_primary_context") + "\n\n"),
                            Text(myBundle.getString("secondary_context_label") + "\n").apply { style = "-fx-font-weight: bold" },
                            Text(myBundle.getString("statute_facultative") + "\n" + myBundle.getString("description_secondary_content") + "\n\n"),
                            Text(myBundle.getString("synonym_label") + "\n").apply { style = "-fx-font-weight: bold" },
                            Text(myBundle.getString("statute_facultative") +"\n" + myBundle.getString("description_synonym") + "\n\n"),
                            Text(myBundle.getString("antonym_label") + "\n").apply { style = "-fx-font-weight: bold" },
                            Text(myBundle.getString("statute_facultative") + "\n" + myBundle.getString("description_antonym") + "\n\n")
                        )

                        dialogPane.content = textFlow
                        dialogPane.setPrefSize(600.0, 350.0)
                    }
                    dialog.showAndWait()
                }
            }
            button(myBundle.getString("button_download_file")) {
                addClass(Styles.downloadButton)
                action {
                    val fileChooser = FileChooser().apply {
                        title = "Choisir un fichier"
                        extensionFilters.addAll(
                            FileChooser.ExtensionFilter("Tous les Fichiers", "*.*"),
                        )
                    }
                    val selectedFile = fileChooser.showOpenDialog(currentWindow)
                    if (selectedFile != null) {
                        val words = JavaFileReader().readOne(selectedFile.toString())
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
                        println(selectedFile)
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
                    glossaryEntries.add(newEntry)

                    // Réinitialiser les champs de texte
                    motInput.clear()
                    synonymeInput.clear()
                    definitionInput.clear()
                    primaryContextInput.clear()
                    antonymeInput.clear()
                    secondaryContextInput.clear()
                }
            }

            // Lier la propriété 'disable' à la condition de validation
            addButton.disableProperty().bind(
                motInput.textProperty().isBlank()
                    .or(synonymeInput.textProperty().isBlank())
                    .or(definitionInput.textProperty().isBlank())
                    .or(primaryContextInput.textProperty().isBlank())
                    .or(antonymeInput.textProperty().isBlank())
                    .or(secondaryContextInput.textProperty().isBlank())
            )
        }
    }
}