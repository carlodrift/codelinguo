package fr.unilim.codelinguo.common.persistence.directory;

import java.io.File;

public interface DirectoryDao {

    String DIRECTORY_STORE = ".codelinguo" + File.separator;

    void save(String directory);

    String retrieve();
}
