package com.example.backend.gseb;

import lombok.Data;

@Data
public class GsebMarkResponse {
    private String subCode;
    private String subject;
    private Integer total;
    private Integer obtained;
    private String obtainedInWords;
    private String grade;
}
