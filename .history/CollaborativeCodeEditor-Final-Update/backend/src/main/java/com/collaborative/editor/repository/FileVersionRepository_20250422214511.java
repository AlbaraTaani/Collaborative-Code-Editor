package com.collaborative.editor.repository;
import com.collaborative.editor.model.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FileVersionRepository extends MongoRepository<File, String> {
List findByRepoIdOrderByLastModifiedAtDesc(String repoId);
}