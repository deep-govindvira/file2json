package com.example.backend.cbse.marksheet;

import com.example.backend.cbse.subject.CbseSubjectResponse;
import lombok.Data;

import java.util.List;

@Data
public class CbseMarksheetResponse {
    private String board;
    private String studentsName;
    private int rollNo;
    private String mothersName;
    private String fatherName;
    private String school;
    private List<CbseSubjectResponse> subjects;
    private String result;
    private String date;
    private String corrected;
}
