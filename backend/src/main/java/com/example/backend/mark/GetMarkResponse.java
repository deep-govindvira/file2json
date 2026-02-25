package com.example.backend.mark;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetMarkResponse {
    private String id;
    private String subjectCode;
    private String subjectName;
    private String subjectGrade;
    private Integer subjectOutOfMarks;
    private Integer obtained;
    private String obtainedInWords;
    private String corrected;
}
