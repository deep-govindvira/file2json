package com.example.backend.marksheet;

public class MarksheetConverter {
    public static SaveMarksheetResponse toCreateMarksheetResponse(Marksheet marksheet) {
        return SaveMarksheetResponse.builder()
                .id(marksheet.getId())
                .filePath(marksheet.getFilePath())
                .status(marksheet.getStatus())
                .build();
    }
}
