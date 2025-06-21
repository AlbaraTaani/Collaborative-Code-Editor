package com.collaborative.editor.controller;

import com.collaborative.editor.model.File;
import com.collaborative.editor.service.FileVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


//new
@RestController
@RequestMapping("/api/viewer")
@CrossOrigin
public class FileVersionController {
private final FileVersionService fileVersionService;
@Autowired
public FileVersionController(FileVersionService fileVersionService) {
    this.fileVersionService = fileVersionService;
}

@GetMapping("/repo/{repoId}/file-versions")
public ResponseEntity<Map<String, List<File>>> getAllFileVersions(
        @PathVariable String repoId) {
    List<File> versions = fileVersionService.findByRepoIdOrderByLastModifiedAtDesc(repoId);
    return ResponseEntity.ok(Map.of("fileVersions", versions));
}