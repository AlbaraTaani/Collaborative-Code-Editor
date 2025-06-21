package com.collaborative.editor.repository;

import com.collaborative.editor.model.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoRepository extends JpaRepository<Repo, String> {

    @Query("SELECT r FROM Repo r  WHERE r.repoId = :repoId")
    Optional<Repo> findByRepoId(@Param("repoId") String repoId);

    @Modifying
    @Query("DELETE FROM Repo r WHERE r.repoId = :repoId")
    void deleteByRepoId(@Param("repoId") String repoId);

}
