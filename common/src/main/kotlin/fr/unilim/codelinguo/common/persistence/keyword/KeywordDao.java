package fr.unilim.codelinguo.common.persistence.keyword;

import java.util.Set;

public interface KeywordDao {

    Set<String> retrieve(String language);
}
