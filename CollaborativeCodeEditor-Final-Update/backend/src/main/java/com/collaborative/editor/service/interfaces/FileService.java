package com.collaborative.editor.service.interfaces;

import com.collaborative.editor.dto.FileDTO;
import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.exception.FileConflictException;
import com.collaborative.editor.model.File;


import java.util.List;

public interface FileService {
    List<FileDTO> getFiles(ProjectDTO project);

    void createFile(FileDTO fileDTO) throws FileConflictException;

    void pushFileContent(File file);

    File mergeFileContent(File newVersion) throws FileConflictException;

    File pullFileContent(FileDTO fileDTO);
}
