package com.example.backend.cbse;

import lombok.Data;

import java.util.List;

@Data
public class CbseResponse {
    private String board;
    private String studentName;
    private Long rollNo;
    private String motherName;
    private String fatherName;
    private String school;
    private List<CbseMarkResponse> marks;
    private String result;
    private String date;
    private String corrected;
}
