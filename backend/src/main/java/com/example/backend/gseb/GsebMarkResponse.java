package com.example.backend.gseb;

import com.example.backend.Correction;
import lombok.Data;

import java.util.List;

@Data
public class GsebMarkResponse {
    private String subCode;
    private String subject;
    private Integer total;
    private Integer obtained;
    private String obtainedInWords;
    private String grade;
    private List<Correction> corrected;
}
