package com.collaborative.editor.service.repoService;

import com.collaborative.editor.repository.RepoMembershipRepository;
import com.collaborative.editor.repository.RepoRepository;
import com.collaborative.editor.model.Repo;
import com.collaborative.editor.model.RepoRole;
import com.collaborative.editor.model.RepoMembership;
import com.collaborative.editor.model.User;
import com.collaborative.editor.service.impls.RepoServiceImpl;
import com.collaborative.editor.service.interfaces.FileService;
import com.collaborative.editor.service.interfaces.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepoServiceImplTest {

    @InjectMocks
    private RepoServiceImpl repoService;

    @Mock
    private RepoRepository repoRepository;

    @Mock
    private RepoMembershipRepository repoMembershipRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private FileService fileService;

    @Mock
    private ReentrantLock lock;

    private User owner;
    private Repo repo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        owner = new User();
        owner.setEmail("test@example.com");
        owner.setUsername("testUser");

        repo = new Repo();
        repo.setRepoId(UUID.randomUUID().toString());
        repo.setName("Test Repo");
    }

    @Test
    void createRepo_success() {
        when(repoRepository.save(any(Repo.class))).thenReturn(repo);

        when(repoMembershipRepository.save(any(RepoMembership.class))).thenReturn(new RepoMembership());

        String repoId = repoService.createRepo(owner, "Test Repo");

        assertNotNull(repoId);
        verify(repoRepository, times(1)).save(any(Repo.class));

        verify(repoMembershipRepository, times(1)).save(any(RepoMembership.class));
    }

    @Test
    void createRepo_failure() {
        when(repoRepository.save(any(Repo.class))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> repoService.createRepo(owner, "Test Repo"));
    }

    @Test
    void deleteRepo_success() {
        when(repoRepository.findByRepoId(anyString())).thenReturn(Optional.of(repo));
        doNothing().when(repoRepository).deleteByRepoId(anyString());

        assertDoesNotThrow(() -> repoService.deleteRepo(repo.getRepoId()));
        verify(repoRepository, times(1)).deleteByRepoId(anyString());
    }

    @Test
    void deleteRepo_repoNotFound() {
        when(repoRepository.findByRepoId(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> repoService.deleteRepo(repo.getRepoId()));
    }

    @Test
    void removeUserFromRepo_success() {
        when(repoMembershipRepository.findByRole(repo, owner, RepoRole.COLLABORATOR))
                .thenReturn(Optional.of(new RepoMembership()));
        doNothing().when(repoMembershipRepository).delete(any());

        assertDoesNotThrow(() -> repoService.removeUserFromRepo(repo, owner, RepoRole.COLLABORATOR));
        verify(repoMembershipRepository, times(1)).delete(any());
    }

    @Test
    void renameRepo_success() {
        when(repoRepository.save(repo)).thenReturn(repo);
        repoService.rename(repo);

        verify(repoRepository, times(1)).save(repo);
    }

}
