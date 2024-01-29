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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Word> getWords() {
        return this.words;
    }
}
