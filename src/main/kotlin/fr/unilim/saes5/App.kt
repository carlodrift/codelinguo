package fr.unilim.saes5

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TextField
import tornadofx.*
import kotlin.random.Random

class MyApp: App(TestView::class)

class GlossaryEntry(val mot: String, val definition: String, val primaryContext: String, val secondaryContext: String, val synonym: String, val antonym: String)
class Foo(y: Int, z: String) {
    val yProperty = SimpleIntegerProperty(y)
    var y by yProperty
    val zProperty = SimpleStringProperty(z)
    var z by zProperty
}
class TestView : View("Hello TornadoFX") {
    val list = SortedFilteredList<Foo>().apply {
        for(i in 0..10) {
            add(Foo(Random.nextInt(0, 100), "Row $i"))
        }
    }
    var column1Field: TextField by singleAssign()
    var column2Field: TextField by singleAssign()

    override val root = vbox {
        tableview(list) {
            column("", Foo::yProperty) { //Put column name in graphic so text field will be below label
                graphic = vbox(2) {
                    label("Y Column")
                    column1Field = textfield()
                }
            }
            column("", Foo::zProperty) {
                graphic = vbox(2){
                    label("Z Column")
                    column2Field = textfield()
                }
            }
        }
    }

    init {
        column1Field.textProperty().onChange { filterList() }
        column2Field.textProperty().onChange { filterList() }
    }

    fun filterList() {
        list.predicate = { row ->
            val c1Text = column1Field.text
            val c1Filter = if(!c1Text.isNullOrBlank()) row.y.toString().contains(c1Text, true)
            else true

            val c2Text = column2Field.text
            val c2Filter = if(!c2Text.isNullOrBlank()) row.z.contains(c2Text, true)
            else true

            c1Filter && c2Filter
        }
    }
}

class HelloWorldView : View() {
    private val glossaryEntries = listOf(
        GlossaryEntry("mot1", "definition1", "context1", "context2", "synonym1", "antonym1"),
        // Ajoutez d'autres entrées si nécessaire
    ).observable()



    override val root = vbox(10.0) {
        paddingAll = 20.0

        hbox(10.0) {
            // TableView pour les entrées de glossaire
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
