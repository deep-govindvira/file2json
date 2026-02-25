package com.example.backend.mark;

import org.springframework.stereotype.Component;

@Component
public class MarkConverter {
    public GetMarkResponse getMarksheetResponse(Mark mark) {
        return GetMarkResponse.builder()
                .corrected(mark.getCorrected())
                .id(mark.getId())
                .obtained(mark.getObtained())
                .obtainedInWords(mark.getObtainedInWords())
                .subjectCode(mark.getSubjectCode())
                .subjectGrade(mark.getSubjectGrade())
                .subjectName(mark.getSubjectName())
                .subjectOutOfMarks(mark.getSubjectOutOfMarks())
                .build();
    }
}
