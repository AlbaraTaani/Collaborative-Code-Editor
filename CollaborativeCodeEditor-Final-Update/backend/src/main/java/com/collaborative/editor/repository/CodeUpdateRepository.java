package com.collaborative.editor.repository;

import com.collaborative.editor.model.CodeUpdate;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeUpdateRepository extends MongoRepository<CodeUpdate, String> {

        @Query("{ 'projectName' : ?0, 'filename' :?1, 'repoId' : ?2 }")
        Optional<CodeUpdate> findByFileNameProjectNameAndRepoId(@Param("projectName") String projectName,
                        @Param("filename") String filename,
                        @Param("repoId") String repoId);

        @Modifying
        @Update(update = "{ 'filename' : ?0, 'projectName' : ?1, 'repoId' : ?2, 'code' : ?3, 'userId' : ?4, 'lineNumber' : ?5, 'column': ?6, 'lineContent': ?7 }")
        @Query(value = "{ 'filename' : ?0, 'projectName' : ?1, 'repoId' : ?2}")
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        void updateCode(@Param("filename") String filename,
                        @Param("projectName") String projectName,
                        @Param("repoId") String repoId,
                        @Param("code") String code,
                        @Param("userId") String userId,
                        @Param("lineNumber") String lineNumber,
                        @Param("column") String column,
                        @Param("lineContent") String lineContent);
}
