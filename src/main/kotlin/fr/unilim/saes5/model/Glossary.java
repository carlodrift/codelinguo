package fr.unilim.saes5.model;

import java.util.List;

public class Glossary {

    private final List<Word> words;

    public Glossary(List<Word> words) {
        this.words = words;
    }

    public Glossary() {
        this.words = List.of();
    }

    public void addWord(Word word) {
        this.words.add(word);
    }

    public void removeWord(Word word) {
        this.words.remove(word);
    }

    public List<Word> getWords() {
        return this.words;
    }
}
