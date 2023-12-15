package fr.unilim.saes5.view

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.Node
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
    }

    private val activeViewProperty = SimpleObjectProperty<Node>(generalView)

    private val generalButton = button(myBundle.getString("button_general")) {
        toggleClass(ViewStyles.downloadButton, true)
        action {
            activeViewProperty.value = generalView
        }
    }

    private val detailsButton = button(myBundle.getString("button_details")) {
        toggleClass(ViewStyles.helpButton, true)
        action {
            activeViewProperty.value = detailsView
        }
    }

    private val closeButton = button(myBundle.getString("button_close")) {
        addClass(ViewStyles.downloadButton)
        action {
            close()
        }
    }

    override val root = borderpane {
        minWidth = 600.0
        minHeight = 400.0

        top = hbox {
            paddingAll = 10.0
            spacing = 10.0
            add(generalButton)
            add(detailsButton)
            region {
                hgrow = Priority.ALWAYS
            }
            add(closeButton)
        }

        centerProperty().bind(activeViewProperty)

        centerProperty().addListener { _, _, newValue ->
            generalButton.toggleClass(ViewStyles.downloadButton, newValue == generalView)
            generalButton.toggleClass(ViewStyles.helpButton, newValue != generalView)

            detailsButton.toggleClass(ViewStyles.downloadButton, newValue == detailsView)
            detailsButton.toggleClass(ViewStyles.helpButton, newValue != detailsView)
        }
    }
}
