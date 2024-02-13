package fr.unilim.codelinguo.common.persistence.recentgiturl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.unilim.codelinguo.common.persistence.directory.DirectoryDao;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class JsonRecentGitURLDAO implements RecentGitURLDAO {

    private final File file;
    private final Gson gson;
    private final Type setType = new TypeToken<Set<String>>() {
    }.getType();

    public JsonRecentGitURLDAO() {
        String userHome = System.getProperty("user.home");
        File directory = new File(userHome, DirectoryDao.DIRECTORY_STORE);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        this.file = new File(directory, "recent_git_urls.json");
        this.gson = new GsonBuilder().create();

        if (!this.file.exists()) {
            try {
                boolean created = this.file.createNewFile();
                if (!created) {
                    throw new IOException("Failed to create file: " + this.file);
                }
                this.add(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void add(String url) {
        Set<String> urls = this.retrieve();
        if (urls == null) {
            urls = new HashSet<>();
        }
        if (url != null) {
            urls.add(url);
        }
        try (Writer writer = new FileWriter(this.file, false)) {
            this.gson.toJson(urls, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> retrieve() {
        if (!this.file.exists()) {
            return new HashSet<>();
        }
        try (Reader reader = new FileReader(this.file)) {
            return this.gson.fromJson(reader, this.setType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
