package com.collaborative.editor.controller;


import com.collaborative.editor.dto.CodeExecution;
import com.collaborative.editor.service.impls.CodeExecutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/execute")
public class ExecutionController {

    private final CodeExecutorService codeExecutor;

    public ExecutionController(CodeExecutorService codeExecutor) {
        this.codeExecutor = codeExecutor;
    }

    @PostMapping("/run")
    public  ResponseEntity<Map<String, String>>  runCode(@RequestBody CodeExecution codeRequest) {
        try {
            String output = codeExecutor.executeCode(codeRequest);
            return ResponseEntity.ok(Map.of("output", output));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }
}