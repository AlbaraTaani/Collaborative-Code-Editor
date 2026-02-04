package com.collaborative.editor.controller;

import com.collaborative.editor.dto.AddMemberRequest;
import com.collaborative.editor.dto.RepoDTO;
import com.collaborative.editor.dto.CreateRepoRequest;
import com.collaborative.editor.exception.RepositoryCreationFailedException;
import com.collaborative.editor.model.Repo;
import com.collaborative.editor.model.RepoRole;
import com.collaborative.editor.model.User;
import com.collaborative.editor.service.impls.RepoSecurityService;
import com.collaborative.editor.service.interfaces.ProjectService;
import com.collaborative.editor.service.interfaces.RepoService;
import com.collaborative.editor.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/repos")
public class RepoController {

    private final RepoService repoService;
    private final UserService userService;
    private final ProjectService projectService;
    private final RepoSecurityService repoSecurityService;

    @Autowired
    public RepoController(
            @Qualifier("RepoServiceImpl") RepoService repoService,
            @Qualifier("UserServiceImpl") UserService userService,
            @Qualifier("ProjectServiceImpl") ProjectService projectService, RepoSecurityService repoSecurityService) {
        this.repoService = repoService;
        this.userService = userService;
        this.projectService = projectService;
        this.repoSecurityService = repoSecurityService;

    }

    @PostMapping("/createRepo")
    public ResponseEntity<Map<String, String>> createRepo(@RequestBody CreateRepoRequest request) {
        String ownerEmail = request.getMemberEmail();
        String repoName = request.getRepoName();

        Map<String, String> body = new HashMap<>();
        try {
            User owner = userService.findUserByEmail(ownerEmail)
                    .orElseThrow(() -> new RepositoryCreationFailedException("Owner not found"));
            String repoId = repoService.createRepo(owner, repoName);

            body.put("repoId", repoId);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(body);

        } catch (RepositoryCreationFailedException e) {
            body.put("error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(body);
        }
    }

    @DeleteMapping("/deleteRepo")
    public ResponseEntity<String> deleteRepo(@RequestBody String repoId) {
        try {
            repoService.deleteRepo(repoId);
            return ResponseEntity.ok("Repo deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add-member")
    public ResponseEntity<String> addMember(@RequestBody AddMemberRequest request) {
        String repoId = request.getRepoId();
        String memberEmail = request.getMemberEmail();
        RepoRole role = RepoRole.valueOf(request.getRole());
        try {
            User user = userService.findUserByEmail(memberEmail).get();
            Repo repo = repoService.getRepoById(repoId).get();

            repoService.addUserToRepo(repo, user, role);

            return ResponseEntity.ok("Viewer added successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to find user!");
        }

    }

    @PostMapping("/remove-member")
    public ResponseEntity<String> removeMember(@RequestBody AddMemberRequest request) {

        try {
            String repoId = request.getRepoId();
            String memberEmail = request.getMemberEmail();
            RepoRole role = RepoRole.valueOf(request.getRole());

            User user = userService.findUserByEmail(memberEmail).get();
            Repo repo = repoService.getRepoById(repoId).get();

            repoService.removeUserFromRepo(repo, user, role);

            return ResponseEntity.ok("Viewer removed successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to find user!");
        }
    }

    @PreAuthorize("@repoSecurityService.canViewRepo(principal.username, #repoId)")
    @PostMapping("/join-repo/{repoId}")
    public ResponseEntity<String> joinRepo(@PathVariable("repoId") String repoId, @RequestBody String repoName) {
        return ResponseEntity.ok("Repo");
    }

    @GetMapping("/join-repo")
    public ResponseEntity<Map<String, List<RepoDTO>>> joinRepo(@RequestParam("username") String username) {
        try {

            User user = userService.findUserByEmail(username).get();

            List<RepoDTO> collaborativeRepos = repoService.getCollaborativeRepos(user);
            List<RepoDTO> viewRepos = repoService.getViewerRepos(user);

            Map<String, List<RepoDTO>> response = new HashMap<>();
            response.put("collaborativeRepos", collaborativeRepos);
            response.put("viewRepos", viewRepos);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/edit-repo/{username}")
    public ResponseEntity<Map<String, List<RepoDTO>>> getOwnedRepos(@PathVariable("username") String username) {
        try {

            User owner = userService.findUserByEmail(username).get();
            List<RepoDTO> repos = repoService.getUserOwnedRepos(owner);

            Map<String, List<RepoDTO>> response = new HashMap<>();
            response.put("repos", repos);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/rename")
    public ResponseEntity<String> renameRepo(@RequestBody RepoDTO repoDTO) {
        String repoId = repoDTO.getRepoId();
        String repoName = repoDTO.getName();
        try {
            Repo repo = repoService.getRepoById(repoId).get();
            repo.setName(repoName);
            repoService.rename(repo);
            return ResponseEntity.ok("Repo renamed successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to rename repo!");
        }
    }

    @PreAuthorize("@repoSecurityService.isOwner(principal.username, #repoId)")
    @GetMapping("/{repoId}/details")
    public ResponseEntity<Map<String, List<Object>>> getRepoDetails(@PathVariable("repoId") String repoId) {
        try {
            Repo repo = repoService.getRepoById(repoId).get();

            List<Object> collaborators = Collections.singletonList(repoService.getCollaborators(repo));
            List<Object> viewers = Collections.singletonList(repoService.getViewers(repo));
            List<Object> projects = Collections.singletonList(projectService.getProjects(repo.getRepoId()));

            Map<String, List<Object>> response = new HashMap<>();
            response.put("collaborators", collaborators);
            response.put("viewers", viewers);
            response.put("projects", projects);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
