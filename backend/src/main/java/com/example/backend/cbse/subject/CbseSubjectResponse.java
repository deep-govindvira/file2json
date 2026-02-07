package com.example.backend.cbse.subject;

import lombok.Data;

@Data
public class CbseSubjectResponse {
    private String subCode;
    private String subject;
    private Integer theory;
    private Integer practical;
    private Integer total;
    private String totalInWords;
    private String positionalGrade;
}
