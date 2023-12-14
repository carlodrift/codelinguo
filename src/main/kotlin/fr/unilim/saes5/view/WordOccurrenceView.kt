package fr.unilim.saes5.view

import javafx.collections.FXCollections
import javafx.scene.layout.Priority
import tornadofx.*
import java.util.*

class WordOccurrenceView(wordRank: Map<String, Int>, private val myBundle: ResourceBundle) : Fragment() {
    private val wordRankList = FXCollections.observableArrayList(wordRank.entries.toList())

    private val generalView = tableview(wordRankList) {
        addClass(ViewStyles.customTableView)
        readonlyColumn(myBundle.getString("wordoccurrenceview_word"), Map.Entry<String, Int>::key)
        readonlyColumn(myBundle.getString("wordoccurrenceview_occurrences"), Map.Entry<String, Int>::value)
        columnResizePolicy = SmartResize.POLICY
    }

    private val detailsView = vbox {
        label("La terminologie du document est la suivante : ")
        // Ajoutez ici le code pour afficher la phrase et le graphique
    }

    override val root = borderpane {
        minWidth = 600.0
        minHeight = 400.0

        top = hbox {
            paddingAll = 10.0
            spacing = 10.0
            button(myBundle.getString("button_general")) {
                addClass(ViewStyles.helpButton)
                action {
                    center = generalView
                }
            }
            button(myBundle.getString("button_details")) {
                addClass(ViewStyles.helpButton)
                action {
                    center = detailsView
                }
            }
            region {
                hgrow = Priority.ALWAYS
            }
            button(myBundle.getString("button_close")) {
                addClass(ViewStyles.downloadButton)
                action {
                    close()
                }
            }
        }

        center = generalView

    }
}
