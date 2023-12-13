package fr.unilim.saes5.view

import javafx.collections.FXCollections
import tornadofx.*
import java.util.*

class WordOccurrenceView(private val wordRank: Map<String, Int>, private val myBundle: ResourceBundle) : Fragment() {
    override val root = vbox {
        val wordRankList = FXCollections.observableArrayList(wordRank.entries.toList())
        tableview(wordRankList) {
            readonlyColumn(myBundle.getString("wordoccurrenceview_word"), Map.Entry<String, Int>::key)
            readonlyColumn(myBundle.getString("wordoccurrenceview_occurrences"), Map.Entry<String, Int>::value)
            columnResizePolicy = SmartResize.POLICY
        }
    }
}
