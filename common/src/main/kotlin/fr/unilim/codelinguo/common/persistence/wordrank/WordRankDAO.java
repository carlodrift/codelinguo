package fr.unilim.codelinguo.common.persistence.wordrank;

import fr.unilim.codelinguo.common.model.Word;

import java.util.Map;

public interface WordRankDAO {

    void save(String directory, Map<Word, Integer> wordRank, float glossaryRatio, String projectName, String fileName);

}
