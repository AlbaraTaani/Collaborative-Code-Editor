package com.collaborative.editor.repository;

import com.collaborative.editor.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query("SELECT r FROM Project r JOIN r.repo v WHERE v.repoId = :repoId")
    Optional<List<Project>> findByRepoId(@Param("repoId") String repoId);

    @Query("SELECT r FROM Project r WHERE r.name = :projectName")
    Optional<Project> findByProjectName(@Param("projectName") String projectName);

    @Query("SELECT r FROM Project r JOIN r.repo v WHERE v.repoId = :repoId AND r.name = :projectName")
    Optional<Project> findByRepoIdAndProjectName(@Param("repoId") String repoId,
            @Param("projectName") String projectName);

}
