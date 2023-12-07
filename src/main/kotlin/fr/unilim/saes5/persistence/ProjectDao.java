package fr.unilim.saes5.persistence;

import fr.unilim.saes5.model.Glossary;

import java.util.List;

public interface ProjectDao {

    void saveProject(Glossary project);

    List<Glossary> getAllProjects();
}
