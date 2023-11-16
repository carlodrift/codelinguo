package fr.unilim.saes5

import tornadofx.*

class MyApp: App(HelloWorldView::class)
class GlossaryEntry(val mot: String, val definition: String, val primaryContext: String, val secondaryContext: String, val synonym: String, val antonym: String)

class HelloWorldView : View() {

    private val glossaryEntries = listOf(
        GlossaryEntry("mot1", "definition1", "context1", "context2", "synonym1", "antonym1"),
    ).observable()

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
            prefHeight = 200.0 // Réglez cela comme vous le souhaitez
        }
        form {
            fieldset {
                field("Mot") { textfield{promptText="Joyeux"} }
                field("Synonyme") { textfield{promptText="Heureux"} }
                field("Définition") { textarea{promptText="Qui éprouve de la joie."} }
                field("Contexte Principal") { textfield{promptText="Joueur"} }
                field("Antonyme") { textfield{promptText="Aigri"} }
                field("Contexte Secondaire") { textfield{promptText="Psycologie"} }
            }
        }

        hbox(20.0) {
            button("Aide") {
                action {
                    println("Aide")
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
                    println("Ajouter")
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
