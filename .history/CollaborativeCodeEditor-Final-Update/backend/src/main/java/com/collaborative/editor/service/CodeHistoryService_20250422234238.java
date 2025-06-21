package com.collaborative.editor.service;

import com.collaborative.editor.model.File;
import java.util.List;

public interface CodeHistoryService {
    List<File> findByRepoIdOrderByLastModifiedAtDesc(String repoId);
    List<File> findByRepoIي(String repoId);
}