package fr.unilim.saes5

import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val addButton by cssclass()
        val helpButton by cssclass()
        val downloadButton by cssclass()
    }

    init {
        heading {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        addButton {
            fontSize = 18.px
            backgroundColor += c("#90FF8D")
            backgroundRadius += box(10.px)
            borderRadius += box(10.px)
            borderWidth += box(1.px)
            borderColor += box(c("#555555"))

            and(hover) {
                backgroundColor += c("#6DEB67")
            }
            // ... autres propriétés
        }

        helpButton {
            fontSize = 18.px
            backgroundColor += c("#EAEAEA")
            backgroundRadius += box(10.px)
            borderRadius += box(10.px)
            borderWidth += box(1.px)
            borderColor += box(c("#555555"))

            and(hover) {
                backgroundColor += c("#CFCFCF")
            }
            // ... autres propriétés
        }

        downloadButton {
            fontSize = 18.px
            backgroundColor += c("#20D7FF")
            backgroundRadius += box(10.px)
            borderRadius += box(10.px)
            borderWidth += box(1.px)
            borderColor += box(c("#555555"))

            and(hover) {
                backgroundColor += c("#1AB6E8")
            }
            // ... autres propriétés
        }
    }
}
