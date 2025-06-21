package com.collaborative.editor.service;


import com.collaborative.editor.model.File;
import com.collaborative.editor.repository.FileVersionRepository;
import com.collaborative.editor.service.FileVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileVersionServiceImpl implements FileVersionService {

    private final FileVersionRepository repository;

    @Autowired
    public FileVersionServiceImpl(FileVersionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<File> findByRepoIdOrderByLastModifiedAtDesc(String repoId) {
        return repository.findByRepoIdOrderByLastModifiedAtDesc(repoId);
    }
}


6807f0157faa99664baeb3fa Main-version p2 2e153615-e914-4568-9358-c5a1b8b9aadc print("fjladsf;adsf") #fdasfl;dashgfdhghfghgfgfh… 1745350677170 1745350732354 .txt