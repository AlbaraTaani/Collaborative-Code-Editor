package com.collaborative.editor.service.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RepoSecurityService {

    @Autowired
    private RepoMembershipService repoMembershipService;

    public boolean isOwner(String userEmail, String repoId) {
        return repoMembershipService.isOwner(userEmail, repoId);
    }

    public boolean canViewRepo(String userEmail, String repoId) {
        return repoMembershipService.isOwner(userEmail, repoId) ||
                repoMembershipService.isCollaborator(userEmail, repoId) ||
                repoMembershipService.isViewer(userEmail, repoId);
    }
}
