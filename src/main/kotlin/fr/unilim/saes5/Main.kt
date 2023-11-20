package fr.unilim.saes5

import fr.unilim.saes5.model.Project
import fr.unilim.saes5.model.reader.JavaFileReader
import fr.unilim.saes5.persistence.JsonProjectDao
import fr.unilim.saes5.service.WordAnalyticsService
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.util.*

class MyApp: App(HelloWorldView::class)
class GlossaryEntry(val mot: String, val definition: String, val primaryContext: String, val secondaryContext: String, val synonym: String, val antonym: String)
{
    override fun toString(): String {
        return "GlossaryEntry(mot='$mot', definition='$definition', primaryContext='$primaryContext', secondaryContext='$secondaryContext', synonym='$synonym', antonym='$antonym')"
    }
}

class HelloWorldView : View() {

    private val glossaryEntries = mutableListOf<GlossaryEntry>().observable()
    private val myBundle = ResourceBundle.getBundle("Messages", Locale.getDefault())

    private val motInput: TextField = textfield { promptText = myBundle.getString("prompt_joyeux") }
    private val synonymeInput: TextField = textfield { promptText = myBundle.getString("prompt_heureux") }
    private val definitionInput: TextArea = textarea { promptText = myBundle.getString("prompt_definition") }
    private val primaryContextInput: TextField = textfield { promptText = myBundle.getString("prompt_joueur") }
    private val antonymeInput: TextField = textfield { promptText = myBundle.getString("prompt_aigri") }
    private val secondaryContextInput: TextField = textfield { promptText = myBundle.getString("prompt_psychologie") }

    override val root = vbox(10.0) {
        paddingAll = 20.0

        tableview(glossaryEntries) {
            columnResizePolicy = SmartResize.POLICY
            readonlyColumn(myBundle.getString("token_label"), GlossaryEntry::mot).remainingWidth()
            readonlyColumn(myBundle.getString("definition_label"), GlossaryEntry::definition).remainingWidth()
            readonlyColumn(myBundle.getString("primary_context_label"), GlossaryEntry::primaryContext).remainingWidth()
            readonlyColumn(myBundle.getString("secondary_context_label"), GlossaryEntry::secondaryContext).remainingWidth()
            readonlyColumn(myBundle.getString("synonym_label"), GlossaryEntry::synonym).remainingWidth()
            readonlyColumn(myBundle.getString("antonym_label"), GlossaryEntry::antonym).remainingWidth()
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
                action {
                    val dialog = Dialog<ButtonType>().apply {
                        initOwner(this@HelloWorldView.currentWindow)
                        title = "Aide pour le Glossaire"
                        dialogPane.buttonTypes.add(ButtonType.CLOSE)

                        val textFlow = TextFlow(
                            Text("Token\n").apply { style = "-fx-font-weight: bold" },
                            Text("Statut : Obligatoire\nDescription : Le \"token\" est le terme principal ou le mot-clé que vous souhaitez ajouter au glossaire. Il sert de référence principale pour l'entrée du glossaire.\n\n"),
                            Text("Définition\n").apply { style = "-fx-font-weight: bold" },
                            Text("Statut : Facultatif\nDescription : Dans ce champ, vous pouvez fournir une définition ou une explication détaillée du \"token\". Bien que ce champ soit facultatif, il est recommandé de fournir une définition pour clarifier l'usage et la signification du \"token\".\n\n"),
                            Text("Contexte Principal\n").apply { style = "-fx-font-weight: bold" },
                            Text("Statut : Obligatoire\nDescription : Le \"contexte principal\" est l'environnement ou la situation dans laquelle le \"token\" est principalement utilisé. Ce champ est obligatoire pour aider à contextualiser le \"token\" et à comprendre son application dans un contexte spécifique.\n\n"),
                            Text("Contexte 2\n").apply { style = "-fx-font-weight: bold" },
                            Text("Statut : Facultatif\nDescription : Ce champ vous permet de fournir un contexte supplémentaire ou secondaire dans lequel le \"token\" peut être utilisé. Cela peut aider à donner une vue plus complète de l'utilisation du \"token\".\n\n"),
                            Text("Synonyme\n").apply { style = "-fx-font-weight: bold" },
                            Text("Statut : Facultatif\nDescription : Ici, vous pouvez lister tout synonyme du \"token\". Les synonymes sont des mots qui ont une signification similaire ou identique au \"token\".\n\n"),
                            Text("Antonyme\n").apply { style = "-fx-font-weight: bold" },
                            Text("Statut : Facultatif\nDescription : Dans ce champ, vous pouvez fournir des mots qui ont une signification opposée au \"token\". Les antonymes peuvent aider à clarifier la signification du \"token\" en indiquant ce qu'il n'est pas.\n\n")
                        )

                        dialogPane.content = textFlow
                        dialogPane.setPrefSize(600.0, 350.0) // Ajustez ces valeurs selon vos besoins
                    }
                    dialog.showAndWait()
                }
                style {
                    fontSize = 18.px
                }
            }
            button(myBundle.getString("button_download_file")) {
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
                style {
                    fontSize = 18.px
                }
            }
            button(myBundle.getString("button_download_folder")) {
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
                style {
                    fontSize = 18.px
                }
            }
            val addButton = button(myBundle.getString("button_add")) {
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
                style {
                    fontSize = 18.px
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


fun main(args: Array<String>) {
    launch<MyApp>(args)
}
