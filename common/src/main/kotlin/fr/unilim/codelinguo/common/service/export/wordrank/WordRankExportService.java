package fr.unilim.codelinguo.common.service.export.wordrank;

import fr.unilim.codelinguo.common.model.Word;

import java.util.Map;

public interface WordRankExportService {

    String save(String directory, Map<Word, Integer> wordRank, float glossaryRatio, String projectName, String fileName);
}
