package com.collaborative.editor.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRepoRequest {
    private String memberEmail;
    private String repoName;
}
