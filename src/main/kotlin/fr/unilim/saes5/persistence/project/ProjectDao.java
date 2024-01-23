package fr.unilim.saes5.persistence.project;

import fr.unilim.saes5.model.Glossary;

import java.util.List;

public interface ProjectDao {

    String CODELINGUO_PROJECTS = ".codelinguo/projects";

    void save(Glossary project, String name);

    List<Glossary> retrieve();

    void delete(String name);
}
