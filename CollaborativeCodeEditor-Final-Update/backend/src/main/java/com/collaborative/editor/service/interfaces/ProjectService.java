package com.collaborative.editor.service.interfaces;

import com.collaborative.editor.dto.ProjectDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface ProjectService {

    void createProject(ProjectDTO project) throws NoSuchFieldException;

    List<ProjectDTO> getProjects(String repoId);

    void deleteProject(ProjectDTO projectDTO) throws NoSuchMethodException;

    void downloadProject(ProjectDTO projectDTO, HttpServletResponse response) throws IOException;
}
