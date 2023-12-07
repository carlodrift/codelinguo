package fr.unilim.saes5.view

import javafx.collections.FXCollections
import tornadofx.*

class WordOccurrenceView(private val wordRank: Map<String, Int>) : Fragment() {
    override val root = vbox {
        val wordRankList = FXCollections.observableArrayList(wordRank.entries.toList())
        tableview(wordRankList) {
            readonlyColumn("Mot", Map.Entry<String, Int>::key)
            readonlyColumn("Occurrences", Map.Entry<String, Int>::value)
            columnResizePolicy = SmartResize.POLICY
        }
    }
}
