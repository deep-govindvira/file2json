package com.example.backend.cbse.subject;

public class CbseSubjectConverter {
    public static CbseSubject toCbseSubject(CbseSubjectResponse cbseSubjectResponse) {
        return CbseSubject.builder()
                .subCode(cbseSubjectResponse.getSubCode())
                .subject(cbseSubjectResponse.getSubject())
                .theory(cbseSubjectResponse.getTheory())
                .practical(cbseSubjectResponse.getPractical())
                .total(cbseSubjectResponse.getTotal())
                .totalInWords(cbseSubjectResponse.getTotalInWords())
                .positionalGrade(cbseSubjectResponse.getPositionalGrade())
                .build();
    }
}
