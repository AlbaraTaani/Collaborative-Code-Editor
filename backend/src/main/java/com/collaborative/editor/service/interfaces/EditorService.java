package com.collaborative.editor.service.interfaces;



import com.collaborative.editor.dto.CodeMetrics;
import com.collaborative.editor.model.CodeUpdate;

public interface EditorService {
    CodeMetrics calculateMetrics(String code, String language);
    CodeUpdate insertComment(CodeUpdate codeUpdate);
    void saveCodeUpdate(CodeUpdate codeUpdate);
}
