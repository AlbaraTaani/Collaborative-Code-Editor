package com.collaborative.editor.service.fileService;

import com.collaborative.editor.dto.FileDTO;
import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.repository.FileRepository;
import com.collaborative.editor.model.File;
import com.collaborative.editor.service.impls.FileMergeHandler;
import com.collaborative.editor.service.impls.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileMergeHandler fileMergeHandler;

    private FileDTO fileDTO;
    private File file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileDTO = new FileDTO("test.txt", "repo1", "project1", ".txt");
        file = new File("test.txt", "repo1", "project1", "File content", System.currentTimeMillis(),
                System.currentTimeMillis(), ".txt");
    }

    @Test
    void getFiles() {
        when(fileRepository.findByProjectNameAndRepoId(anyString(), anyString()))
                .thenReturn(Optional.of(Collections.singletonList(file)));

        var files = fileService.getFiles(new ProjectDTO("project1", "repo1"));

        assertEquals(1, files.size());
        assertEquals("test.txt", files.get(0).getFilename());
    }

    @Test
    void createFile() throws Exception {
        when(fileRepository.findByFileNameProjectNameAndRepoId(anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        fileService.createFile(fileDTO);

        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    void mergeFileContent() {

        when(fileRepository.findByFileNameProjectNameAndRepoId(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(file));
        when(fileMergeHandler.mergeFileContent(any(), anyString())).thenReturn(file);

        var result = fileService.mergeFileContent(file);

        assertEquals(file, result);
    }

}
