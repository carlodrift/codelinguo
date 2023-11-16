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
}
