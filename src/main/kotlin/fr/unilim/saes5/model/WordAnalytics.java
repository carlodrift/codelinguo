package fr.unilim.saes5.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordAnalytics {

    public boolean isWordPresent(List<Word> words) {
        return words != null && !words.isEmpty();
    }

    public float wordRatio(Word word, List<Word> words) {
        if (words == null || words.isEmpty()) {
            return 0.0f;
        }

        long count = words.stream().filter(w -> w.equals(word)).count();
        return ((float) count / words.size()) * 100;
    }

    public Map<Word, Integer> wordRank(List<Word> words) {
        Map<Word, Integer> wordCount = new HashMap<>();

        if (words != null) {
            for (Word word : words) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        return wordCount;
    }
}
