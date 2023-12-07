package fr.unilim.saes5.persistence;

import fr.unilim.saes5.model.Project;

import java.util.List;

public interface ProjectDao {

    void saveProject(Project project);

    List<Project> getAllProjects();
}
