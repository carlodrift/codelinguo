package fr.unilim.saes5.unit;

import fr.unilim.saes5.CompletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompletionServiceTest {

    private CompletionService completionService;

    @BeforeEach
    void setUp() {
        this.completionService = new CompletionService();
        this.completionService.addCompletion("Pialleport");
        this.completionService.addCompletion("Transport");
        this.completionService.addCompletion("Airport");
    }

    @Test
    void whenInputIsPORT_thenSuggestPialleport() {
        String input = "PORT";

        List<String> completions = this.completionService.suggestCompletions(input);

        assertThat(completions)
                .isNotNull()
                .anySatisfy(completion -> assertThat(completion).isEqualToIgnoringCase("Pialleport"));
    }

    @Test
    void whenInputIsPORT_thenSuggestCaseInsensitiveCompletions() {
        String input = "PORT";

        List<String> completions = this.completionService.suggestCompletions(input);

        assertThat(completions)
                .isNotNull()
                .filteredOn(completion -> "Pialleport".equalsIgnoreCase(completion) || "Airport".equalsIgnoreCase(completion))
                .hasSize(2);
    }

    @Test
    void whenInputIsEmpty_thenSuggestAllCompletions() {
        String input = "";

        List<String> completions = this.completionService.suggestCompletions(input);

        assertThat(completions)
                .isNotNull()
                .containsExactlyInAnyOrder("Pialleport", "Transport", "Airport");
    }

    @Test
    void whenInputIsNull_thenSuggestNoCompletions() {
        String input = null;

        List<String> completions = this.completionService.suggestCompletions(input);

        assertThat(completions).isEmpty();
    }
}
