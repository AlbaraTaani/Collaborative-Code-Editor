package com.collaborative.editor.service.impls;

import com.collaborative.editor.dto.FileDTO;
import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.dto.RepoDTO;
import com.collaborative.editor.repository.RepoMembershipRepository;
import com.collaborative.editor.repository.RepoRepository;
import com.collaborative.editor.exception.RepositoryCreationFailedException;
import com.collaborative.editor.exception.RepositoryMembershipNotFoundException;
import com.collaborative.editor.exception.RepositoryNotFoundException;
import com.collaborative.editor.model.Repo;
import com.collaborative.editor.model.RepoMembership;
import com.collaborative.editor.model.RepoRole;
import com.collaborative.editor.model.User;
import com.collaborative.editor.service.interfaces.FileService;
import com.collaborative.editor.service.interfaces.ProjectService;
import com.collaborative.editor.service.interfaces.RepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service("RepoServiceImpl")
public class RepoServiceImpl implements RepoService {

    @Autowired
    private RepoRepository repoRepository;

    @Autowired
    private RepoMembershipRepository repoMembershipRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FileService fileService;

    private final Map<String, Object> repoLocks = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public String createRepo(User owner, String repoName) {
        try {
            String repoId = UUID.randomUUID().toString();
            Repo repo = Repo.builder()
                    .name(repoName)
                    .repoId(repoId)
                    .repoMemberships(new ArrayList<>())
                    .projects(new HashSet<>())
                    .build();

            repoRepository.save(repo);
            addUserToRepo(repo, owner, RepoRole.OWNER);

            createDefaultProject(repo);
            createDefaultFile(repo);
            return repoId;
        } catch (Exception e) {
            throw new RepositoryCreationFailedException("Failed to create repo: " + e.getMessage());
        }
    }

    private void createDefaultProject(Repo repo) {
        try {
            projectService.createProject(
                    ProjectDTO
                            .builder()
                            .projectName("Main-Project")
                            .repoId(repo.getRepoId())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create default project: " + e.getMessage());
        }
    }

    private void createDefaultFile(Repo repo) {
        try {
            fileService.createFile(
                    FileDTO
                            .builder()
                            .filename("README")
                            .projectName("Main-Project")
                            .repoId(repo.getRepoId())
                            .extension(".md")
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create default file: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Repo> getRepoById(String repoId) {
        try {
            return repoRepository.findByRepoId(repoId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void deleteRepo(String repoId) {
        synchronized (getRepoLock(repoId)) {
            Repo repo = repoRepository.findByRepoId(repoId)
                    .orElseThrow(() -> new RepositoryNotFoundException("Repo not found with ID: " + repoId));

            repoRepository.deleteByRepoId(repoId);
        }
    }

    @Override
    public void addUserToRepo(Repo repo, User user, RepoRole role) {
        Object repoLock = getRepoLock(repo.getRepoId());
        synchronized (repoLock) {
            RepoMembership newMembership = RepoMembership
                    .builder()
                    .repo(repo)
                    .user(user)
                    .role(role)
                    .build();

            repo.getRepoMemberships().add(newMembership);
            user.getRepoMemberships().add(newMembership);
            repoMembershipRepository.save(newMembership);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepoDTO> getCollaborativeRepos(User user) {
        return user.getRepoMemberships().stream()
                .filter(rm -> rm.getRole() == RepoRole.COLLABORATOR)
                .map(RepoMembership::getRepo)
                .toList().stream()
                .map(repo -> new RepoDTO(repo.getRepoId(), repo.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepoDTO> getViewerRepos(User user) {
        return user.getRepoMemberships().stream()
                .filter(rm -> rm.getRole() == RepoRole.VIEWER)
                .map(RepoMembership::getRepo)
                .toList().stream()
                .map(repo -> new RepoDTO(repo.getRepoId(), repo.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepoDTO> getUserOwnedRepos(User user) {
        Optional<List<RepoMembership>> repoMemberships = repoMembershipRepository.findByUser(user.getEmail());

        return repoMemberships.map(memberships -> memberships.stream()
                .filter(rm -> rm.getRole() == RepoRole.OWNER)
                .map(RepoMembership::getRepo)
                .toList().stream()
                .map(repo -> new RepoDTO(repo.getRepoId(), repo.getName()))
                .collect(Collectors.toList())).orElse(Collections.emptyList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getViewers(Repo repo) {
        return repo.getRepoMemberships().stream()
                .filter(rm -> rm.getRole() == RepoRole.VIEWER)
                .map(RepoMembership::getUser)
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getCollaborators(Repo repo) {
        return repo.getRepoMemberships().stream()
                .filter(rm -> rm.getRole() == RepoRole.COLLABORATOR)
                .map(RepoMembership::getUser)
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeUserFromRepo(Repo repo, User user, RepoRole role) {
        Object repoLock = getRepoLock(repo.getRepoId());

        synchronized (repoLock) {
            RepoMembership membership = repoMembershipRepository.findByRole(repo, user, role)
                    .orElseThrow(() -> new RepositoryMembershipNotFoundException("Repo membership not found"));

            repo.getRepoMemberships().remove(membership);
            user.getRepoMemberships().remove(membership);

            repoMembershipRepository.delete(membership);
        }
    }

    @Override
    @Transactional
    public void rename(Repo repo) {

        Object repoLock = getRepoLock(repo.getRepoId());

        synchronized (repoLock) {
            repoRepository.save(repo);
        }
    }

    private Object getRepoLock(String repoId) {
        return repoLocks.computeIfAbsent(repoId, k -> new Object());
    }
}
