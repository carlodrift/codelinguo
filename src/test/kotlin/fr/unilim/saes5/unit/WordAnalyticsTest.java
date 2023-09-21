package fr.unilim.saes5.unit;

import fr.unilim.saes5.model.Word;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.*;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;

public class WordAnalyticsTest {

    private static WordAnalytics wordAnalytics;

    @BeforeEach
    void init() {
        analytics = new WordAnalytics();
    }



    @Test
    void testWordPresent() {
        List<Word> words = new ArrayList<Word>();
        words.add(new Word("Jeu"));
        words.add(new Word("Plateau"));
        words.add(new Word("Grille"));

        assertThat(wordAnalytics.isWordPresent(words)).isEqualTo(true);
    }

    @Test
    void testWordRatio() {
        List<Word> words = new ArrayList<Word>();
        words.add(new Word("Jeu"));
        words.add(new Word("Plateau"));
        words.add(new Word("Grille"));
        // TODO finir le test
        assertThat(wordAnalytics.wordRatio(new Word("Robot"), )).isEqualTo(100.00f); // tester avec une liste de mot qui contient juste Robot
    }
}
