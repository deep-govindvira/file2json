package com.example.backend.cbse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CBSEConverter {
    public static CBSE toCBSE(StructuredCBSEResponse r) {
        CBSE cbse = CBSE.builder()
                .corrected(r.getCorrected())
                .date(r.getDate() == null ? null :
                        LocalDate.parse(r.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .fatherName(r.getFatherName())
                .motherName(r.getMotherName())
                .rollNo(r.getRollNo())
                .result(r.getResult())
                .school(r.getSchool())
                .studentName(r.getStudentName())
                .build();

        if (r.getCbseSubjects() != null) {
            for (StructuredCBSESubjectResponse s : r.getCbseSubjects()) {
                CBSEMark subject = CBSEMark.builder()
                        .subCode(s.getSubCode())
                        .subject(s.getSubject())
                        .total(s.getTotal())
                        .positionalGrade(s.getPositionalGrade())
                        .practical(s.getPractical())
                        .theory(s.getTheory())
                        .totalInWords(s.getTotalInWords())
                        .cbse(cbse)
                        .build();

                cbse.getCbseMarks().add(subject);
            }
        }

        return cbse;
    }
}
