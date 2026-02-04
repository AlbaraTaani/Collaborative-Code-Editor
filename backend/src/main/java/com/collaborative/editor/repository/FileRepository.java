package com.collaborative.editor.repository;

import com.collaborative.editor.model.File;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String> {

        @Query("{ 'projectName' : ?0, 'filename' :?1, 'repoId' : ?2 }")
        Optional<File> findByFileNameProjectNameAndRepoId(@Param("projectName") String projectName,
                        @Param("filename") String filename,
                        @Param("repoId") String repoId);

        @Query("{ 'projectName' :?0, 'repoId' :?1 }")
        Optional<List<File>> findByProjectNameAndRepoId(@Param("projectName") String projectName,
                        @Param("repoId") String repoId);

        @Modifying
        @Update(update = "{ 'filename' : ?0, 'projectName' : ?1, 'repoId' : ?2, 'content' : ?3, 'createdAt' : ?4, 'lastModifiedAt' : ?5, 'extension': ?6 }")
        @Query(value = "{ 'filename' : ?0, 'projectName' : ?1, 'repoId' : ?2}")
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        void upsertFileContent(@Param("filename") String filename,
                        @Param("projectName") String projectName,
                        @Param("repoId") String repoId,
                        @Param("content") String content,
                        @Param("createdAt") Long createdAt,
                        @Param("lastModifiedAt") Long lastModifiedAt,
                        @Param("extension") String extension);

}
