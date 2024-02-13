package fr.unilim.codelinguo.common.persistence.recent_git_url;

import java.util.Set;

public interface RecentGitURLDAO {

    String DIRECTORY_STORE = ".codelinguo/";

    void add(String url);

    Set<String> retrieve();
}
