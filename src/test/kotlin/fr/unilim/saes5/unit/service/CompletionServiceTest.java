package fr.unilim.saes5.unit.service;

import fr.unilim.saes5.service.CompletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompletionServiceTest {

    private CompletionService completionService;

    @BeforeEach
    public void setUp() {
        this.completionService = new CompletionService();
    }

    @Test
    public void testAddCompletionWithNonNullValue() {
        this.completionService.addCompletion("hello");
        Set<String> completions = this.completionService.suggestCompletions("he");
        assertEquals(1, completions.size());
        assertTrue(completions.contains("hello"));
    }

    @Test
    public void testAddCompletionWithNullValue() {
        this.completionService.addCompletion(null);
        Set<String> completions = this.completionService.suggestCompletions("anything");
        assertTrue(completions.isEmpty());
    }

    @Test
    public void testAddCompletionWithTrimming() {
        this.completionService.addCompletion("  spaces  ");
        Set<String> completions = this.completionService.suggestCompletions("sp");
        assertEquals(1, completions.size());
        assertTrue(completions.contains("spaces"));
    }

    @Test
    public void testSuggestCompletionsWithNullInput() {
        this.completionService.addCompletion("test");
        Set<String> completions = this.completionService.suggestCompletions(null);
        assertTrue(completions.isEmpty());
    }

    @Test
    public void testSuggestCompletionsWithEmptyInput() {
        this.completionService.addCompletion("test");
        Set<String> completions = this.completionService.suggestCompletions("");
        assertEquals(1, completions.size());
    }

    @Test
    public void testSuggestCompletionsWithCaseInsensitivity() {
        this.completionService.addCompletion("CaseTest");
        Set<String> completions = this.completionService.suggestCompletions("caset");
        assertEquals(1, completions.size());
        assertTrue(completions.contains("CaseTest"));
    }

    @Test
    public void testSuggestCompletionsWithPartialMatch() {
        this.completionService.addCompletion("partial");
        Set<String> completions = this.completionService.suggestCompletions("art");
        assertEquals(1, completions.size());
        assertTrue(completions.contains("partial"));
    }

    @Test
    public void testSuggestCompletionsWithNoMatch() {
        this.completionService.addCompletion("nomatch");
        Set<String> completions = this.completionService.suggestCompletions("xyz");
        assertTrue(completions.isEmpty());
    }

    @Test
    public void testSuggestCompletionsWithMultipleMatches() {
        this.completionService.addCompletion("multi");
        this.completionService.addCompletion("multiplication");
        this.completionService.addCompletion("multiverse");
        this.completionService.addCompletion("museum");
        Set<String> completions = this.completionService.suggestCompletions("multi");
        assertEquals(3, completions.size());
        assertTrue(completions.containsAll(List.of("multi", "multiplication", "multiverse")));
    }

    @Test
    public void testSuggestCompletionsWithExactMatch() {
        this.completionService.addCompletion("exact");
        Set<String> completions = this.completionService.suggestCompletions("exact");
        assertEquals(1, completions.size());
        assertTrue(completions.contains("exact"));
    }

    @Test
    public void testAddCompletionWithDuplicateValues() {
        this.completionService.addCompletion("duplicate");
        this.completionService.addCompletion("duplicate");
        Set<String> completions = this.completionService.suggestCompletions("dup");
        assertEquals(1, completions.size());
    }

    @Test
    public void testSuggestCompletionsWithSpecialCharacters() {
        this.completionService.addCompletion("special@character!");
        Set<String> completions = this.completionService.suggestCompletions("@char");
        assertEquals(1, completions.size());
        assertTrue(completions.contains("special@character!"));
    }

    @Test
    public void testSuggestCompletionsWithNumericValues() {
        this.completionService.addCompletion("123numeric");
        Set<String> completions = this.completionService.suggestCompletions("123");
        assertEquals(1, completions.size());
        assertTrue(completions.contains("123numeric"));
    }

    @Test
    public void testSuggestCompletionsWithMixedCaseAndSpecialCharacters() {
        String mixedCaseCompletion = "Mix3dCase&Special";
        this.completionService.addCompletion(mixedCaseCompletion);
        Set<String> completions = this.completionService.suggestCompletions("mix3d");
        assertEquals(1, completions.size());
        assertTrue(completions.contains(mixedCaseCompletion));
    }

    @Test
    public void testSuggestCompletionsWithWhitespaceInput() {
        this.completionService.addCompletion("whitespace");
        Set<String> completions = this.completionService.suggestCompletions("white");
        assertEquals(1, completions.size());
        assertTrue(completions.contains("whitespace"));
    }

    @Test
    public void testSuggestCompletionsWithNoInputAndMultipleCompletions() {
        this.completionService.addCompletion("first");
        this.completionService.addCompletion("second");
        Set<String> completions = this.completionService.suggestCompletions("");
        assertEquals(2, completions.size());
        assertTrue(completions.containsAll(List.of("first", "second")));
    }

    @Test
    public void testSuggestCompletionsWithInputNotAtStart() {
        this.completionService.addCompletion("unmatched");
        Set<String> completions = this.completionService.suggestCompletions("matched");
        assertTrue(completions.contains("unmatched"));
    }

    @Test
    public void testAddEmptyStringAsCompletion() {
        this.completionService.addCompletion("");
        Set<String> completions = this.completionService.suggestCompletions("anything");
        assertTrue(completions.isEmpty());
    }

    @Test
    public void testSuggestCompletionsWhenNoCompletionsAdded() {
        Set<String> completions = this.completionService.suggestCompletions("test");
        assertTrue(completions.isEmpty());
    }

    @Test
    public void testSuggestCompletionsWithWhitespaceOnlyInput() {
        this.completionService.addCompletion("whitespace");
        Set<String> completions = this.completionService.suggestCompletions("   ");
        assertTrue(completions.isEmpty());
    }
}
