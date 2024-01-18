package fr.unilim.saes5.persistence.glossary;

import fr.unilim.saes5.model.Glossary;

import java.util.List;

public interface GlossaryDao {

    String CODELINGUO_PROJECTS = ".codelinguo/projects";

    void saveProject(Glossary project, String name);

    List<Glossary> getAllProjects();
    void deleteProject(String name);
}
