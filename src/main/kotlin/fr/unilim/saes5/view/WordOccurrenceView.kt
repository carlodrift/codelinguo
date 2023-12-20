package fr.unilim.saes5.view

import fr.unilim.saes5.model.Word
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import javafx.util.Callback
import tornadofx.*
import java.util.*

class WordOccurrenceView(
    wordRank: Map<Word, Int>,
    wordsInListNotInGlossary: List<Word>,
    glossaryRatio: Float,
    private val myBundle: ResourceBundle
) : Fragment() {

    private val aggregatedWordMap = aggregateWords(wordRank.keys)

    private fun showFileNamesWindow(word: Word) {
        val fileNames = word.fileName?.split("\n") ?: listOf("Inconnu")
        val fileNamesList = FXCollections.observableArrayList(fileNames)

        val stage = Stage()
        stage.title = "${word.token}"

        val tableView = TableView<String>().apply {
            items = fileNamesList

            columnResizePolicy = CONSTRAINED_RESIZE_POLICY
            columns.clear()

            val fileNameColumn = TableColumn<String, String>("Fichiers")
            fileNameColumn.cellValueFactory = Callback { SimpleStringProperty(it.value) }
            columns.add(fileNameColumn)
        }

        val scene = Scene(VBox(tableView), 300.0, 200.0)

        stage.scene = scene
        stage.show()
    }

    private val wordRankList = FXCollections.observableArrayList<Map.Entry<Word, Int>>(
        aggregatedWordMap.map { (token, fileNames) ->
            val word = Word(token).apply { fileName = fileNames }
            val count = wordRank.filterKeys { it.token == token }.values.sum()
            object : Map.Entry<Word, Int> {
                override val key: Word = word
                override val value: Int = count
            }
        }.sortedByDescending { it.value }
    )

    private fun aggregateWords(words: Set<Word>): Map<String, String> {
        val wordToFileNames = mutableMapOf<String, MutableList<String>>()

        for (word in words) {
            wordToFileNames.getOrPut(word.token ?: "") { mutableListOf() }.add(word.fileName ?: "Unknown")
        }

        return wordToFileNames.mapValues { (_, fileNames) -> fileNames.distinct().joinToString("\n") }
    }


    init {
        this.whenDocked {
            currentStage?.isResizable = false
        }
    }

    private val generalView = tableview(wordRankList) {
        addClass(ViewStyles.customTableView)
        readonlyColumn(myBundle.getString("wordoccurrenceview_word"), Map.Entry<Word, Int>::key) {
            prefWidth = 300.0
            cellFormat { wordEntry ->
                text = wordEntry.token
                style {
                    alignment = Pos.CENTER_LEFT
                    textAlignment = TextAlignment.LEFT
                    padding = box(0.px, 10.px, 0.px, 10.px)
                    if (!wordsInListNotInGlossary.any { word -> word.token == wordEntry.token }) {
                        textFill = c("green")
                    }
                }
                setOnMouseClicked {
                    if (it.clickCount == 2) {
                        showFileNamesWindow(wordEntry)
                    }
                }
            }
        }

        readonlyColumn(myBundle.getString("wordoccurrenceview_occurrences"), Map.Entry<Word, Int>::value) {
            prefWidth = 100.0
        }

        columnResizePolicy = CONSTRAINED_RESIZE_POLICY
    }

    private val detailsView = vbox {
        label("La terminologie de ce projet est respectée à " + String.format("%.2f", glossaryRatio * 100) + "%.")
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
        addClass(ViewStyles.downloadButtonHover)
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
