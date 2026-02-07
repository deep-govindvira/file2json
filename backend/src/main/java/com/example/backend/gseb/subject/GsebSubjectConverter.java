package com.example.backend.gseb.subject;

public class GsebSubjectConverter {
    public static GsebSubject toGsebSubject(GsebSubjectResponse gsebSubjectResponse) {
        return GsebSubject.builder()
                .subCode(gsebSubjectResponse.getSubCode())
                .subjectName(gsebSubjectResponse.getSubject())
                .totalMarks(gsebSubjectResponse.getTotal())
                .obtainedMarks(gsebSubjectResponse.getObtained())
                .obtainedInWords(gsebSubjectResponse.getObtainedInWords())
                .grade(gsebSubjectResponse.getGrade())
                .build();
    }
}
