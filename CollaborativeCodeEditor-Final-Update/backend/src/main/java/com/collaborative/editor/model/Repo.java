package com.collaborative.editor.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "repos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repo {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, unique = true)
    private String repoId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepoMembership> repoMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL)
    private Set<Project> projects = new HashSet<>();

}
