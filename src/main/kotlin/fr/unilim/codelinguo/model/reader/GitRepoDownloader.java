package fr.unilim.codelinguo.model.reader;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.nio.file.Files;
import java.nio.file.Path;

public class GitRepoDownloader {

    private GitRepoDownloader() {
    }

    public static Path cloneRepository(String repoUrl, String branch) throws GitAPIException {
        try {
            Path tempDir = Files.createTempDirectory("gitRepo");
            Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(tempDir.toFile())
                    .setBranch(branch)
                    .call();
            return tempDir;
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone repository: " + repoUrl, e);
        }
    }
}