package com.collaborative.editor.controller;

import com.collaborative.editor.model.File;
import com.collaborative.editor.service.CodeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin //(origins = "http://localhost:3000")
@RequestMapping("/api/viewer")
public class CodeHistoryController {

    private final CodeHistoryService codeHistoryService;

    @Autowired
    public CodeHistoryController(CodeHistoryService codeHistoryService) {
        this.codeHistoryService = codeHistoryService;
    }

    /**
     * Returns all versions of every file in the repo,
     * sorted by lastModifiedAt descending (newest first).
     */
    @GetMapping("/repo/{repoId}/code-history")
    public ResponseEntity<Map<String, List<File>>> getCodeHistory(
            @PathVariable String repoId) {
        List<File> versions = codeHistoryService.findByRepoIdOrderByLastModifiedAtDesc(repoId);
        return ResponseEntity.ok(Map.of("codeHistory", versions));
    }
}