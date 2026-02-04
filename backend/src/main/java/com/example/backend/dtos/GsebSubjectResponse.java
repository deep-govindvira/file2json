package com.example.backend.dtos;

import lombok.Data;

@Data
public class GsebSubjectResponse {
    private String subCode;
    private String subject;
    private Integer total;
    private Integer obtained;
    private String obtainedInWords;
    private String grade;
}

