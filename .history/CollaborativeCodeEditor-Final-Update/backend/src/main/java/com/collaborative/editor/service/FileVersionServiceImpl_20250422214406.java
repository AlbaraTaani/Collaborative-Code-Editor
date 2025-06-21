package com.collaborative.editor.service;

com.collaborative.editor.service.impl;

import com.collaborative.editor.model.File;
import com.collaborative.editor.repository.FileVersionRepository;
import com.collaborative.editor.service.FileVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileVersionServiceImpl implements FileVersionService {
private final FileVersionRepository repo;

@Autowired
public FileVersionServiceImpl(FileVersionRepository repo) {
    this.repo = repo;
}

@Override
public List<File> findByRepoIdOrderByLastModifiedAtDesc(String repoId) {
    return repo.findByRepoIdOrderByLastModifiedAtDesc(repoId);
}

}

