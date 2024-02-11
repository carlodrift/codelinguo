package fr.unilim.codelinguo.unit.service

import fr.unilim.codelinguo.service.CompletionService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CompletionServiceTest {
    private var completionService: CompletionService? = null

    @BeforeEach
    fun setUp() {
        completionService = CompletionService()
    }

    @Test
    fun testAddCompletionWithNonNullValue() {
        completionService!!.addCompletion("hello")
        val completions = completionService!!.suggestCompletions("he")
        Assertions.assertEquals(1, completions.size)
        Assertions.assertTrue(completions.contains("hello"))
    }

    @Test
    fun testAddCompletionWithNullValue() {
        completionService!!.addCompletion(null)
        val completions = completionService!!.suggestCompletions("anything")
        Assertions.assertTrue(completions.isEmpty())
    }

    @Test
    fun testAddCompletionWithTrimming() {
        completionService!!.addCompletion("  spaces  ")
        val completions = completionService!!.suggestCompletions("sp")
        Assertions.assertEquals(1, completions.size)
        Assertions.assertTrue(completions.contains("spaces"))
    }

    @Test
    fun testSuggestCompletionsWithNullInput() {
        completionService!!.addCompletion("test")
        val completions = completionService!!.suggestCompletions(null)
        Assertions.assertTrue(completions.isEmpty())
    }

    @Test
    fun testSuggestCompletionsWithEmptyInput() {
        completionService!!.addCompletion("test")
        val completions = completionService!!.suggestCompletions("")
        Assertions.assertEquals(1, completions.size)
    }

    @Test
    fun testSuggestCompletionsWithCaseInsensitivity() {
        completionService!!.addCompletion("CaseTest")
        val completions = completionService!!.suggestCompletions("caset")
        Assertions.assertEquals(1, completions.size)
        Assertions.assertTrue(completions.contains("CaseTest"))
    }

    @Test
    fun testSuggestCompletionsWithPartialMatch() {
        completionService!!.addCompletion("partial")
        val completions = completionService!!.suggestCompletions("art")
        Assertions.assertEquals(1, completions.size)
        Assertions.assertTrue(completions.contains("partial"))
    }

    @Test
    fun testSuggestCompletionsWithNoMatch() {
        completionService!!.addCompletion("nomatch")
        val completions = completionService!!.suggestCompletions("xyz")
        Assertions.assertTrue(completions.isEmpty())
    }

    @Test
    fun testSuggestCompletionsWithMultipleMatches() {
        completionService!!.addCompletion("multi")
        completionService!!.addCompletion("division et multiplication")
        completionService!!.addCompletion("le multiverse")
        completionService!!.addCompletion("musée")
        val completions = completionService!!.suggestCompletions("multi")
        Assertions.assertEquals(3, completions.size)
        Assertions.assertTrue(completions.containsAll(listOf("multi", "division et multiplication", "le multiverse")))
    }

    @Test
    fun testSuggestCompletionsWithExactMatch() {
        completionService!!.addCompletion("exact")
        val completions = completionService!!.suggestCompletions("exact")
        Assertions.assertEquals(1, completions.size)
        Assertions.assertTrue(completions.contains("exact"))
    }

    @Test
    fun testAddCompletionWithDuplicateValues() {
        completionService!!.addCompletion("duplicate")
        completionService!!.addCompletion("duplicate")
        val completions = completionService!!.suggestCompletions("dup")
        Assertions.assertEquals(1, completions.size)
    }

    @Test
    fun testSuggestCompletionsWithSpecialCharacters() {
        completionService!!.addCompletion("special@character!")
        val completions = completionService!!.suggestCompletions("@char")
        Assertions.assertEquals(1, completions.size)
        Assertions.assertTrue(completions.contains("special@character!"))
    }

    @Test
    fun testSuggestCompletionsWithNumericValues() {
        completionService!!.addCompletion("123numeric")
        val completions = completionService!!.suggestCompletions("123")
        Assertions.assertEquals(1, completions.size)
        Assertions.assertTrue(completions.contains("123numeric"))
    }

    @Test
    fun testSuggestCompletionsWithMixedCaseAndSpecialCharacters() {
        val mixedCaseCompletion = "Mix3dCase&Special"
        completionService!!.addCompletion(mixedCaseCompletion)
        val completions = completionService!!.suggestCompletions("mix3d")
        Assertions.assertEquals(1, completions.size)
        Assertions.assertTrue(completions.contains(mixedCaseCompletion))
    }

    @Test
    fun testSuggestCompletionsWithWhitespaceInput() {
        completionService!!.addCompletion("whitespace  ")
        val completions = completionService!!.suggestCompletions("white")
        Assertions.assertEquals(1, completions.size)
        Assertions.assertTrue(completions.contains("whitespace"))
    }

    @Test
    fun testSuggestCompletionsWithNoInputAndMultipleCompletions() {
        completionService!!.addCompletion("first")
        completionService!!.addCompletion("second")
        val completions = completionService!!.suggestCompletions("")
        Assertions.assertEquals(2, completions.size)
        Assertions.assertTrue(completions.containsAll(listOf("first", "second")))
    }

    @Test
    fun testSuggestCompletionsWithInputNotAtStart() {
        completionService!!.addCompletion("unmatched")
        val completions = completionService!!.suggestCompletions("matched")
        Assertions.assertTrue(completions.contains("unmatched"))
    }

    @Test
    fun testAddEmptyStringAsCompletion() {
        completionService!!.addCompletion("")
        val completions = completionService!!.suggestCompletions("anything")
        Assertions.assertTrue(completions.isEmpty())
    }

    @Test
    fun testSuggestCompletionsWhenNoCompletionsAdded() {
        val completions = completionService!!.suggestCompletions("test")
        Assertions.assertTrue(completions.isEmpty())
    }

    @Test
    fun testSuggestCompletionsWithWhitespaceOnlyInput() {
        completionService!!.addCompletion("whitespace")
        val completions = completionService!!.suggestCompletions("   ")
        Assertions.assertTrue(completions.isEmpty())
    }
}
