package com.collaborative.editor.service.impls;

import com.collaborative.editor.dto.FileDTO;
import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.repository.FileRepository;
import com.collaborative.editor.repository.ProjectRepository;
import com.collaborative.editor.repository.RepoRepository;
import com.collaborative.editor.model.File;
import com.collaborative.editor.model.Project;
import com.collaborative.editor.model.Repo;
import com.collaborative.editor.exception.RepositoryNotFoundException;
import com.collaborative.editor.exception.ProjectNotFoundException;
import com.collaborative.editor.service.interfaces.FileService;
import com.collaborative.editor.service.interfaces.ProjectService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.stream.Collectors;

@Service("ProjectServiceImpl")
public class ProjectServiceImpl implements ProjectService {

    private static final String MAIN_VERSION = "Main-version";
    private static final String DEFAULT_EXTENSION = ".txt";

    private final Map<String, Object> projectLocks = new ConcurrentHashMap<>();

    @Autowired
    private FileService fileService;

    @Autowired
    private ProjectDownloadService projectDownloadService;

    private final ProjectRepository projectRepository;
    private final RepoRepository repoRepository;
    private final FileRepository fileRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
            RepoRepository repoRepository,
            FileRepository fileRepository) {
        this.projectRepository = projectRepository;
        this.repoRepository = repoRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    @Transactional
    public void createProject(ProjectDTO project) {
        Repo repo = repoRepository.findByRepoId(project.getRepoId())
                .orElseThrow(() -> new RepositoryNotFoundException("Repo not found for ID " + project.getRepoId()));

        Project newProject = buildNewProject(project, repo);
        try {
            projectRepository.save(newProject);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create project: " + e.getMessage());
        }

        createDefaultFile(project);
    }

    private Project buildNewProject(ProjectDTO project, Repo repo) {

        return Project.builder()
                .name(project.getProjectName())
                .repo(repo)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .description("")
                .build();
    }

    private void createDefaultFile(ProjectDTO project) {
        try {
            fileService.createFile(
                    FileDTO
                            .builder()
                            .filename(MAIN_VERSION)
                            .projectName(project.getProjectName())
                            .repoId(project.getRepoId())
                            .extension(DEFAULT_EXTENSION)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create default file: " + e.getMessage());
        }
    }

    @Override
    public void deleteProject(ProjectDTO projectDTO) {
        String fileKey = projectDTO.getRepoId() + "-" + projectDTO.getProjectName();

        synchronized (getProjectLock(fileKey)) {
            try {
                Project project = projectRepository.findByRepoIdAndProjectName(
                        projectDTO.getRepoId(),
                        projectDTO.getProjectName()).orElseThrow(
                                () -> new ProjectNotFoundException(
                                        "Project not found for repo ID " + projectDTO.getRepoId()));

                projectRepository.delete(project);
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete project.", e);
            }
        }
    }

    @Override
    public List<ProjectDTO> getProjects(String repoId) {
        Repo repo = repoRepository.findByRepoId(repoId)
                .orElseThrow(() -> new RepositoryNotFoundException("Repo not found for ID " + repoId));

        return repo.getProjects().stream()
                .map(project -> new ProjectDTO(project.getName(), repoId))
                .collect(Collectors.toList());
    }

    @Override
    public void downloadProject(ProjectDTO projectDTO, HttpServletResponse response) throws IOException {

        List<File> files = findFilesByProjectAndRepo(
                projectDTO.getProjectName(),
                projectDTO.getRepoId());

        projectDownloadService.downloadProjectFiles(
                projectDTO.getProjectName(),
                files,
                response);
    }


    private List<File> findFilesByProjectAndRepo(String projectName, String repoId) {
        return fileRepository.findByProjectNameAndRepoId(projectName, repoId)
                .filter(files -> !files.isEmpty())
                .orElseThrow(() -> new ProjectNotFoundException("No files found for the project"));
    }

    private Object getProjectLock(String repoId) {
        return projectLocks.computeIfAbsent(repoId, k -> new Object());
    }
}
