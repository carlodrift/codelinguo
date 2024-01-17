package fr.unilim.saes5.persistence.keyword;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class TxtKeywordDao implements KeywordDao {

    private static final String KEYWORDS_FILE_PATH = "/java_reserved_keywords.txt";

    @Override
    public Set<String> loadKeywords() {
        try (InputStream is = TxtKeywordDao.class.getResourceAsStream(TxtKeywordDao.KEYWORDS_FILE_PATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return reader.lines().collect(Collectors.toSet());
        } catch (IOException | NullPointerException e) {
            return Collections.emptySet();
        }
    }
}
