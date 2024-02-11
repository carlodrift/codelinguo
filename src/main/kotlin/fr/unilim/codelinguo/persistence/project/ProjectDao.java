package fr.unilim.codelinguo.persistence.project;

import fr.unilim.codelinguo.model.Glossary;

import java.util.List;

public interface ProjectDao {

    String CODELINGUO_PROJECTS = ".codelinguo/projects";

    void save(Glossary project, String name);

    List<Glossary> retrieve();

    void delete(String name);
}
