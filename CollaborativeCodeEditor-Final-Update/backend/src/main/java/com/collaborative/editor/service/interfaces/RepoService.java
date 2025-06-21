package com.collaborative.editor.service.interfaces;

import com.collaborative.editor.dto.RepoDTO;
import com.collaborative.editor.model.Repo;
import com.collaborative.editor.model.RepoRole;
import com.collaborative.editor.model.User;

import java.util.List;
import java.util.Optional;

public interface RepoService {
    String createRepo(User owner, String repoName);

    Optional<Repo> getRepoById(String repoId);

    void deleteRepo(String repoId);

    void addUserToRepo(Repo repo, User user, RepoRole role);

    List<RepoDTO> getCollaborativeRepos(User user);

    List<RepoDTO> getViewerRepos(User user);

    List<RepoDTO> getUserOwnedRepos(User user);

    List<String> getViewers(Repo repo);

    List<String> getCollaborators(Repo repo);

    void removeUserFromRepo(Repo repo, User user, RepoRole role);

    void rename(Repo repo);
}
