package com.collaborative.editor.controller;

import com.collaborative.editor.model.CodeUpdate;
import com.collaborative.editor.model.MessageLog;
import com.collaborative.editor.service.interfaces.EditorService;
import com.collaborative.editor.service.interfaces.LogsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Controller
public class WebsocketController {

    private final ExecutorService executorService;
    private final EditorService editorService;
    private final LogsService logService;
    private final Map<String, Object> lineLocks = new ConcurrentHashMap<>();

    public WebsocketController(@Qualifier("EditorServiceImpl") EditorService editorService,
            @Qualifier("LogsServiceImpl") LogsService logService,
            @Qualifier("webSocketExecutorService") ExecutorService executorService) {
        this.editorService = editorService;
        this.logService = logService;
        this.executorService = executorService;
    }

    @MessageMapping("/code/updates/{repoId}/{projectName}/{filename}/COLLABORATOR")
    @SendTo("/topic/file/updates/{repoId}/{projectName}/{filename}")
    public CodeUpdate handleCollaboratorCode(
            @DestinationVariable String repoId,
            @DestinationVariable String projectName,
            @DestinationVariable String filename,
            @Payload CodeUpdate codeUpdate) {

        String lineKey = repoId + "-" + projectName + "-" + filename + "-" + codeUpdate.getLineNumber();

        executorService.submit(() -> {
            synchronized (getLineLock(lineKey)) {
                editorService.saveCodeUpdate(codeUpdate);
            }
        });

        return codeUpdate;
    }

    @MessageMapping("/code/updates/{repoId}/{projectName}/{filename}/VIEWER")
    @SendTo("/topic/file/updates/{repoId}/{projectName}/{filename}")
    public CodeUpdate handleViewerComment(
            @DestinationVariable String repoId,
            @DestinationVariable String projectName,
            @DestinationVariable String filename,
            @Payload CodeUpdate codeUpdate) {

        String lineKey = repoId + "-" + projectName + "-" + filename + "-" + codeUpdate.getLineNumber();

        codeUpdate = editorService.insertComment(codeUpdate);
        CodeUpdate finalCodeUpdate = codeUpdate;

        executorService.submit(() -> {
            synchronized (getLineLock(lineKey)) {
                editorService.saveCodeUpdate(finalCodeUpdate);
            }
        });

        return finalCodeUpdate;
    }

    @MessageMapping("/chat/{repoId}")
    @SendTo("/topic/chat/{repoId}")
    public MessageLog handleChatMessage(@DestinationVariable("repoId") String repoId,
            @Payload MessageLog messageLog) {

        messageLog.setTimestamp(System.currentTimeMillis());
        executorService.submit(() -> {
            if (!messageLog.getType().equalsIgnoreCase("message")) {
                logService.saveLog(messageLog);
            }
        });

        return messageLog;
    }

    private Object getLineLock(String lineKey) {
        return lineLocks.computeIfAbsent(lineKey, k -> new Object());
    }
}
