package fr.unilim.codelinguo.common.persistence.project;

import fr.unilim.codelinguo.common.model.Glossary;

import java.util.List;

public interface ProjectDao {

    String CODELINGUO_PROJECTS = ".codelinguo/projects";

    void save(Glossary project, String name);

    List<Glossary> retrieve();

    void delete(String name);
}
