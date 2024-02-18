package fr.unilim.codelinguo.common.service.export.report

import fr.unilim.codelinguo.common.model.Word

interface ReportExportService {
    fun createCodeAnalysisReport(
        projectName: String,
        wordRank: Map<Word, Int>,
        glossaryRatio: Float,
        fileName: String,
        directory: String
    ): String
}