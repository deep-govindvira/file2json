package com.example.backend.gseb;

import com.example.backend.Correction;
import lombok.Data;

import java.util.List;

@Data
public class GsebResponse {
    private String board;
    private String monthAndYearOfExam;
    private String seatNo;
    private String centreNo;
    private String schoolIndexNo;
    private String groupName;
    private String studentName;

    private List<GsebMarkResponse> marks;

    private Integer total;
    private Integer obtained;
    private String obtainedInWords;
    private String overAllGrade;
    private String date;
    private Double sciencePercentileRank;
    private String result;
    private List<Correction> corrected;
}
