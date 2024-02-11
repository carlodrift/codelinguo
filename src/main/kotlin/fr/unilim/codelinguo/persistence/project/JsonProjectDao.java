package fr.unilim.codelinguo.persistence.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.unilim.codelinguo.model.Glossary;
import fr.unilim.codelinguo.model.context.Context;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonProjectDao implements ProjectDao {

    private static final String FILE_EXTENSION = ".json";
    private final Gson gson;

    private final File directory;

    public JsonProjectDao() {
        String userHome = System.getProperty("user.home");
        this.directory = new File(userHome, ProjectDao.CODELINGUO_PROJECTS);
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Context.class, new ContextDeserializer());
        this.gson = gsonBuilder.create();
    }

    @Override
    public void save(Glossary project, String name) {
        File jsonFile = new File(this.directory, name + JsonProjectDao.FILE_EXTENSION);
        if (!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
                this.writeListToFile(new ArrayList<>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        project.setName(name);
        List<Glossary> singleProjectList = new ArrayList<>();
        singleProjectList.add(project);
        this.writeListToFile(singleProjectList);
    }

    public List<Glossary> retrieve() {
        List<Glossary> allGlossaries = new ArrayList<>();

        File folder = this.directory;
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(JsonProjectDao.FILE_EXTENSION));

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                try (Reader reader = new FileReader(file)) {
                    Glossary glossary = this.gson.fromJson(reader, Glossary.class);
                    allGlossaries.add(glossary);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("demo.json");
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Glossary demoGlossary = this.gson.fromJson(reader, Glossary.class);

            boolean demoExists = allGlossaries.stream()
                    .anyMatch(glossary -> glossary.getName().equalsIgnoreCase(demoGlossary.getName()));

            if (!demoExists) {
                demoGlossary.setDemo(true);
                allGlossaries.add(demoGlossary);
            }
        } catch (IOException | NullPointerException ignored) {
        }

        return allGlossaries;
    }

    private void writeListToFile(List<Glossary> projects) {
        for (Glossary project : projects) {
            String fileName = project.getName();
            try (Writer writer = new FileWriter(new File(this.directory, fileName + JsonProjectDao.FILE_EXTENSION), false)) {
                this.gson.toJson(project, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(String name) {
        File projectFile = new File(this.directory, name + JsonProjectDao.FILE_EXTENSION);
        if (projectFile.exists()) {
            if (!projectFile.delete()) {
                System.err.println("Failed to delete the project: " + name);
            }
        } else {
            System.err.println("Project not found: " + name);
        }
    }
}
