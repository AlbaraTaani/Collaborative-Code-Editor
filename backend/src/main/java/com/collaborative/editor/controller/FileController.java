package com.collaborative.editor.controller;

import com.collaborative.editor.dto.FileDTO;
import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.exception.FileConflictException;
import com.collaborative.editor.exception.FileNotFoundException;
import com.collaborative.editor.model.File;
import com.collaborative.editor.service.interfaces.FileService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin
public class FileController {

    private final FileService fileService;

    public FileController(@Qualifier("FileServiceImpl") FileService fileService) {
        this.fileService = fileService;
    }

    @PreAuthorize("@repoSecurityService.canViewRepo(principal.username, #repoId)")
    @GetMapping("/list-files/{projectName}/{repoId}")
    public ResponseEntity<Map<String, List<FileDTO>>> listFiles(@PathVariable String projectName,
            @PathVariable String repoId) {
        ProjectDTO projectDTO = ProjectDTO
                .builder()
                .projectName(projectName)
                .repoId(repoId)
                .build();

        List<FileDTO> files = fileService.getFiles(projectDTO);

        if (files.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of("files", files));
    }

    @PostMapping("/create-file")
    public ResponseEntity<String> createFile(@RequestBody FileDTO fileDTO) {
        try {
            fileService.createFile(fileDTO);
            return ResponseEntity.ok("File created successfully!");
        } catch (FileConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("File already exists: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating file: " + e.getMessage());
        }
    }

    @PostMapping("/pull-file-content")
    public ResponseEntity<Map<String, File>> pullFileContent(@RequestBody FileDTO fileDTO) {
        try {
            File pulledFile = fileService.pullFileContent(fileDTO);
            return ResponseEntity.ok(Map.of("file", pulledFile));
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/push-file-content")
    public ResponseEntity<String> pushFile(@RequestBody File file) {
        try {
            fileService.pushFileContent(file);
            return ResponseEntity.ok("File pushed successfully!");
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error pushing file content: " + e.getMessage());
        }
    }

    @PostMapping("/merge-file-content")
    public ResponseEntity<Map<String, File>> mergeFileContent(@RequestBody File file) {
        try {
            File conflictVersion = fileService.mergeFileContent(file);
            return ResponseEntity.ok(Map.of("file", conflictVersion));
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}