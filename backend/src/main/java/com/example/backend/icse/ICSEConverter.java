package com.example.backend.icse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ICSEConverter {
    public static ICSE toICSE(StructuredICSEResponse r) {
        ICSE icse = ICSE.builder()
                .corrected(r.getCorrected())
                .date(r.getDate() == null ? null :
                        LocalDate.parse(r.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .dateOfBirth(r.getDateOfBirth() == null ? null :
                        LocalDate.parse(r.getDateOfBirth(), DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .fatherName(r.getFatherName())
                .motherName(r.getMotherName())
                .result(r.getResult())
                .school(r.getSchool())
                .uniqueId(r.getUniqueId())
                .build();

        if (r.getSubjects() != null) {
            for (StructuredICSESubjectResponse s : r.getSubjects()) {

                ICSEMark subject = ICSEMark.builder()
                        .icse(icse)
                        .percentageMarks(s.getPercentageMarks())
                        .percentageMarksInWords(s.getPercentageMarksInWords())
                        .subject(s.getSubject())
                        .build();

                icse.getIcseMarks().add(subject);
            }
        }

        return icse;
    }
}
