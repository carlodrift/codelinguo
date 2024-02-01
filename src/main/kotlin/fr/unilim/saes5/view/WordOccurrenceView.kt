package fr.unilim.saes5.view

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.persistence.lang.LangDAO
import fr.unilim.saes5.view.style.ViewStyles
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.chart.PieChart
import javafx.scene.control.*
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import javafx.util.Duration
import org.controlsfx.control.PopOver
import tornadofx.*
import java.util.*


class WordOccurrenceView(
    private val wordRank: Map<Word, Int>,
    wordsInListNotInGlossary: List<Word>,
    private val glossaryRatio: Float,
    private val lang: LangDAO
) : Fragment() {

    private val aggregatedWordMap = aggregateWords(wordRank.keys)

    private val clickDetailLabel = label(lang.getMessage("click_detail_label")) {
        addClass(ViewStyles.clickDetailLabel)
    }

    private fun showFileNamesWindow(word: Word, wordRank: Map<Word, Int>): Node {
        val fileOccurrencesMap = wordRank.filterKeys { it.token == word.token }
        val totalOccurrences = fileOccurrencesMap.values.sum()

        val topFileOccurrences = fileOccurrencesMap.entries
            .sortedByDescending { it.value }
            .take(9)

        val otherOccurrencesCount = fileOccurrencesMap.values.sum() - topFileOccurrences.sumOf { it.value }

        val pieChartData = topFileOccurrences.map { (word, count) ->
            val percentage = if (totalOccurrences > 0) count.toDouble() / totalOccurrences * 100 else 0.0
            val fileName = word.fileName?.substringAfterLast("\\")?.substringAfterLast("/") ?: "Inconnu"
            PieChart.Data("$fileName (${String.format("%.2f%%", percentage)})", count.toDouble())
        }.toMutableList()

        val newCloseButton = button(lang.getMessage("button_close")) {
            action {
                val popup = this@button.scene.window as PopOver
                popup.hide(Duration.millis(0.0))
            }
            style {
                fontSize = 18.px
                cursor = Cursor.HAND
                backgroundColor += c("#FFFFFF")
                backgroundRadius += box(15.px)
                borderRadius += box(15.px)
                borderWidth += box(1.px)
                borderColor += box(c("#000000"))
                fontFamily = listOf("DM Sans", "Arial", "Helvetica", "sans-serif").joinToString(",")
            }
            hoverProperty().addListener { _, _, isHovered ->
                if (isHovered) {
                    style {
                        fontSize = 18.px
                        cursor = Cursor.HAND
                        backgroundColor += c("#D7D7D7")
                        backgroundRadius += box(15.px)
                        borderRadius += box(15.px)
                        borderWidth += box(1.px)
                        borderColor += box(c("#000000"))
                        fontFamily = listOf("DM Sans", "Arial", "Helvetica", "sans-serif").joinToString(",")
                    }
                } else {
                    style {
                        fontSize = 18.px
                        cursor = Cursor.HAND
                        backgroundColor += c("#FFFFFF")
                        backgroundRadius += box(15.px)
                        borderRadius += box(15.px)
                        borderWidth += box(1.px)
                        borderColor += box(c("#000000"))
                        fontFamily = listOf("DM Sans", "Arial", "Helvetica", "sans-serif").joinToString(",")
                    }
                }
            }
        }

        if (otherOccurrencesCount > 0) {
            val otherPercentage = otherOccurrencesCount.toDouble() / totalOccurrences * 100
            pieChartData.add(
                PieChart.Data(
                    "Autre (${String.format("%.2f%%", otherPercentage)})",
                    otherOccurrencesCount.toDouble()
                )
            )
        }

        val observablePieChartData = FXCollections.observableArrayList(pieChartData)

        val pieChart = PieChart(observablePieChartData).apply {
            isClockwise = true
            labelsVisible = true
            startAngle = 180.0
        }


        val closeButtonHBox = HBox().apply {
            alignment = Pos.TOP_RIGHT
            add(newCloseButton)
            padding = Insets(10.0)
        }

        return BorderPane().apply {
            top = closeButtonHBox
            center = pieChart
            style {
                backgroundColor += c("#FFFFFF")
            }
        }
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
        readonlyColumn(lang.getMessage("wordoccurrenceview_word") + " ⇅", Map.Entry<Word, Int>::key) {
            prefWidth = 300.0
            cellFormat { wordEntry ->
                text = wordEntry.token
                cursor = Cursor.HAND
                style {
                    alignment = Pos.CENTER_LEFT
                    textAlignment = TextAlignment.LEFT
                    padding = box(0.px, 10.px, 0.px, 10.px)
                    if (!wordsInListNotInGlossary.any { word -> word.token == wordEntry.token }) {
                        backgroundColor += c("#A1F9B4")
                        text = "\u2713 $text"
                    }
                }

                val customTooltip = PopOver().apply {
                    contentNode = showFileNamesWindow(wordEntry, wordRank)
                    arrowLocation = PopOver.ArrowLocation.LEFT_TOP
                    isDetachable = false
                    isAutoHide = true
                }

                setOnMouseClicked { event ->
                    if (event.clickCount > 0) {
                        customTooltip.show(this@cellFormat, event.screenX, event.screenY)
                    }
                }
            }
        }

        readonlyColumn(lang.getMessage("wordoccurrenceview_occurrences") + " ⇅", Map.Entry<Word, Int>::value) {
            prefWidth = 100.0
            cellFormat { occurrenceEntry ->
                text = occurrenceEntry.toString()
                style {
                    if (!wordsInListNotInGlossary.any { word -> word.token == this@cellFormat.rowItem.key.token }) {
                        backgroundColor += c("#A1F9B4")
                    }
                }
            }
        }

        columnResizePolicy = CONSTRAINED_RESIZE_POLICY
        selectionModel = null
    }

    private val detailsView = vbox {
        alignment = Pos.CENTER
        spacing = 20.0
        style {
            padding = box(20.px)
        }

        text {
            val percentageText = String.format("%.2f%%", glossaryRatio * 100)
            text = percentageText
            style {
                fontSize = 50.px
                fontWeight = FontWeight.EXTRA_BOLD
                fill = c("#0078D7")
            }
        }

        label {
            text = lang.getMessage("glossary_ratio")
            style {
                fontSize = 20.px
                fontWeight = FontWeight.BOLD
            }
        }
    }

    private val closeButton = button(lang.getMessage("button_close")) {
        addClass(ViewStyles.helpButton)
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

            vbox {
                add(clickDetailLabel)
                alignment = Pos.CENTER_LEFT
            }

            region {
                hgrow = Priority.ALWAYS
            }

            add(closeButton)
        }

        center = vbox {
            add(generalView)
            add(detailsView)
        }
    }
}
