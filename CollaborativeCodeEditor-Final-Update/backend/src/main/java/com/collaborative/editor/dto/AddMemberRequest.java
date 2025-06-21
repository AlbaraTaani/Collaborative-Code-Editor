package com.collaborative.editor.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMemberRequest {
    private String repoId;
    private String memberEmail;
    private String role;
}
