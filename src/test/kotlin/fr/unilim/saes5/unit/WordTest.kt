package fr.unilim.saes5.unit

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.context.PrimaryContext
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import fr.unilim.saes5.model.context.SecondaryContext
import fr.unilim.saes5.model.context.Context

class WordTest {
    @Test
    fun testToken() {
        val word = Word()
        word.token = "testToken"
        assertThat(word.token).isEqualTo("testToken")
    }

    @Test
    fun testSynonyms() {
        val word = Word()
        word.synonyms = setOf(
            Word("synonym1"),
            Word("synonym2")
        )
        assertThat(word.synonyms!!.size).isEqualTo(2)
    }

    @Test
    fun testAntonyms() {
        val word = Word()
        word.antonyms = setOf(
            Word("antonym1")
        )
        assertThat(word.antonyms!!.size).isEqualTo(1)
    }

    @Test
    fun testRelateds() {
        val word = Word("wordwithRelations")
        word.related = setOf(
            Word("relation1"),
            Word("relation2"),
            Word("relation3")
        )

        assertThat(word.related!!.size).isEqualTo(3)
    }

    @Test
    fun testContexts() {
        val word = Word()
        val primaryContext: Context = PrimaryContext(Word("testContext"))
        val secondaryContext: Context = SecondaryContext(Word("testContext"))
        assertThat(primaryContext.priority).isEqualTo(2.0f) // l'importante pour contexte principal
        assertThat(secondaryContext.priority).isEqualTo(1.0f)
    }
}