package com.example.backend.mark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMarkRequest {
    private String id;
    private String subjectCode;
    private String subjectName;
    private String subjectGrade;
    private Integer subjectOutOfMarks;
    private Integer obtained;
    private String obtainedInWords;
    private String corrected;
}
