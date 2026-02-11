package com.example.backend.gseb;

import lombok.Data;

@Data
public class StructuredGSEBSubjectResponse {
    private String subCode;
    private String subject;
    private int total;
    private int obtained;
    private String obtainedInWords;
    private String grade;
}
