package fr.unilim.saes5.model.reader;

import fr.unilim.saes5.interfaces.IAnalyzable;
import fr.unilim.saes5.model.Word;

import java.util.*;

public class TestReader implements IAnalyzable {

    List<Word> words;

    public TestReader(List<Word> words) {
        this.words = words;
    }
    @Override
    public List<Word> read() {
        return this.words;
    }
}
