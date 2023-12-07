package fr.unilim.saes5.persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.unilim.saes5.model.Glossary;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonProjectDao implements ProjectDao {

    private final File jsonFile;
    private final Gson gson;

    public JsonProjectDao(String filePath) {
        this.jsonFile = new File(filePath);
        this.gson = new Gson();
        if (!this.jsonFile.exists()) {
            try {
                this.jsonFile.createNewFile();
                this.writeListToFile(new ArrayList<>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveProject(Glossary project) {
        List<Glossary> projects = this.getAllProjects();
        projects.add(project);
        this.writeListToFile(projects);
    }

    @Override
    public List<Glossary> getAllProjects() {
        try (Reader reader = new FileReader(this.jsonFile)) {
            Type listType = new TypeToken<ArrayList<Glossary>>() {
            }.getType();
            return this.gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void writeListToFile(List<Glossary> projects) {
        try (Writer writer = new FileWriter(this.jsonFile, false)) {
            this.gson.toJson(projects, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
