package com.collaborative.editor.service.impls;

import com.collaborative.editor.dto.FileDTO;
import com.collaborative.editor.dto.ProjectDTO;
import com.collaborative.editor.exception.FileConflictException;
import com.collaborative.editor.model.CodeUpdate;
import com.collaborative.editor.repository.CodeUpdateRepository;
import com.collaborative.editor.repository.FileRepository;
import com.collaborative.editor.model.File;
import com.collaborative.editor.service.interfaces.FileService;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service("FileServiceImpl")
public class FileServiceImpl implements FileService {

    private static final String DEFAULT_CONTENT_FILE = "// choose language and start coding";
    private final FileRepository fileVersionRepository;
    private final FileMergeHandler fileMergeHandler;
    private final CodeUpdateRepository codeUpdateRepository;

    private final Map<String, Object> fileLocks = new ConcurrentHashMap<>();

    @Autowired
    public FileServiceImpl(FileRepository fileVersionRepository,
            FileMergeHandler fileMergeHandler,
            CodeUpdateRepository codeUpdateRepository) {
        this.fileVersionRepository = fileVersionRepository;
        this.fileMergeHandler = fileMergeHandler;
        this.codeUpdateRepository = codeUpdateRepository;
    }

    @Override
    public List<FileDTO> getFiles(ProjectDTO project) {
        List<File> files = fileVersionRepository
                .findByProjectNameAndRepoId(project.getProjectName(), project.getRepoId())
                .orElseThrow(() -> new RuntimeException("No files found for project " + project.getProjectName()));

        return files.stream()
                .map(file -> FileDTO
                        .builder()
                        .filename(file.getFilename())
                        .repoId(file.getRepoId())
                        .projectName(file.getProjectName())
                        .extension(file.getExtension())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createFile(FileDTO fileDTO) throws FileConflictException {

        try {

            File file = File
                    .builder()
                    .filename(fileDTO.getFilename())
                    .repoId(fileDTO.getRepoId())
                    .projectName(fileDTO.getProjectName())
                    .extension(fileDTO.getExtension())
                    .createdAt(System.currentTimeMillis())
                    .content(DEFAULT_CONTENT_FILE)
                    .lastModifiedAt(System.currentTimeMillis())
                    .build();

            if (checkExistingFile(file).isEmpty()) {
                fileVersionRepository.save(file);
            } else {
                throw new FileConflictException("File already exists with the name " + fileDTO.getFilename());
            }

        } catch (OptimisticLockException e) {
            throw new RuntimeException("Failed to push content due to concurrent modification.", e);
        }

    }

    @Override
    @Transactional
    public void pushFileContent(File file) {
        String fileKey = file.getRepoId() + "-" + file.getProjectName() + "-" + file.getFilename();

        synchronized (getFileLock(fileKey)) {
            try {
                Optional<File> existingFile = checkExistingFile(file);

                if (existingFile.isPresent()) {
                    file.setLastModifiedAt(System.currentTimeMillis());
                    fileVersionRepository.upsertFileContent(
                            file.getFilename(),
                            file.getProjectName(),
                            file.getRepoId(),
                            file.getContent(),
                            file.getCreatedAt(),
                            file.getLastModifiedAt(),
                            file.getExtension());
                } else {
                    throw new RuntimeException("File not found for pushing content.");
                }
            } catch (OptimisticLockException e) {
                throw new RuntimeException("Failed to push content due to concurrent modification.", e);
            }
        }
    }

    @Override
    public File mergeFileContent(File newVersion) {
        String fileKey = newVersion.getRepoId() + "-" + newVersion.getProjectName() + "-" + newVersion.getFilename();

        synchronized (getFileLock(fileKey)) {
            File oldVersion = fileVersionRepository.findByFileNameProjectNameAndRepoId(
                    newVersion.getProjectName(),
                    newVersion.getFilename(),
                    newVersion.getRepoId()).orElseThrow(() -> new RuntimeException("Old version not found"));

            return fileMergeHandler.mergeFileContent(oldVersion, newVersion.getContent());
        }
    }

    @Override
    public File pullFileContent(FileDTO fileDTO) {

        Optional<CodeUpdate> codeUpdate = checkExistingCodeUpdate(fileDTO);

        if (codeUpdate.isEmpty()) {
            return fileVersionRepository.findByFileNameProjectNameAndRepoId(
                    fileDTO.getProjectName(),
                    fileDTO.getFilename(),
                    fileDTO.getRepoId()).orElseThrow(() -> new RuntimeException("File not found"));
        } else {
            File file = fileVersionRepository.findByFileNameProjectNameAndRepoId(
                    fileDTO.getProjectName(),
                    fileDTO.getFilename(),
                    fileDTO.getRepoId()).get();
            file.setContent(codeUpdate.get().getCode());
            return file;
        }

    }

    private Optional<File> checkExistingFile(File file) {
        return fileVersionRepository.findByFileNameProjectNameAndRepoId(
                file.getProjectName(),
                file.getFilename(),
                file.getRepoId());
    }

    private Optional<CodeUpdate> checkExistingCodeUpdate(FileDTO file) {
        return codeUpdateRepository.findByFileNameProjectNameAndRepoId(
                file.getProjectName(),
                file.getFilename(),
                file.getRepoId());
    }

    private Object getFileLock(String repoId) {
        return fileLocks.computeIfAbsent(repoId, k -> new Object());
    }

}
