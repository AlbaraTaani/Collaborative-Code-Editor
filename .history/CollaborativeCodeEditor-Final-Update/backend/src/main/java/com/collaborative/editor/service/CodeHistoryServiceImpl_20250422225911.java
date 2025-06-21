package com.collaborative.editor.service;

import com.collaborative.editor.model.File;
import com.collaborative.editor.repository.FileVersionRepository;
import com.collaborative.editor.service.CodeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodeHistoryServiceImpl implements CodeHistoryService {

    private final FileVersionRepository repository;

    @Autowired
    public CodeHistoryServiceImpl(FileVersionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<File> findByRepoIdOrderByLastModifiedAtDesc(String repoId) {
        return repository.findByRepoIdOrderByLastModifiedAtDesc(repoId);
    }
}