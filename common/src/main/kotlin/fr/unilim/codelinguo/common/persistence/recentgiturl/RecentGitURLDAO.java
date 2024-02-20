package fr.unilim.codelinguo.common.persistence.recentgiturl;

import java.io.File;
import java.util.Set;

public interface RecentGitURLDAO {

    String DIRECTORY_STORE = ".codelinguo" + File.separator;

    void add(String url);

    Set<String> retrieve();
}
