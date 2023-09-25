package fr.unilim.saes5.unit;

import fr.unilim.saes5.interfaces.IAnalyzable;
import fr.unilim.saes5.model.Word;
import fr.unilim.saes5.model.WordAnalytics;
import fr.unilim.saes5.model.reader.TestReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WordAnalyticsTest {

    private static WordAnalytics wordAnalytics;

    @BeforeEach
    void init() {
        WordAnalyticsTest.wordAnalytics = new WordAnalytics();
    }


    @Test
    void testWordPresent() {
        List<Word> words = new ArrayList<Word>();
        words.add(new Word("Jeu"));
        words.add(new Word("Plateau"));
        words.add(new Word("Grille"));

        assertThat(WordAnalyticsTest.wordAnalytics.isWordPresent(words)).isTrue();
    }

    @Test
    void testWordRatio() {
        IAnalyzable reader = new TestReader(Arrays.asList(new Word("Robot"), new Word("Joueur")));
        assertThat(WordAnalyticsTest.wordAnalytics.wordRatio(new Word("Robot"), reader.read())).isEqualTo(50.00f); // tester avec une liste de mot qui contient juste Robot
    }

    @Test
    void testWordRank() {
        IAnalyzable reader = new TestReader(Arrays.asList(new Word("Robot"), new Word("Robot"), new Word("Joueur")));
        HashMap<Word, Integer> expected = new HashMap<Word, Integer>();
        expected.put(new Word("Robot"), 2);
        expected.put(new Word("Joueur"), 1);

        assertThat(WordAnalyticsTest.wordAnalytics.wordRank(reader.read())).isEqualTo(expected);
    }
}
