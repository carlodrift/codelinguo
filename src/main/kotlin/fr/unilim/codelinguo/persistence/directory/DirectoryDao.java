package fr.unilim.codelinguo.persistence.directory;

public interface DirectoryDao {

    String DIRECTORY_STORE = ".codelinguo/";

    void save(String directory);

    String retrieve();
}
