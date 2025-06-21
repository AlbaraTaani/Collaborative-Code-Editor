package com.collaborative.editor.controller;

import com.collaborative.editor.model.MessageLog;
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
public class HistoryController {

    private final LogsService logsService;

    @Autowired
    public HistoryController(LogsService logsService) {
        this.logsService = logsService;
    }

    /**
     * Returns full sequence of MessageLog entries for a repo,
     * sorted newest → oldest by timestamp.
     */
    @GetMapping("/repo/{repoId}/history")
    public ResponseEntity<Map<String, List<MessageLog>>> getRepoHistory(
            @PathVariable String repoId) {
        List<MessageLog> logs = logsService.getLogsByRepoId(repoId)
            .orElse(List.of())
            .stream()
            .sorted(Comparator.comparingLong(MessageLog::getTimestamp).reversed())
            .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("repoLogs", logs));
    }
}