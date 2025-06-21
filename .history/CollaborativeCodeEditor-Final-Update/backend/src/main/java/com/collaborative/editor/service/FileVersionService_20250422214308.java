package com.collaborative.editor.service.FileVersionService

import com.collaborative.editor.model.File;
import java.util.List;

public interface FileVersionService {
    List findByRepoIdOrderByLastModifiedAtDesc(String repoId);
}

