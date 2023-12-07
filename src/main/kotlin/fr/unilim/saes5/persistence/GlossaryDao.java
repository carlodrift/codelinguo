package fr.unilim.saes5.persistence;

import fr.unilim.saes5.model.Glossary;

import java.util.List;

public interface GlossaryDao {

    void saveProject(Glossary project);

    List<Glossary> getAllProjects();
}
