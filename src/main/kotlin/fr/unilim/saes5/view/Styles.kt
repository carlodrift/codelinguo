package fr.unilim.saes5.view

import javafx.scene.text.FontWeight
import tornadofx.*
import javafx.scene.Cursor

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val addButton by cssclass()
        val helpButton by cssclass()
        val downloadButton by cssclass()
        val customTextField by cssclass()
        val customTableView by cssclass()
        val customTableHeader by cssclass()
    }

    init {
        heading {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        addButton {
            fontSize = 18.px // Taille de police en pixels
            cursor = Cursor.HAND
            backgroundColor += c("#84B71A")
            backgroundRadius += box(15.px)
            textFill = c("#ffffff")

            and(hover) {
                backgroundColor += c("#388E3C")
            }
            // ... autres propriétés
        }

        helpButton {
            fontSize = 18.px
            cursor = Cursor.HAND
            backgroundColor += c("#FFFFFF")
            backgroundRadius += box(15.px)
            borderRadius += box(15.px)
            borderWidth += box(1.px)
            borderColor += box(c("#000000"))

            and(hover) {
                backgroundColor += c("#D7D7D7")
            }
            // ... autres propriétés
        }

        downloadButton {
            fontSize = 18.px
            cursor = Cursor.HAND
            backgroundColor += c("#000000")
            backgroundRadius += box(15.px)
            textFill = c("#ffffff")
            fontFamily = listOf("DM Sans", "Arial", "Helvetica", "sans-serif").joinToString(",")


            and(hover) {
                backgroundColor += c("#5C5E60")
            }
        }

        customTextField {
            borderWidth += box(1.px)
            borderColor += box(c(0, 0, 0, 1.0))
        }

        customTableView {
            backgroundColor += c("#E5E5E5")
            borderWidth += box(1.px)
            borderColor += box(c("#8C7E7E"))
            tabMaxWidth = 200.px
        }

        customTableHeader{
            textFill = c("#FFFFFF")
            backgroundColor += c("#736C6C")
        }
    }
}
