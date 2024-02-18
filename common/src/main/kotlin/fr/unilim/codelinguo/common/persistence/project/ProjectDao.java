package fr.unilim.codelinguo.common.persistence.project;

import fr.unilim.codelinguo.common.model.Glossary;

import java.io.File;
import java.util.List;

public interface ProjectDao {

    String CODELINGUO_PROJECTS = ".codelinguo" + File.separator + "projects";

    void save(Glossary project, String name);

    List<Glossary> retrieve();

    void delete(String name);
}
