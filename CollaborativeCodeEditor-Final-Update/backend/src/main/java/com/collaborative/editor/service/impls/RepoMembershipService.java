package com.collaborative.editor.service.impls;

import com.collaborative.editor.model.Repo;
import com.collaborative.editor.model.RepoRole;
import com.collaborative.editor.model.RepoMembership;
import com.collaborative.editor.model.User;
import com.collaborative.editor.repository.RepoMembershipRepository;
import com.collaborative.editor.repository.RepoRepository;
import com.collaborative.editor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class RepoMembershipService {

    @Autowired
    private RepoMembershipRepository repoMembershipRepository;

    @Autowired
    private RepoRepository repoRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean isOwner(String userEmail, String repoId) {
        return getUserRolesForRepo(userEmail, repoId).contains(RepoRole.OWNER);
    }

    public boolean isCollaborator(String userEmail, String repoId) {
        return getUserRolesForRepo(userEmail, repoId).contains(RepoRole.COLLABORATOR);
    }

    public boolean isViewer(String userEmail, String repoId) {
        return getUserRolesForRepo(userEmail, repoId).contains(RepoRole.VIEWER);
    }

    public List<RepoRole> getUserRolesForRepo(String userEmail, String repoId) {
        Repo repo = repoRepository.findByRepoId(repoId).get();
        User user = userRepository.findOneByEmail(userEmail).get();
        Optional<List<RepoMembership>> userRoles = repoMembershipRepository.findRoles(repo, user);

        if (userRoles.isEmpty() || userRoles.get().isEmpty()) {
            throw new AccessDeniedException("User does not have access to this repo");
        }

        return userRoles.get().stream()
                .map(RepoMembership::getRole)
                .collect(Collectors.toList());
    }
}
