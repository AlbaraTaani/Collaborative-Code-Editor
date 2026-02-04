package com.collaborative.editor.repository;

import com.collaborative.editor.model.MessageLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogsRepository extends MongoRepository<MessageLog, Long> {

        @Query("{ 'type' : ?0, repoId: ?1 }")
        Optional<List<MessageLog>> findByType(@Param("type") String type, @Param("repoId") String repoId);

        @Query("{ 'repoId' : ?0 }")
        Optional<List<MessageLog>> findByRepoId(@Param("repoId") String repoId);

        @Query("{ 'sender' : ?0 }")
        Optional<List<MessageLog>> findBySenderEmail(@Param("sender") String sender);

        @Query("{ 'projectName' : ?0, 'filename' :?1, 'repoId' : ?2 }")
        Optional<List<MessageLog>> findByFileNameProjectNameAndRepoId(@Param("projectName") String projectName,
                        @Param("fileName") String filename,
                        @Param("repoId") String repoId);

        @Query("{ 'projectName' : ?0, 'repoId' :?1 }")
        Optional<List<MessageLog>> findByProjectNameAndRepoId(@Param("projectName") String projectName,
                        @Param("repoId") String repoId);
}
