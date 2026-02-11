package com.example.backend.cbse;

import lombok.Data;

@Data
public class StructuredCBSESubjectResponse {
    private String subCode;
    private String subject;
    private Integer theory;
    private Integer practical;
    private Integer total;
    private String totalInWords;
    private String positionalGrade;
}
