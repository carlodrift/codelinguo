package fr.unilim.saes5.model;

import java.util.List;

public class Project {

    private final List<Word> words;

    public Project(List<Word> words) {
        this.words = words;
    }

    public Project() {
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
