package com.collaborative.editor.controller;

import com.collaborative.editor.model.MessageLog;
import com.collaborative.editor.dto.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/viewer")
public class CodeHistoryController {

    private final LogsService logsService;

    @Autowired
    public CodeHistoryController(LogsService logsService) {
        this.logsService = logsService;
    }

    /**
     * Returns full code history for a given repoId,
     * grouping entries by filename and ordered newest → oldest timestamps.
     * Each MessageLog.content contains the file snapshot at that point.
     */
    @GetMapping("/repo/{repoId}/code-history")
    public ResponseEntity<Map<String, List<MessageLog>>> getCodeHistory(@PathVariable String repoId) {
        // Fetch all logs for the repo
        List<MessageLog> allLogs = logsService.getLogsByRepoId(repoId)
            .orElse(List.of());
        
        // Filter only logs that have a filename (i.e. file operations) and sort
        List<MessageLog> fileLogs = allLogs.stream()
            .filter(log -> log.getFilename() != null && !log.getFilename().isBlank())
            .sorted(Comparator.comparingLong(MessageLog::getTimestamp).reversed())
            .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("codeHistory", fileLogs));
    }
}