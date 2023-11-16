package fr.unilim.saes5.unit

import fr.unilim.saes5.model.interfaces.IRead
import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.WordAnalytics
import fr.unilim.saes5.model.reader.DummyReader
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class WordAnalyticsTest {
    @BeforeEach
    fun init() {
        wordAnalytics = WordAnalytics()
    }

    @Test
    fun testWordPresent() {
        val words: MutableList<Word?> = ArrayList()
        words.add(Word("Jeu"))
        words.add(Word("Plateau"))
        words.add(Word("Grille"))
        for (word in words) {
            Assertions.assertThat(wordAnalytics!!.isWordPresent(words, word)).isTrue()
        }
    }

    @Test
    fun testWordRatio() {
        val reader: IRead = DummyReader(listOf(Word("Robot"), Word("Joueur")))
        Assertions.assertThat(wordAnalytics!!.wordRatio(Word("Robot"), reader.read("")))
            .isEqualTo(50.00f) // tester avec une liste de mot qui contient juste Robot
    }

    @Test
    fun testWordRank() {
        val reader: IRead = DummyReader(Arrays.asList(Word("Robot"), Word("Robot"), Word("Joueur")))
        val expected = HashMap<Word, Int>()
        expected[Word("Robot")] = 2
        expected[Word("Joueur")] = 1
        Assertions.assertThat(wordAnalytics!!.wordRank(reader.read(""))).isEqualTo(expected)
    }

    companion object {
        private var wordAnalytics: WordAnalytics? = null
    }
}
