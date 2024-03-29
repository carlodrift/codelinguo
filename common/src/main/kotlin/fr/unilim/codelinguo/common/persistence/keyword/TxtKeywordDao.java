package fr.unilim.codelinguo.common.persistence.keyword;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class TxtKeywordDao implements KeywordDao {

    @Override
    public Set<String> retrieve(String language) {
        try (InputStream is = TxtKeywordDao.class.getResourceAsStream(File.separator + "keywords" + File.separator + language + ".txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return reader.lines().collect(Collectors.toSet());
        } catch (IOException | NullPointerException e) {
            return Collections.emptySet();
        }
    }
}
