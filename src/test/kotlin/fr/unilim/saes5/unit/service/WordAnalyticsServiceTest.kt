package fr.unilim.saes5.unit.service

import fr.unilim.saes5.model.Glossary
import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.reader.IRead
import fr.unilim.saes5.model.reader.DummyReader
import fr.unilim.saes5.service.WordAnalyticsService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class WordAnalyticsServiceTest {
    @BeforeEach
    fun init() {
        wordAnalyticsService = WordAnalyticsService()
    }

    private val service = WordAnalyticsService()

    @Test
    fun testWordsInGlossaryNotInList() {
        val words = listOf(Word("word1"), Word("word2"))
        val glossary = Glossary(listOf(Word("word1"), Word("word3")))

        val result = service.wordsInGlossaryNotInList(words, glossary)

        assertEquals(1, result.size)
        assertEquals("word3", result[0].token)
    }

    @Test
    fun testWordsInListNotInGlossary() {
        val words = listOf(Word("word1"), Word("word2"), Word("word3"))
        val glossary = Glossary(listOf(Word("word1"), Word("word2")))

        val result = service.wordsInListNotInGlossary(words, glossary)

        assertEquals(1, result.size)
        assertEquals("word3", result[0].token)
    }


    @Test
    fun testGlossaryRatio() {

        val service = WordAnalyticsService()


        val words = listOf(
            Word("word1"),
            Word("word2"),
            Word("word3"),
            Word("word4")
        )

        val glossaryWords = listOf(
            Word("word1"),
            Word("word2")
        )

        val glossary = Glossary(glossaryWords)

        val ratio = service.glossaryRatio(words, glossary)

        assertEquals(0.5f, ratio)
    }

    @Test
    fun testWordPresent() {
        val words: MutableList<Word?> = ArrayList()
        words.add(Word("Jeu"))
        words.add(Word("Plateau"))
        words.add(Word("Grille"))
        for (word in words) {
            Assertions.assertThat(wordAnalyticsService!!.isWordPresent(words, word)).isTrue()
        }
    }

    @Test
    fun testWordRatio() {
        val reader: IRead = DummyReader(listOf(Word("Robot"), Word("Joueur")))
        Assertions.assertThat(wordAnalyticsService!!.wordRatio(Word("Robot"), reader.read("")))
            .isEqualTo(50.00f) // tester avec une liste de mot qui contient juste Robot
    }

    @Test
    fun testWordRank() {
        val reader: IRead = DummyReader(Arrays.asList(Word("Robot"), Word("Robot"), Word("Joueur")))
        val expected = HashMap<Word, Int>()
        expected[Word("Robot")] = 2
        expected[Word("Joueur")] = 1
        Assertions.assertThat(wordAnalyticsService!!.wordRank(reader.read(""))).isEqualTo(expected)
    }

    companion object {
        private var wordAnalyticsService: WordAnalyticsService? = null
    }
}
