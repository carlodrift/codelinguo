package fr.unilim.codelinguo.persistence.word_rank;

import fr.unilim.codelinguo.model.Word;

import java.util.Map;

public interface WordRankDAO {

    void save(String directory, Map<Word, Integer> wordRank);

}
