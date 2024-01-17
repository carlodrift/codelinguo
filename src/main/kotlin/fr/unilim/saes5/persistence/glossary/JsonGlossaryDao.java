package fr.unilim.saes5.persistence.glossary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.unilim.saes5.model.Glossary;
import fr.unilim.saes5.model.context.Context;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonGlossaryDao implements GlossaryDao {

    private static final String CODELINGUO_PROJECTS = ".codelinguo/projects";
    private final Gson gson;

    private File directory;

    public JsonGlossaryDao() {
        String userHome = System.getProperty("user.home");
        this.directory = new File(userHome, CODELINGUO_PROJECTS);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Context.class, new ContextDeserializer());
        this.gson = gsonBuilder.create();
    }

    @Override
    public void saveProject(Glossary project, String name) {
        File jsonFile = new File(directory, name);
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

    public List<Glossary> getAllProjects() {
        List<Glossary> allGlossaries = new ArrayList<>();
        File folder = this.directory;

        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

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

        return allGlossaries;
    }

    private void writeListToFile(List<Glossary> projects) {
        for (Glossary project : projects) {
            String fileName = project.getName();
            try (Writer writer = new FileWriter(new File(this.directory, fileName + ".json"), false)) {
                this.gson.toJson(project, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
