package com.collaborative.editor.controller;

import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.model.MessageLog;
import com.collaborative.editor.service.interfaces.LogsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/viewer")
@CrossOrigin
public class LogController {

    private final LogsService logService;

    public LogController(@Qualifier("LogsServiceImpl") LogsService logService) {
        this.logService = logService;
    }

    @GetMapping("/repo/{repoId}/logs")
    public ResponseEntity<Map<String, List<MessageLog>>> getRepoLogs(@PathVariable("repoId") String repoId) {
        try {
            List<MessageLog> repoLogs = logService.getLogsByRepoId(repoId).get();
            return ResponseEntity.ok(Map.of("repoLogs", repoLogs));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{username}/logs")
    public ResponseEntity<Map<String, List<MessageLog>>> getUserLogs(@PathVariable("username") String username) {
        try {
            List<MessageLog> repoLogs = logService.getCollaboratorLogs(username).get();
            return ResponseEntity.ok(Map.of("collaboratorLogs", repoLogs));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/project/{repoId}/{projectName}/logs")
    public ResponseEntity<Map<String, List<MessageLog>>> getProjectLogs(@PathVariable("projectName") String projectName,
            @PathVariable("repoId") String repoId) {
        try {
            List<MessageLog> projectLogs;
            projectLogs = logService.getProjectLogs(new ProjectDTO(projectName, repoId)).get();

            return ResponseEntity.ok(Map.of("projectLogs", projectLogs));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/files/{repoId}/{projectName}/{filename}/logs")
    public ResponseEntity<Map<String, List<MessageLog>>> getFileLogs(@PathVariable("projectName") String projectName,
            @PathVariable("repoId") String repoId,
            @PathVariable("filename") String filename) {
        try {
            List<MessageLog> fileLogs;
            fileLogs = logService.getFileLogs(projectName, repoId, filename).get();

            return ResponseEntity.ok(Map.of("projectLogs", fileLogs));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/files/{repoId}/{type}/logs")
    public ResponseEntity<Map<String, List<MessageLog>>> getActionLogs(@PathVariable("type") String type,
            @PathVariable("repoId") String repoId) {
        try {
            List<MessageLog> actionLogs;
            actionLogs = logService.getLogsByActionType(type, repoId).get();

            return ResponseEntity.ok(Map.of("actionLogs", actionLogs));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}