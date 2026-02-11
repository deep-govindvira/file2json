package com.example.backend.icse;

import lombok.Data;

@Data
public class StructuredICSESubjectResponse {
    private String subject;
    private int percentageMarks;
    private String percentageMarksInWords;
}
