package fr.unilim.codelinguo.common.service

import fr.unilim.codelinguo.common.model.Glossary
import fr.unilim.codelinguo.common.model.Word
import java.text.Normalizer
import java.util.regex.Pattern

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
        if (words.isNullOrEmpty()) return emptyMap()

        val wordDetails = mutableMapOf<String, Pair<MutableSet<String>, Int>>()
        words.forEach { word ->
            val token = word.token ?: ""
            val fileName = word.fileName ?: "Unknown"
            wordDetails[token] = wordDetails[token]?.let {
                it.first.add(fileName)
                it.first to it.second + 1
            } ?: (mutableSetOf(fileName) to 1)
        }

        val rankedWords = wordDetails.map { (token, details) ->
            Word(token).apply {
                fileName = details.first.joinToString("\n")
            } to details.second
        }.sortedByDescending { it.second }

        return rankedWords.associate { it.first to it.second }
    }

    fun rawWordRank(words: List<Word>?): Map<Word, Int> {
        val wordCount: MutableMap<Word, Int> = HashMap()
        if (words != null) {
            for (word in words) {
                wordCount[word] = wordCount.getOrDefault(word, 0) + 1
            }
        }
        return wordCount.entries.sortedByDescending { it.value }.associate { it.toPair() }
    }

    fun filesList(wordRank: Map<Word, Int>): Set<String> {
        return wordRank.flatMap { it.key.fileName?.split("\n").orEmpty() }.toHashSet()
    }

    fun glossaryRatio(words: List<Word?>, glossary: Glossary): Float {
        if (words.isEmpty()) {
            return 0.0f
        }
        val glossaryWords = glossary.words.map { it.token }.toSet()
        val count = words.count { it?.token in glossaryWords }
        return count.toFloat() / words.size
    }

    fun glossaryCoverageRatio(words: List<Word?>, glossary: Glossary): Float {
        if (glossary.words.isEmpty()) {
            return 0.0f
        }
        val normalizedFoundWords = words.mapNotNull { it?.token?.let(this::normalizeString) }.toSet()
        val count = glossary.words.count { it.token?.let(this::normalizeString) in normalizedFoundWords }
        return count.toFloat() / glossary.words.size
    }

    private fun normalizeString(input: String): String {
        val normalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalizedString).replaceAll("")
    }

    fun wordsInGlossaryNotInList(words: List<Word?>, glossary: Glossary): List<Word> {
        val wordTokens = words.mapNotNull { it?.token?.let { token -> normalizeString(token) } }.toSet()
        return glossary.words.filter { it.token?.let { it1 -> normalizeString(it1) } !in wordTokens }
    }

    fun wordsInListNotInGlossary(words: List<Word?>, glossary: Glossary): List<Word> {
        val glossaryWordTokens = glossary.words.map { it.token?.let { it1 -> normalizeString(it1) } }.toSet()
        return words.filterNotNull().filter { it.token?.let { it1 -> normalizeString(it1) } !in glossaryWordTokens }
    }
}
