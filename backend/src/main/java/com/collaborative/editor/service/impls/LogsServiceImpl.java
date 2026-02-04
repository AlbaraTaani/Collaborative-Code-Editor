package com.collaborative.editor.service.impls;

import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.repository.LogsRepository;
import com.collaborative.editor.model.MessageLog;
import com.collaborative.editor.service.interfaces.LogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("LogsServiceImpl")
public class LogsServiceImpl implements LogsService {

    @Autowired
    private LogsRepository logsRepository;

    @Override
    public void saveLog(MessageLog log) {
        try {
            logsRepository.save(log);
        } catch (Exception e) {
            throw new RuntimeException("LOG NOT SAVED" + e.getMessage(), e);
        }
    }

    @Override
    public Optional<List<MessageLog>> getLogsByActionType(String type, String repoId) {
        try {
            return logsRepository.findByType(type, repoId);
        } catch (Exception e) {
            throw new RuntimeException("TYPE NOT FOUND" + e.getMessage(), e);
        }
    }

    @Override
    public Optional<List<MessageLog>> getLogsByRepoId(String repoId) {
        try {
            return logsRepository.findByRepoId(repoId);
        } catch (Exception e) {
            throw new RuntimeException("REPO NOT FOUND" + e.getMessage(), e);
        }
    }

    @Override
    public Optional<List<MessageLog>> getCollaboratorLogs(String sender) {
        try {
            return logsRepository.findBySenderEmail(sender);
        } catch (Exception e) {
            throw new RuntimeException("SENDER NOT FOUND" + e.getMessage(), e);
        }
    }

    @Override
    public Optional<List<MessageLog>> getFileLogs(String projectName, String repoId, String filename) {
        try {
            return logsRepository.findByFileNameProjectNameAndRepoId(projectName, filename, repoId);
        } catch (Exception e) {
            throw new RuntimeException("FILE NOT FOUND" + e.getMessage(), e);
        }
    }

    @Override
    public Optional<List<MessageLog>> getProjectLogs(ProjectDTO project) {
        try {
            return logsRepository.findByProjectNameAndRepoId(project.getProjectName(), project.getRepoId());
        } catch (Exception e) {
            throw new RuntimeException("PROJECT NOT FOUND" + e.getMessage(), e);
        }
    }
}