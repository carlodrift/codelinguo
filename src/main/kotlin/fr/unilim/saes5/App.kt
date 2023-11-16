package fr.unilim.saes5

import com.sun.javafx.binding.BidirectionalBinding.bind
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*

class MyApp: App(HelloWorldView::class)
class GlossaryEntry(val mot: String, val definition: String, val primaryContext: String, val secondaryContext: String, val synonym: String, val antonym: String)
{
    override fun toString(): String {
        return "GlossaryEntry(mot='$mot', definition='$definition', primaryContext='$primaryContext', secondaryContext='$secondaryContext', synonym='$synonym', antonym='$antonym')"
    }
}

class HelloWorldView : View() {

    private val glossaryEntries = mutableListOf<GlossaryEntry>().observable()

    private val motInput = textfield { promptText = "Joyeux" }
    private val synonymeInput = textfield { promptText = "Heureux" }
    private val definitionInput = textarea { promptText = "Qui éprouve de la joie." }
    private val primaryContextInput = textfield { promptText = "Joueur" }
    private val antonymeInput = textfield { promptText = "Aigri" }
    private val secondaryContextInput = textfield { promptText = "Psychologie" }

    override val root = vbox(10.0) {
        paddingAll = 20.0

        tableview(glossaryEntries) {
            columnResizePolicy = SmartResize.POLICY
            readonlyColumn("Mot", GlossaryEntry::mot)
            readonlyColumn("Définition", GlossaryEntry::definition)
            readonlyColumn("Contexte Principal", GlossaryEntry::primaryContext)
            readonlyColumn("Contexte Secondaire", GlossaryEntry::secondaryContext)
            readonlyColumn("Synonyme", GlossaryEntry::synonym)
            readonlyColumn("Antonyme", GlossaryEntry::antonym)
            prefHeight = 200.0
        }
        form {
            fieldset {
                field("Mot") { this += motInput }
                field("Synonyme") { this += synonymeInput }
                field("Définition") { this += definitionInput }
                field("Contexte Principal") { this += primaryContextInput }
                field("Antonyme") { this += antonymeInput }
                field("Contexte Secondaire") { this += secondaryContextInput }
            }
        }

        hbox(20.0) {
            button("Aide") {
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
            button("Télécharger un fichier") {
                action {
                    val fileChooser = FileChooser().apply {
                        title = "Choisir un fichier"
                        extensionFilters.addAll(
                            FileChooser.ExtensionFilter("Tous les Fichiers", "*.*"),
                        )
                    }
                    val selectedFile = fileChooser.showOpenDialog(currentWindow)
                    if (selectedFile != null) {
                        println(selectedFile)
                    }
                }
                style {
                    fontSize = 18.px
                }
            }
            button("Télécharger un dossier") {
                action {
                    val directoryChooser = DirectoryChooser().apply {
                        title = "Choisir un dossier"
                    }
                    val selectedDirectory = directoryChooser.showDialog(currentWindow)
                    if (selectedDirectory != null) {
                        println(selectedDirectory)
                    }
                }
                style {
                    fontSize = 18.px
                }
            }
            val addButton = button("Ajouter") {
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
