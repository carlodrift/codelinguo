package fr.unilim.codelinguo.common.model.reader;

import fr.unilim.codelinguo.common.model.Word;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.nio.file.Path;
import java.util.List;

public class GitProjectReader {

    private final FileReader fileReader = new FileReader();

    public List<Word> readFromGitUrl(String gitUrl, String branch) {
        try {
            Path repoPath = GitRepoDownloader.cloneRepository(gitUrl, branch);
            return this.fileReader.read(repoPath.toString());
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to read from Git URL: " + gitUrl, e);
        }
    }
}
