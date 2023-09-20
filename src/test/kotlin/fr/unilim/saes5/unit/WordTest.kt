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
        word.setToken("testToken")
        assertThat(word.getToken()).isEqualTo("testToken")
    }

    @Test
    fun testSynonyms() {
        val word = Word()
        word.setSynonyms(
            Word("synonym1"),
            Word("synonym2")
        )
        assertThat(word.size()).isEqualTo(2)
    }

    @Test
    fun testAntonyms() {
        val word = Word()
        word.setAntonyms(
            Word("antonym1")
        )
        assertThat(word.size()).isEqualTo(1)
    }

    @Test
    fun testRelateds() {
        val word = Word("wordwithRelations")
        word.setRelations(
            Word("relation1"),
            Word("relation2"),
            Word("relation3")
        )

        assertThat(word.getRelation().size()).isEqualTo(3)
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