package fr.unilim.saes5.service
import OpenAIAPIService
import com.lowagie.text.*
import com.lowagie.text.pdf.*
import fr.unilim.codelinguo.model.Word
import java.awt.Color
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

class PdfExportService : PdfPageEventHelper() {

    private val footerFont = Font(Font.HELVETICA, 8f, Font.ITALIC, Color.GRAY)
    private val baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED)
    private val titleFont = Font(baseFont, 14f, Font.BOLD, Color.BLACK)
    private val coloredFont = Font(baseFont, 20f, Font.BOLD, Color.DARK_GRAY)

    override fun onEndPage(writer: PdfWriter, document: Document) {
        val footer = Phrase("Page ${document.pageNumber}", footerFont)
        ColumnText.showTextAligned(writer.directContent, Element.ALIGN_CENTER, footer,
            (document.pageSize.right - document.pageSize.left) / 2 + document.pageSize.left,
            document.pageSize.bottom + 10, 0f)
    }

    fun createCodeAnalysisReport(fileName: String, projectName : String, wordRank : Map<Word, Int>, glossaryRatio : Float) {
        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, FileOutputStream(fileName))
        writer.pageEvent = this
        document.open()

        addHeader(document, projectName)
        addParagraphWithSpacing(document, "Résumé de l'analyse", titleFont, "Ce document présente un résumé des résultats de l'analyse de code effectuée par CodeLinguo. Les détails des problèmes détectés, ainsi que les recommandations, sont présentés dans les sections suivantes.")
        addGlobalAnalysis(document, glossaryRatio, wordRank)
        addChatGPTSummary(document, wordRank)
        addTermsFrequency(document, wordRank)

        document.close()
    }

    private fun convertToStringRepresentation(wordRank: Map<Word, Int>, ): MutableList<String>? {
        return wordRank.entries.stream()
            .map { entry: Map.Entry<Word, Int> -> entry.key.token + "," + entry.value }
            .collect(Collectors.toList())
    }
    private fun addChatGPTSummary(document: Document, wordRank: Map<Word, Int>) {
        val service = OpenAIAPIService();

        val prompt = "En tant qu'expert en développement de logiciels, avec une spécialisation en assurance de la qualité du code, votre mission consiste à effectuer une analyse sur des données fournies par une application qui compare un glossaire contenant le contexte métier souhaité pour l'application et les termes métiers réellement trouvé dans l'application. Les données contiennent quels termes ont été trouvés, dans quels fichiers et en quelle quantité. Ton but à toi sera d'effectuer une analyse critique sur ces données fournies, des statistiques qui pourraient intéresser l'utilisateur afin d'améliorer la qualité de son code"
        val data = convertToStringRepresentation(wordRank)?.joinToString("\n")
        val response = data?.let { service.sendRequest(prompt, it) }
        addParagraphWithSpacing(document, "Analyse de ChatGPT", titleFont, response, alignment = Element.ALIGN_CENTER, spacingAfter = 20f)

    }

    private fun addTermsFrequency(document: Document, wordRank: Map<Word, Int>) {
        addParagraphWithSpacing(document, "Fréquence des termes (occurence < 2 ignoré)", titleFont, null, 20f, 10f)

        val table = PdfPTable(2).apply {
            widthPercentage = 100f
            setWidths(floatArrayOf(2f, 1f))
            addCell("Terme")
            addCell("Fréquence")
            wordRank.forEach { (key, value) ->
                if (value > 1) {
                    addCell(key.token)
                    addCell(value.toString())
                }
            }
        }

        document.add(table)
    }

    private fun addGlobalAnalysis(document: Document, glossaryRatio: Float, wordRank: Map<Word, Int>) {
        val fGlossaryRatioChunk = Chunk(String.format("%.2f%%", glossaryRatio), Font(baseFont, 12f, Font.BOLD))
        val totalWordCountChunk = Chunk(wordRank.values.count().toString(), Font(baseFont, 12f, Font.BOLD))
        val totalFileCountChunk = Chunk(wordRank.keys.map { it.fileName }.toSet().size.toString(), Font(baseFont, 12f, Font.BOLD))

        val paragraph = Paragraph().apply {
            add("Le glossaire que vous avez utilisé est respecté à ")
            add(fGlossaryRatioChunk)
            add(". Votre code comporte ")
            add(totalWordCountChunk)
            add(" termes différents et un total de ")
            add(totalFileCountChunk)
            add(" fichiers.")
            font = Font(baseFont, 12f) // Set the general font for the paragraph
            spacingBefore = 20f
            spacingAfter = 20f
        }

        document.add(paragraph)
        document.newPage()
    }

    private fun addHeader(document: Document, projectName: String) {
        val logo = Image.getInstance("src/main/resources/logo/logo.png").apply {
            scaleToFit(140f, 120f)
            alignment = Element.ALIGN_CENTER
        }
        document.add(logo)

        addParagraphWithSpacing(document, "Rapport d'analyse de CodeLinguo", coloredFont, null, alignment = Element.ALIGN_CENTER, spacingAfter = 20f)
        addParagraphWithSpacing(document, "Projet : $projectName", titleFont, null, alignment = Element.ALIGN_CENTER, spacingAfter = 20f)

        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        addParagraphWithSpacing(document, "Généré le $date", titleFont, null, alignment = Element.ALIGN_CENTER, spacingAfter = 30f)

        document.newPage()
    }

    private fun addParagraphWithSpacing(document: Document, title: String, font: Font, text: String? = null, spacingBefore: Float = 0f, spacingAfter: Float = 0f, alignment: Int = Element.ALIGN_LEFT) {
        val paragraph = Paragraph(title, font).apply {
            this.spacingBefore = spacingBefore
            this.spacingAfter = spacingAfter
            this.alignment = alignment
        }
        document.add(paragraph)

        text?.let {
            val paragraphText = Paragraph(it, Font(baseFont, 12f)).apply {
                this.spacingAfter = spacingAfter
            }
            document.add(paragraphText)
        }
    }
}
