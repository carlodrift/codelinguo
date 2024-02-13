package fr.unililim.codelinguo.desktop.view.style

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class ViewStyles : Stylesheet() {
    companion object {
        val test by cssclass()
        val heading by cssclass()
        val addButton by cssclass()
        val helpButton by cssclass()
        val downloadButton by cssclass()
        val downloadButtonHover by cssclass()
        val customTextField by cssclass()
        val customTableView by cssclass()
        val customTableHeader by cssclass()
        val removeButton by cssclass()
        val projectButton by cssclass()
        val separator by cssclass()
        val openButton by cssclass()
        val clickDetailLabel by cssclass()
    }

    init {
        heading {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        test {
            backgroundColor += c("#f70505")
        }

        projectButton {
            fontSize = 18.px
            cursor = Cursor.HAND
            backgroundRadius += box(15.px)
            backgroundColor += c("#ffffff")
            borderColor += box(c("#000000"))
            borderRadius += box(15.px)
            fontFamily = listOf("DM Sans", "Arial", "Helvetica", "sans-serif").joinToString(",")

            and(hover) {
                backgroundColor += c("#D7D7D7")
            }
        }

        openButton {
            fontSize = 13.px
            cursor = Cursor.HAND
            backgroundRadius += box(15.px)
            backgroundColor += c("#ffffff")
            borderColor += box(c("#000000"))
            borderRadius += box(10.px)
            fontFamily = listOf("DM Sans", "Arial", "Helvetica", "sans-serif").joinToString(",")

            and(hover) {
                backgroundColor += c("#D7D7D7")
            }
        }

        addButton {
            fontSize = 18.px
            cursor = Cursor.HAND
            backgroundColor += c("#84B71A")
            backgroundRadius += box(15.px)
            textFill = c("#ffffff")
            fontFamily = listOf("DM Sans", "Arial", "Helvetica", "sans-serif").joinToString(",")

            and(hover) {
                backgroundColor += c("#388E3C")
            }
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
        }

        downloadButton {
            fontSize = 18.px
            cursor = Cursor.HAND
            backgroundColor += c("#000000")
            backgroundRadius += box(15.px)
            textFill = c("#ffffff")
            fontFamily = listOf("DM Sans", "Arial", "Helvetica", "sans-serif").joinToString(",")

        }

        downloadButtonHover {
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
            backgroundColor += c("#ffffff")
            borderColor += box(c(0, 0, 0, 1.0))
            backgroundRadius += box(15.px)
            borderRadius += box(15.px)

            s(".scroll-pane") {
                backgroundColor += c("#ffffff")
                backgroundInsets += box(0.px)
                backgroundRadius += box(15.px)
            }

            s(".content") {
                backgroundColor += c("#ffffff")
                backgroundInsets += box(0.px)
                backgroundRadius += box(15.px)
            }

            s(".viewport") {
                backgroundColor += c("#ffffff")
                backgroundInsets += box(0.px)
                backgroundRadius += box(15.px)
            }
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
            backgroundColor += Color.TRANSPARENT
            borderColor += box(Color.TRANSPARENT)
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

        separator {
            backgroundColor += c("#000000")
        }

        clickDetailLabel {
            fontSize = 14.px
            textFill = c("#616161")
            padding = box(5.px)
            alignment = Pos.CENTER
        }
    }


}
