package com.collaborative.editor.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeMetricsRequest {
    private String code;
    private String language;
}
