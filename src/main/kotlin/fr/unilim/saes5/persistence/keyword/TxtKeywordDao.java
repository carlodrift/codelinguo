package fr.unilim.saes5.persistence.keyword;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

public class TxtKeywordDao implements KeywordDao {

    private static final String KEYWORDS_FILE_PATH = "java_reserved_keywords.txt";

    @Override
    public Set<String> loadKeywords() {
        try {
            var path = Paths.get(TxtKeywordDao.KEYWORDS_FILE_PATH);
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            return Set.copyOf(Files.readAllLines(path, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return Collections.emptySet();
        }
    }
}
