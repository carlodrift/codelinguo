package fr.unilim.codelinguo.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CompletionService {

    private final List<String> completions = new ArrayList<>();

    public void addCompletion(String completion) {
        if (completion != null) {
            this.completions.add(completion.trim());
        }
    }

    public void clearCompletions() {
        this.completions.clear();
    }

    public Set<String> suggestCompletions(String input) {
        if (input == null) {
            return Set.of();
        }

        if (input.isEmpty()) {
            return new HashSet<>(this.completions);
        }

        String inputLower = input.toLowerCase();
        return this.completions.stream()
                .filter(completion -> completion.toLowerCase().contains(inputLower))
                .collect(Collectors.toSet());
    }
}
