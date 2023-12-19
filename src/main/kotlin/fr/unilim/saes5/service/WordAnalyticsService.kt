package fr.unilim.saes5.service

import fr.unilim.saes5.model.Glossary
import fr.unilim.saes5.model.Word

class WordAnalyticsService {
    fun isWordPresent(words: List<Word?>?, word: Word?): Boolean {
        return words!!.contains(word)
    }

    fun wordRatio(word: Word?, words: List<Word>?): Float {
        if (words.isNullOrEmpty()) {
            return 0.0f
        }
        val count = words.stream().filter { w: Word -> w == word }.count()
        return count.toFloat() / words.size * 100
    }

    fun wordRank(words: List<Word>?): Map<Word, Int> {
        val wordCount: MutableMap<Word, Int> = HashMap()
        if (words != null) {
            for (word in words) {
                wordCount[word] = wordCount.getOrDefault(word, 0) + 1
            }
        }
        return wordCount.entries.sortedByDescending { it.value }.associate { it.toPair() }
    }

    fun glossaryRatio(words: List<Word?>, glossary: Glossary): Float {
        if (words.isNullOrEmpty()) {
            return 0.0f
        }
        val glossaryWords = glossary.words.map { it.token }.toSet()
        val count = words.count { it?.token in glossaryWords }
        return count.toFloat() / words.size
    }

}
