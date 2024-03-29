package fr.unilim.codelinguo.common.service.export.report

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import fr.unilim.codelinguo.common.model.Word
import fr.unilim.codelinguo.common.service.OpenAIAPIService
import fr.unilim.codelinguo.common.service.WordAnalyticsService
import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

class PDFReportExportService : PdfPageEventHelper(), ReportExportService {

    private val footerFont = Font(Font.HELVETICA, 8f, Font.ITALIC, Color.GRAY)
    private val baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED)
    private val titleFont = Font(baseFont, 14f, Font.BOLD, Color.BLACK)
    private val coloredFont = Font(baseFont, 20f, Font.BOLD, Color.DARK_GRAY)

    override fun onEndPage(writer: PdfWriter, document: Document) {
        val footer = Phrase("Page ${document.pageNumber}", footerFont)
        ColumnText.showTextAligned(
            writer.directContent, Element.ALIGN_CENTER, footer,
            (document.pageSize.right - document.pageSize.left) / 2 + document.pageSize.left,
            document.pageSize.bottom + 10, 0f
        )
    }

    override fun createCodeAnalysisReport(
        projectName: String,
        wordRank: Map<Word, Int>,
        glossaryRatio: Float,
        fileName: String,
        directory: String,
        apiKey: String,
        glossaryCoverageRatio: Float
    ): String {
        val dir = File(directory)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val formattedFileName: String = if (projectName == fileName) {
            String.format(Locale.US, "%s_report_%.2f.pdf", projectName, glossaryRatio * 100)
        } else {
            String.format(Locale.US, "%s_%s_report_%.2f.pdf", projectName, fileName, glossaryRatio * 100)
        }
        val fullPath = File(dir, formattedFileName)
        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, FileOutputStream(fullPath))
        writer.pageEvent = this
        document.open()

        addHeader(document, projectName)
        addParagraphWithSpacing(
            document,
            "Résumé de l'analyse",
            titleFont,
            "Ce document présente un résumé les résultats de l'analyse de code effectuée par CodeLinguo. Les détails des problèmes détectés, ainsi que les recommandations, sont présentés dans les sections suivantes."
        )
        addGlobalAnalysis(document, glossaryRatio, wordRank, glossaryCoverageRatio)
        if (apiKey.isNotEmpty()) {
            addChatGPTSummary(document, wordRank, apiKey)
        }
        addTermsFrequency(document, wordRank)

        document.close()

        return fullPath.absolutePath
    }

    private fun addTermsFrequency(document: Document, wordRank: Map<Word, Int>) {
        addParagraphWithSpacing(document, "Termes principaux", titleFont, null, 20f, 10f)

        val table = PdfPTable(2).apply {
            widthPercentage = 100f
            setWidths(floatArrayOf(2f, 1f))

            val headerFont = Font(baseFont, 12f, Font.BOLD)

            val termHeader = PdfPCell(Phrase("Terme", headerFont)).apply {
                backgroundColor = Color.LIGHT_GRAY
                horizontalAlignment = Element.ALIGN_CENTER
                verticalAlignment = Element.ALIGN_MIDDLE
                setPadding(5f)
            }

            val occurrenceHeader = PdfPCell(Phrase("Occurrence", headerFont)).apply {
                backgroundColor = Color.LIGHT_GRAY
                horizontalAlignment = Element.ALIGN_CENTER
                verticalAlignment = Element.ALIGN_MIDDLE
                setPadding(5f)
            }

            addCell(termHeader)
            addCell(occurrenceHeader)

            wordRank.forEach { (key, value) ->
                if (value > 1) {
                    val motCell = PdfPCell(Phrase(key.token)).apply {
                        horizontalAlignment = Element.ALIGN_LEFT
                        verticalAlignment = Element.ALIGN_MIDDLE
                        paddingLeft = 5f
                        setPadding(5f)
                    }
                    addCell(motCell)

                    val occurrenceCell = PdfPCell(Phrase(value.toString())).apply {
                        horizontalAlignment = Element.ALIGN_CENTER
                        verticalAlignment = Element.ALIGN_MIDDLE
                        setPadding(5f)
                    }
                    addCell(occurrenceCell)
                }
            }
        }

        document.add(table)
    }

    private fun addGlobalAnalysis(document: Document, glossaryRatio: Float, wordRank: Map<Word, Int>, glossaryCoverageRatio: Float) {
        val fGlossaryRatioChunk = Chunk(String.format("%.2f%%", glossaryRatio * 100), Font(baseFont, 12f, Font.BOLD))
        val fGlossaryCoverageRatioChunk = Chunk(String.format("%.2f%%", glossaryCoverageRatio * 100), Font(baseFont, 12f, Font.BOLD))
        val totalWordCountChunk = Chunk(wordRank.values.count().toString(), Font(baseFont, 12f, Font.BOLD))

        val totalFileCountChunk = Chunk(
            WordAnalyticsService().filesList(wordRank).size.toString(),
            Font(baseFont, 12f, Font.BOLD)
        )

        val paragraph = Paragraph().apply {
            add(fGlossaryRatioChunk)
            add(" des termes trouvés sont aussi dans le glossaire.")
            add("Votre glossaire est couvert à ")
            add(fGlossaryCoverageRatioChunk)
            add(". Votre code comporte ")
            add(totalWordCountChunk)
            add(" termes différents et un total de ")
            add(totalFileCountChunk)
            add(" fichiers.")
            font = Font(baseFont, 12f)
            spacingBefore = 20f
            spacingAfter = 20f
        }

        document.add(paragraph)
        document.newPage()
    }

    private fun addHeader(document: Document, projectName: String) {
        val logoStream: InputStream? =
            this::class.java.classLoader.getResourceAsStream("logo" + File.separator + "logo.png")
        if (logoStream != null) {
            val logo = Image.getInstance(logoStream.readBytes()).apply {
                scaleToFit(140f, 120f)
                alignment = Element.ALIGN_CENTER
            }
            document.add(logo)
        }

        addParagraphWithSpacing(
            document,
            "Rapport d'analyse de CodeLinguo",
            coloredFont,
            null,
            alignment = Element.ALIGN_CENTER,
            spacingAfter = 20f
        )
        addParagraphWithSpacing(
            document,
            "Projet : $projectName",
            titleFont,
            null,
            alignment = Element.ALIGN_CENTER,
            spacingAfter = 20f
        )

        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        addParagraphWithSpacing(
            document,
            "Généré le $date",
            titleFont,
            null,
            alignment = Element.ALIGN_CENTER,
            spacingAfter = 30f
        )

        document.newPage()
    }

    private fun addParagraphWithSpacing(
        document: Document,
        title: String,
        font: Font,
        text: String? = null,
        spacingBefore: Float = 0f,
        spacingAfter: Float = 0f,
        alignment: Int = Element.ALIGN_LEFT,
    ) {
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

    private fun convertToStringRepresentation(wordRank: Map<Word, Int>): MutableList<String>? {
        return wordRank.entries.stream()
            .map { entry: Map.Entry<Word, Int> -> entry.key.token + "," + entry.value }
            .collect(Collectors.toList())
    }

    private fun addChatGPTSummary(document: Document, wordRank: Map<Word, Int>, apiKey: String) {
        val service = OpenAIAPIService()

        val prompt =
            "En tant qu'expert en développement de logiciels, avec une spécialisation en assurance de la qualité du code, votre mission consiste à effectuer une analyse sur des données fournies par une application qui compare un glossaire contenant le contexte métier souhaité pour l'application et les termes métiers réellement trouvé dans l'application. Les données contiennent quels termes ont été trouvés, dans quels fichiers et en quelle quantité. Ton but à toi sera d'effectuer une analyse critique sur ces données fournies, des statistiques qui pourraient intéresser l'utilisateur afin d'améliorer la qualité de son code"
        val data = convertToStringRepresentation(wordRank)?.joinToString("\n")
        var response = data?.let { service.sendRequest(prompt, it, apiKey) }
        if (response == null) {
            response = "L'analyse n'a pas pu être générée, veuillez vérifier votre clé API OpenAI."
        }
        addParagraphWithSpacing(
            document,
            "Analyse",
            titleFont,
            response,
            alignment = Element.ALIGN_CENTER,
            spacingAfter = 20f
        )

    }
}
