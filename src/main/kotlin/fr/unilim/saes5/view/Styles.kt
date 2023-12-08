package fr.unilim.saes5.view

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val addButton by cssclass()
        val helpButton by cssclass()
        val downloadButton by cssclass()
        val customTextField by cssclass()
        val customTableView by cssclass()
        val customTableHeader by cssclass()
        val removeButton by cssclass()
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
            fontFamily = listOf("DM Sans", "Arial", "Helvetica", "sans-serif").joinToString(",")

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
            fontFamily = listOf("DM Sans", "Arial", "Helvetica", "sans-serif").joinToString(",")

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
            backgroundRadius += box(15.px)
            borderRadius += box(15.px)
        }

        customTableView {
            backgroundColor += c("#E5E5E5")
            borderWidth += box(1.px)
            borderColor += box(c("#8C7E7E"))
            tabMaxWidth = 200.px
        }

        customTableHeader {
            textFill = c("#FFFFFF")
            backgroundColor += c("#736C6C")
        }


        removeButton {
            backgroundColor += Color.TRANSPARENT // Fond transparent
            borderColor += box(Color.TRANSPARENT) // Bordure transparente
            textFill = Color.RED
            fontWeight = FontWeight.BOLD
            padding = box(0.px, 0.px, 0.px, 0.px)

            and(hover) {
                scaleX = 1.2
                scaleY = 1.2
            }
        }

        tableCell {
            alignment = Pos.CENTER
        }

        columnHeader {
            label {
                alignment = Pos.CENTER
            }
        }
    }


}
