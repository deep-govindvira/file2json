package com.example.backend.icse;

import lombok.Data;

import java.util.List;

@Data
public class IcseResponse {
    private String board;
    private String studentName;
    private String school;
    private Long uniqueId;
    private String motherName;
    private String fatherName;
    private List<IcseMarkResponse> marks;
    private String dateOfBirth;
    private String result;
    private String date;
    private String corrected;
}