package com.collaborative.editor.service;

import com.collaborative.editor.model.File;
import java.util.List;

import java.util.List;

public interface FileVersionService {
    List<File> findByRepoIdOrderByLastModifiedAtDesc(String repoId);
}
