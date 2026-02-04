package com.collaborative.editor.repository;

import com.collaborative.editor.model.Repo;
import com.collaborative.editor.model.RepoRole;
import com.collaborative.editor.model.RepoMembership;
import com.collaborative.editor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepoMembershipRepository extends JpaRepository<RepoMembership, String> {

        @Query("SELECT rm FROM RepoMembership rm WHERE rm.repo = :repo AND rm.user = :user")
        Optional<List<RepoMembership>> findRoles(@Param("repo") Repo repo,
                        @Param("user") User user);

        @Query("SELECT rm FROM RepoMembership rm WHERE rm.repo = :repo AND rm.user = :user")
        Optional<RepoMembership> findByRepoAndUser(Repo repo, User user);

        @Query("SELECT rm FROM RepoMembership rm WHERE rm.user.email = :username")
        Optional<List<RepoMembership>> findByUser(@Param("username") String username);

        @Query("SELECT rm FROM RepoMembership rm WHERE rm.repo = :repo AND rm.user = :user AND rm.role = :role")
        Optional<RepoMembership> findByRole(@Param("repo") Repo repo,
                        @Param("user") User user,
                        @Param("role") RepoRole role);
}
