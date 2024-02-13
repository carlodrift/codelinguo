package fr.unilim.codelinguo.common.persistence.recentgiturl;

import java.util.Set;

public interface RecentGitURLDAO {

    String DIRECTORY_STORE = ".codelinguo/";

    void add(String url);

    Set<String> retrieve();
}
