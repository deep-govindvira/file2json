package com.example.backend.marksheet;

public class MarksheetConverter {
    public static SaveMarksheetResponse toCreateMarksheetResponse(Marksheet marksheet) {
        return SaveMarksheetResponse.builder()
                .id(marksheet.getMarksheet_id())
                .name(marksheet.getName())
                .status(marksheet.getStatus())
                .build();
    }

    public static ProcessMarksheetResponse toProcessMarksheetResponse(Marksheet marksheet) {
        return ProcessMarksheetResponse.builder()
                .id(marksheet.getMarksheet_id())
                .name(marksheet.getName())
                .status(marksheet.getStatus())
                .build();
    }
}
