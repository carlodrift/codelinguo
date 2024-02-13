package fr.unilim.saes5.service
import com.lowagie.text.*
import com.lowagie.text.pdf.*
import fr.unilim.codelinguo.model.Word
import java.awt.Color
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class PdfExportService : PdfPageEventHelper() {

    private val footerFont = Font(Font.HELVETICA, 8f, Font.ITALIC, Color.GRAY)

    override fun onEndPage(writer: PdfWriter, document: Document) {
        val footer = Phrase("Page ${document.pageNumber}", footerFont)
        ColumnText.showTextAligned(writer.directContent, Element.ALIGN_CENTER, footer,
            (document.pageSize.right - document.pageSize.left) / 2 + document.pageSize.left,
            document.pageSize.bottom + 10, 0f)
    }

    fun createCodeAnalysisReport(fileName: String, projectName : String, wordRank : Map<Word, Int>) {
        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, FileOutputStream(fileName))
        writer.pageEvent = this
        document.open()

        val baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED)
        val titleFont = Font(baseFont, 14f, Font.BOLD, Color.BLACK)
        val coloredFont = Font(baseFont, 20f, Font.BOLD, Color.DARK_GRAY)

        val logo = Image.getInstance("src/main/resources/logo/logo.png")
        logo.scaleToFit(140f, 120f)
        logo.alignment = Element.ALIGN_CENTER
        document.add(logo)

        document.add(Paragraph("Rapport d'analyse de CodeLinguo", coloredFont).apply {
            alignment = Element.ALIGN_CENTER
            spacingAfter = 20f
        })

        document.add(Paragraph("Projet: $projectName", titleFont).apply {
            alignment = Element.ALIGN_CENTER
            spacingAfter = 20f
        })

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val date = LocalDate.now().format(formatter)
        document.add(Paragraph("Généré le $date", titleFont).apply {
            alignment = Element.ALIGN_CENTER
            spacingAfter = 30f
        })
        document.newPage()

        document.add(Paragraph("Résumé de l'analyse", titleFont).apply {
            spacingBefore = 20f
            spacingAfter = 10f
        })
        document.add(Paragraph("Ce document présente un résumé des résultats de l'analyse de code effectuée par CodeLinguo. Les détails des problèmes détectés, ainsi que les recommandations, sont présentés dans les sections suivantes.", Font(baseFont, 12f)).apply {
            spacingAfter = 20f
        })

        document.add(Paragraph("Fréquence des termes", titleFont).apply {
            spacingBefore = 20f
            spacingAfter = 10f
        })

        // Create a table with 2 columns
        val table = PdfPTable(2) // 2 columns.
        table.setWidthPercentage(100f) // Width 100%

        // Optional: Set the relative widths of each column
        table.setWidths(floatArrayOf(2f, 1f)) // Example: 2 parts for the token column, 1 part for the value column

        // Add table header
        table.addCell("Terme")
        table.addCell("Fréquence")

        // Populate the table with data from wordRank
        wordRank.forEach { (key, value) ->
            table.addCell(key.token) // Assuming key is of a type that has a 'token' attribute
            table.addCell(value.toString()) // Convert value to string if not already
        }

        document.add(table)


        // Add more sections as needed...

        document.close()
    }
}