package com.example.backend.cbse;

import lombok.Data;

import java.util.List;

@Data
public class StructuredCBSEResponse {
    private String board;
    private String studentName;
    private Integer rollNo;
    private String motherName;
    private String fatherName;
    private String school;
    private List<StructuredCBSESubjectResponse> cbseSubjects;
    private String result;
    private String date;
    private String corrected;
}

