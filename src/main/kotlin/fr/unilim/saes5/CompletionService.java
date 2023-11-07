package fr.unilim.saes5;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CompletionService {

    private final List<String> completions = new ArrayList<>();

    public void addCompletion(String completion) {
        Objects.requireNonNull(completion, "Completion cannot be null");
        this.completions.add(completion);
    }

    public List<String> suggestCompletions(String input) {
        if (input == null) {
            return List.of();
        }

        if (input.isEmpty()) {
            return new ArrayList<>(this.completions);
        }

        String inputLower = input.toLowerCase();
        return this.completions.stream()
                .filter(completion -> completion.toLowerCase().contains(inputLower))
                .collect(Collectors.toList());
    }
}
