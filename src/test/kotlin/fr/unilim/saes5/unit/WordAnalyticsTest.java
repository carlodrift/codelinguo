package fr.unilim.saes5.unit;

import fr.unilim.saes5.interfaces.IAnalyzable;
import fr.unilim.saes5.model.Word;
import fr.unilim.saes5.model.reader.TestReader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.*;

import static org.assertj.core.api.AbstractAssert.throwUnsupportedExceptionOnEquals;
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
        IAnalyzable reader = new TestReader(Arrays.asList(new Word("Robot"), new Word("Joueur")));
        assertThat(wordAnalytics.wordRatio(new Word("Robot"), reader.read())).isEqualTo(50.00f); // tester avec une liste de mot qui contient juste Robot
    }

    @Test
    void testWordRank() {
        IAnalyzable reader = new TestReader(Arrays.asList(new Word("Robot"), new Word("Robot"), new Word("Joueur")));
        HashMap<Word, Integer> expected = new HashMap<Word, Integer>();
        expected.put(new Word("Robot"), 2);
        expected.put(new Word("Joueur"), 1);
        
        assertThat(wordAnalytics.wordRank(reader.read())).isEqualTo(expected);
    }
}
