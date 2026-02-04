package com.collaborative.editor.service.LogsService;

import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.repository.LogsRepository;
import com.collaborative.editor.model.MessageLog;
import com.collaborative.editor.service.impls.LogsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LogsServiceImplTest {

    @InjectMocks
    private LogsServiceImpl logsService;

    @Mock
    private LogsRepository logsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveLog() {
        MessageLog log = new MessageLog("user1", "developer", "file1", "repo1", "project1", "log content", 123L,
                "action");

        when(logsRepository.save(log)).thenReturn(log);

        assertDoesNotThrow(() -> logsService.saveLog(log));
    }

    @Test
    void testGetLogsByActionType() {
        String type = "action";
        String repoId = "repo1";
        List<MessageLog> mockLogs = List
                .of(new MessageLog("user1", "developer", "file1", repoId, "project1", "log content", 123L, type));

        when(logsRepository.findByType(type, repoId)).thenReturn(Optional.of(mockLogs));

        Optional<List<MessageLog>> result = logsService.getLogsByActionType(type, repoId);

        assertTrue(result.isPresent());
        assertEquals(mockLogs, result.get());
    }

    @Test
    void testGetLogsByRepoId() {
        String repoId = "repo1";
        List<MessageLog> mockLogs = List
                .of(new MessageLog("user1", "developer", "file1", repoId, "project1", "log content", 123L, "action"));

        when(logsRepository.findByRepoId(repoId)).thenReturn(Optional.of(mockLogs));

        Optional<List<MessageLog>> result = logsService.getLogsByRepoId(repoId);

        assertTrue(result.isPresent());
        assertEquals(mockLogs, result.get());
    }

    @Test
    void testGetCollaboratorLogs() {
        String sender = "user1";
        List<MessageLog> mockLogs = List
                .of(new MessageLog(sender, "developer", "file1", "repo1", "project1", "log content", 123L, "action"));

        when(logsRepository.findBySenderEmail(sender)).thenReturn(Optional.of(mockLogs));

        Optional<List<MessageLog>> result = logsService.getCollaboratorLogs(sender);

        assertTrue(result.isPresent());
        assertEquals(mockLogs, result.get());
    }

    @Test
    void testGetFileLogs() {
        String projectName = "project1";
        String repoId = "repo1";
        String filename = "file1";
        List<MessageLog> mockLogs = List
                .of(new MessageLog("user1", "developer", filename, repoId, projectName, "log content", 123L, "action"));

        when(logsRepository.findByFileNameProjectNameAndRepoId(projectName, filename, repoId))
                .thenReturn(Optional.of(mockLogs));

        Optional<List<MessageLog>> result = logsService.getFileLogs(projectName, repoId, filename);

        assertTrue(result.isPresent());
        assertEquals(mockLogs, result.get());
    }

    @Test
    void testGetProjectLogs() {
        String repoId = "repo1";
        String projectName = "project1";
        ProjectDTO project = new ProjectDTO(projectName, repoId);
        List<MessageLog> mockLogs = List
                .of(new MessageLog("user1", "developer", "file1", repoId, projectName, "log content", 123L, "action"));

        when(logsRepository.findByProjectNameAndRepoId(projectName, repoId)).thenReturn(Optional.of(mockLogs));

        Optional<List<MessageLog>> result = logsService.getProjectLogs(project);

        assertTrue(result.isPresent());
        assertEquals(mockLogs, result.get());
    }
}
