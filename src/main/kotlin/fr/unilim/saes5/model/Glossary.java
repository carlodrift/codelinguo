package fr.unilim.saes5.model;

import java.util.List;

public class Glossary {

    private final List<Word> words;
    private String name;

    private boolean demo;

    public void setDemo(boolean demo) {
        this.demo = demo;
    }

    public boolean isDemo() {
        return this.demo;
    }

    public Glossary(List<Word> words) {
        this.words = words;
    }

    public Glossary() {
        this.words = List.of();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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
