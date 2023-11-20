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
            backgroundColor += c("#4caf50")
        }

        helpButton {
            fontSize = 16.px
            backgroundColor += c("#f0ad4e")
        }

        downloadButton {
            fontSize = 16.px
            backgroundColor += c("#337ab7")
        }
    }
}
