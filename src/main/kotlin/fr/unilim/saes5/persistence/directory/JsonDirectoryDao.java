package fr.unilim.saes5.persistence.directory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class JsonDirectoryDao implements DirectoryDao {

    private final File file;
    private final Gson gson;

    public JsonDirectoryDao() {
        String userHome = System.getProperty("user.home");
        File directory = new File(userHome, DirectoryDao.DIRECTORY_STORE);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        this.file = new File(directory, "directory.json");
        this.gson = new GsonBuilder().create();

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
                this.saveDirectory(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveDirectory(String directory) {
        try (Writer writer = new FileWriter(this.file, false)) {
            this.gson.toJson(directory, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String loadDirectory() {
        try (Reader reader = new FileReader(this.file)) {
            return this.gson.fromJson(reader, String.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
