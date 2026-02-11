package com.example.backend.gseb;

import lombok.Data;

import java.util.List;

@Data
public class StructuredGSEBResponse {
    private String board;
    private String monthAndYearOfExam;
    private String seatNo;
    private String centreNo;
    private String schoolIndexNo;
    private String groupName;
    private String studentName;

    private List<StructuredGSEBSubjectResponse> gsebSubjects;

    private int total;
    private int obtained;
    private String obtainedInWords;

    private String date;
    private String result;
    private String corrected;

}
