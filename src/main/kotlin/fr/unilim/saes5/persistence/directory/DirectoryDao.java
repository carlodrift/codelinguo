package fr.unilim.saes5.persistence.directory;

public interface DirectoryDao {
    void saveDirectory(String directory);
    String loadDirectory();
}
