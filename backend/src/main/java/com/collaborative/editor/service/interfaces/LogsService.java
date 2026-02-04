package com.collaborative.editor.service.interfaces;

import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.model.MessageLog;

import java.util.List;
import java.util.Optional;

public interface LogsService {

    void saveLog(MessageLog log);

    Optional<List<MessageLog>> getLogsByActionType(String type, String repoId);

    Optional<List<MessageLog>> getLogsByRepoId(String repoId);

    Optional<List<MessageLog>> getCollaboratorLogs(String sender);

    Optional<List<MessageLog>> getFileLogs(String projectName, String repoId, String filename);

    Optional<List<MessageLog>> getProjectLogs(ProjectDTO project);
}
