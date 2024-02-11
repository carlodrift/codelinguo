package fr.unilim.codelinguo.persistence.keyword;

import java.util.Set;

public interface KeywordDao {

    Set<String> retrieve(String language);
}
