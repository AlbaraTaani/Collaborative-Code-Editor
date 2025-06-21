package com.collaborative.editor.controller;

import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.service.interfaces.ProjectService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(@Qualifier("ProjectServiceImpl") ProjectService projectService) {

        this.projectService = projectService;
    }

    @PostMapping("/create-project")
    public ResponseEntity<String> createProject(@RequestBody ProjectDTO request) {
        try {
            projectService.createProject(request);

            return ResponseEntity.ok("Project created successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create project.");
        }
    }

    @PreAuthorize("@repoSecurityService.canViewRepo(principal.username, #repoId)")
    @GetMapping("/repo/{repoId}")
    public ResponseEntity<Map<String, List<ProjectDTO>>> listProjects(@PathVariable String repoId) {
        try {
            List<ProjectDTO> projects = projectService.getProjects(repoId);
            return ResponseEntity.ok(Map.of("projects", projects));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/remove-project")
    public ResponseEntity<String> deleteProject(@RequestBody ProjectDTO project) {
        try {
            projectService.deleteProject(project);
            return ResponseEntity.ok("Project deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("@repoSecurityService.canViewRepo(principal.username, #repoId)")
    @GetMapping("/{repoId}/{projectName}/download-project")
    public void downloadProject(@PathVariable String repoId,
            @PathVariable String projectName,
            HttpServletResponse response) {

        try {
            projectService.downloadProject(new ProjectDTO(projectName, repoId), response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}