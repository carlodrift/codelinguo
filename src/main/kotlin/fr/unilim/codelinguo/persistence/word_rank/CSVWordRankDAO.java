package fr.unilim.codelinguo.persistence.word_rank;

import fr.unilim.codelinguo.model.Word;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CSVWordRankDAO implements WordRankDAO {

    @Override
    public void save(String directory, Map<Word, Integer> wordRank) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "word_rank.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Mot,Occurrences");
            writer.newLine();

            for (Map.Entry<Word, Integer> entry : wordRank.entrySet()) {
                String line = entry.getKey().getToken() + "," + entry.getValue();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
