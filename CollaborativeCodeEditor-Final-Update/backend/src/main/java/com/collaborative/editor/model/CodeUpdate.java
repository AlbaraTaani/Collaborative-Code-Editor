package com.collaborative.editor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "codeUpdate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(def = "{'filename': 1, 'projectName': 1, 'repoId': 1}", unique = true)
public class CodeUpdate {
    private String userId;
    private String filename;
    private String repoId;
    private String projectName;
    private String lineNumber;
    private String column;
    private String lineContent;
    private String code;
}
