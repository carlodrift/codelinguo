package fr.unilim.saes5

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
                    println("Télécharger")
                }
                style {
                    fontSize = 18.px
                }
            }
            button("Télécharger") {
                action {
                    println("Télécharger")
                }
                style {
                    fontSize = 18.px
                }
            }
            button("Ajouter") {
                action {
                    val newEntry = GlossaryEntry(
                        motInput.text,
                        definitionInput.text,
                        primaryContextInput.text,
                        secondaryContextInput.text,
                        synonymeInput.text,
                        antonymeInput.text
                    )
                    glossaryEntries.add(newEntry)

                    // Réinitialise les champs de texte
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
        }
    }
}


fun main(args: Array<String>) {
    launch<MyApp>(args)
}
