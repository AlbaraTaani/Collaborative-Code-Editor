package com.collaborative.editor.controller.repo;

import com.collaborative.editor.controller.RepoController;
import com.collaborative.editor.dto.AddMemberRequest;
import com.collaborative.editor.dto.CreateRepoRequest;
import com.collaborative.editor.dto.RepoDTO;
import com.collaborative.editor.model.Repo;
import com.collaborative.editor.model.RepoRole;
import com.collaborative.editor.model.User;
import com.collaborative.editor.service.impls.FileServiceImpl;
import com.collaborative.editor.service.impls.ProjectServiceImpl;
import com.collaborative.editor.service.impls.RepoServiceImpl;
import com.collaborative.editor.service.impls.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepoControllerTest {

    @InjectMocks
    private RepoController repoController;

    @Mock
    private RepoServiceImpl repoService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ProjectServiceImpl projectService;

    @Mock
    private FileServiceImpl fileService;

    private CreateRepoRequest createRepoRequest;
    private AddMemberRequest addMemberRequest;
    private User owner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        owner = new User();
        owner.setEmail("test@example.com");

        createRepoRequest = new CreateRepoRequest();
        createRepoRequest.setMemberEmail("test@example.com");
        createRepoRequest.setRepoName("Test Repo");

        addMemberRequest = new AddMemberRequest();
        addMemberRequest.setRepoId("12345");
        addMemberRequest.setMemberEmail("test@example.com");
        addMemberRequest.setRole("COLLABORATOR");
    }

    @Test
    void createRepo_success() {
        when(userService.findUserByEmail(anyString())).thenReturn(Optional.of(owner));
        when(repoService.createRepo(any(User.class), anyString())).thenReturn("repoId123");

        ResponseEntity<Map<String, String>> response = repoController.createRepo(createRepoRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("repoId123", response.getBody().get("repoId"));
    }

    @Test
    void deleteRepo_success() {
        doNothing().when(repoService).deleteRepo(anyString());

        ResponseEntity<String> response = repoController.deleteRepo("repoId123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Repo deleted successfully!", response.getBody());
    }

    @Test
    void deleteRepo_failure() {
        doThrow(new RuntimeException()).when(repoService).deleteRepo(anyString());

        ResponseEntity<String> response = repoController.deleteRepo("repoId123");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void addMember_success() {
        when(userService.findUserByEmail(anyString())).thenReturn(Optional.of(owner));
        when(repoService.getRepoById(anyString())).thenReturn(Optional.of(new Repo()));
        doNothing().when(repoService).addUserToRepo(any(Repo.class), any(User.class), any(RepoRole.class));

        ResponseEntity<String> response = repoController.addMember(addMemberRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Viewer added successfully!", response.getBody());
    }

    @Test
    void addMember_failure() {
        when(userService.findUserByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> response = repoController.addMember(addMemberRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Failed to find user!", response.getBody());
    }

    @Test
    void getRepoDetails_success() {
        Repo repo = new Repo();
        repo.setRepoId("repoId123");
        when(repoService.getRepoById(anyString())).thenReturn(Optional.of(repo));
        when(repoService.getCollaborators(any(Repo.class))).thenReturn(Arrays.asList("user1@example.com"));
        when(repoService.getViewers(any(Repo.class))).thenReturn(Arrays.asList("viewer@example.com"));

        ResponseEntity<Map<String, List<Object>>> response = repoController.getRepoDetails("repoId123");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("collaborators"));
    }

    @Test
    void getRepoDetails_failure() {
        when(repoService.getRepoById(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Map<String, List<Object>>> response = repoController.getRepoDetails("repoId123");

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void renameRepo_success() {
        RepoDTO repoDTO = new RepoDTO("repoId123", "New Repo Name");
        when(repoService.getRepoById(anyString())).thenReturn(Optional.of(new Repo()));
        doNothing().when(repoService).rename(any(Repo.class));

        ResponseEntity<String> response = repoController.renameRepo(repoDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Repo renamed successfully!", response.getBody());
    }
}