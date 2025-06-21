package com.collaborative.editor.service.projectService;

import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.repository.FileRepository;
import com.collaborative.editor.repository.ProjectRepository;
import com.collaborative.editor.repository.RepoRepository;
import com.collaborative.editor.exception.RepositoryNotFoundException;
import com.collaborative.editor.model.Project;
import com.collaborative.editor.service.impls.ProjectServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProjectServiceImplTest {

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RepoRepository repoRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private HttpServletResponse response;

    private ProjectDTO projectDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectDTO = new ProjectDTO();
        projectDTO.setProjectName("Test Project");
        projectDTO.setRepoId("123");
    }

    @Test
    void testCreateProject_RepoNotFound() {
        when(repoRepository.findByRepoId(projectDTO.getRepoId())).thenReturn(Optional.empty());

        assertThrows(RepositoryNotFoundException.class, () -> projectService.createProject(projectDTO));
    }

    @Test
    void testDeleteProject_Success() {
        Project project = new Project();
        when(projectRepository.findByRepoIdAndProjectName(projectDTO.getRepoId(), projectDTO.getProjectName()))
                .thenReturn(Optional.of(project));

        projectService.deleteProject(projectDTO);

        verify(projectRepository, times(1)).delete(project);
    }

}
