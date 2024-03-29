package fr.unilim.codelinguo.common.service.export.wordrank;

import fr.unilim.codelinguo.common.model.Word;
import fr.unilim.codelinguo.common.persistence.lang.JsonLangDao;
import fr.unilim.codelinguo.common.persistence.lang.LangDAO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class CSVWordRankExportService implements WordRankExportService {

    @Override
    public String save(String directory, Map<Word, Integer> wordRank, float glossaryRatio, String projectName, String fileName) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String score = String.format(Locale.US, "%.2f", glossaryRatio * 100);

        File file;
        if (projectName.equals(fileName)) {
            file = new File(dir, fileName + "_wordRank_" + score + ".csv");
        } else {
            file = new File(dir, projectName + "_" + fileName + "_wordRank_" + score + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            LangDAO lang = new JsonLangDao();
            writer.write(lang.getMessage("wordoccurrenceview_word") + "," + lang.getMessage("wordoccurrenceview_occurrences"));
            writer.newLine();

            for (Map.Entry<Word, Integer> entry : wordRank.entrySet()) {
                String line = entry.getKey().getToken() + "," + entry.getValue();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
}
