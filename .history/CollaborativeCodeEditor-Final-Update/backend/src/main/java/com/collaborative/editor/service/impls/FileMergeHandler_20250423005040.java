// package com.collaborative.editor.service.versionControlService.fileService;


// import com.collaborative.editor.model.File;
// import org.springframework.stereotype.Component;

// @Component
// public class FileMergeHandler {

//     private static final String CONFLICT_WARNING_MESSAGE = """
//         /**
//          * ======================================================
//          * WARNING: MANUAL CONFLICT RESOLUTION REQUIRED
//          * ======================================================
//          * The programmer/user must resolve these conflicts manually, 
//          * ensuring the code is correct. After resolving the conflicts, 
//          * push the resolved version using the PUSH button in the interface.
//          *
//          * ======================================================
//          */
//         """;

//     public File mergeFileContent(File oldVersion, String newContent) {
//         String oldContent = oldVersion.getContent();
//         String[] oldLines = splitLines(oldContent);
//         String[] newLines = splitLines(newContent);

//         String mergedContent = mergeLinesWithConflictMarkers(oldLines, newLines);
//         oldVersion.setContent(mergedContent);

//         return oldVersion;
//     }


//     private String[] splitLines(String content) {
//         return content.split("\n");
//     }

//     private String mergeLinesWithConflictMarkers(String[] oldLines, String[] newLines) {
//         StringBuilder mergedContent = new StringBuilder();
//         mergedContent.append(CONFLICT_WARNING_MESSAGE).append("\n");

//         int oldIndex = 0, newIndex = 0;
//         while (oldIndex < oldLines.length && newIndex < newLines.length) {
//             String oldLine = oldLines[oldIndex];
//             String newLine = newLines[newIndex];

//             if (oldLine.equals(newLine)) {
//                 mergedContent.append(oldLine).append("\n");
//                 oldIndex++;
//                 newIndex++;
//             } else {
//                 appendConflict(mergedContent, oldLine, newLine);
//                 oldIndex++;
//                 newIndex++;
//             }
//         }

//         appendRemainingLines(mergedContent, oldLines, oldIndex);
//         appendRemainingLines(mergedContent, newLines, newIndex);

//         return mergedContent.toString();
//     }

//     private void appendConflict(StringBuilder mergedContent, String oldLine, String newLine) {
//         mergedContent.append("<<<<<<< Current Version\n")
//                 .append(oldLine).append("\n")
//                 .append("=======\n")
//                 .append(newLine).append("\n")
//                 .append(">>>>>>> Incoming Version\n");
//     }

//     private void appendRemainingLines(StringBuilder mergedContent, String[] lines, int index) {
//         while (index < lines.length) {
//             mergedContent.append(lines[index]).append("\n");
//             index++;
//         }
//     }
// }

package com.collaborative.editor.service.impls;

import com.collaborative.editor.model.File;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class FileMergeHandler {

    // notedol
    private static final String CONFLICT_WARNING_MESSAGE = """
        /**
         * resolve these conflicts manually.
         */
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
        mergedContent.append(CONFLICT_WARNING_MESSAGE).append("\n");

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