package com.collaborative.editor.controller;

import com.collaborative.editor.dto.CodeMetrics;
import com.collaborative.editor.dto.CodeMetricsRequest;
import com.collaborative.editor.service.interfaces.EditorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/viewer")
@CrossOrigin
public class EditorController {

    private final EditorService editorService;

    public EditorController(@Qualifier("EditorServiceImpl") EditorService editorService) {
        this.editorService = editorService;
    }

    @PostMapping("/CodeMetrics")
    public ResponseEntity<Map<String, CodeMetrics>> getCodeMetrics(@RequestBody CodeMetricsRequest request) {
        try {
            CodeMetrics metrics = editorService.calculateMetrics(request.getCode(), request.getLanguage());
            return ResponseEntity.ok(Map.of("metrics", metrics));
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

}