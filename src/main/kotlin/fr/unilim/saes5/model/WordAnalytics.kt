package fr.unilim.saes5.model

class WordAnalytics {
    fun isWordPresent(words: List<Word?>?, word: Word?): Boolean {
        return words!!.contains(word);
    }

    fun wordRatio(word: Word?, words: List<Word>?): Float {
        if (words.isNullOrEmpty()) {
            return 0.0f
        }
        val count = words.stream().filter { w: Word -> w.equals(word) }.count()
        return count.toFloat() / words.size * 100
    }

    fun wordRank(words: List<Word>?): Map<Word, Int> {
        val wordCount: MutableMap<Word, Int> = HashMap()
        if (words != null) {
            for (word in words) {
                wordCount[word] = wordCount.getOrDefault(word, 0) + 1
            }
        }
        return wordCount
    }
}
