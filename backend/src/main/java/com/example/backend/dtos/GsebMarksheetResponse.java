package com.example.backend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class GsebMarksheetResponse {
    private String board;
    private String monthAndYearOfExam;
    private String seatNo;
    private String centreNo;
    private String schoolIndexNo;
    private String group;
    private String studentsName;
    private List<GsebSubjectResponse> subjects;
    private Integer total;
    private Integer obtained;
    private String obtainedInWords;
    private String date;
    private String result;
    private String corrected;
}