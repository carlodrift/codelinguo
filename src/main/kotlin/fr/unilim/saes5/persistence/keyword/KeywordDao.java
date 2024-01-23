package fr.unilim.saes5.persistence.keyword;

import java.util.Set;

public interface KeywordDao {

    String KEYWORDS_FILE_PATH = "/java_reserved_keywords.txt";

    Set<String> retrieve();
}
