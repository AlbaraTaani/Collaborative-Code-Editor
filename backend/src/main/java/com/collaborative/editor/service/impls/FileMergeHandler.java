package com.collaborative.editor.service.impls;

import com.collaborative.editor.model.File;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class FileMergeHandler {

    private static final String HEADER_MESSAGE = """
        
        // resolve these conflicts manually.
        
        """;

    public File mergeFileContent(File oldFile, String newFile) {
        String oldContent = oldFile.getContent();
        String[] oldLines = splitLines(oldContent);
        String[] newLines = splitLines(newFile);

        String mergedContent = mergeContentWithConflictMarkers(oldLines, newLines);
        oldFile.setContent(mergedContent);

        return oldFile;
    }

    private String[] splitLines(String content) {
        return content.split("\n");
    }

    private String mergeContentWithConflictMarkers(String[] oldLines, String[] newLines) {
        StringBuilder mergedContent = new StringBuilder();
        mergedContent.append(HEADER_MESSAGE).append("\n");

        if (Arrays.equals(oldLines, newLines)) {
            appendLines(mergedContent, oldLines);
        } else {
            appendConflictBlock(mergedContent, oldLines, newLines);
        }

        return mergedContent.toString();
    }

    private void appendLines(StringBuilder sb, String[] lines) {
        for (String line : lines) {
            sb.append(line).append("\n");
        }
    }

    private void appendConflictBlock(StringBuilder sb, String[] oldLines, String[] newLines) {
        sb.append("<<<<<<< Current Version\n");
        appendLines(sb, oldLines);
        sb.append("=======\n");
        appendLines(sb, newLines);
        sb.append(">>>>>>> Incoming Version\n");
    }
}