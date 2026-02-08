package com.example.backend.gseb;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GSEBConverter {
    public static GSEB toGSEB(StructuredGSEBResponse r) {
        GSEB gseb = GSEB.builder()
                .board(r.getBoard())
                .monthAndYearOfExam(r.getMonthAndYearOfExam())
                .seatNo(r.getSeatNo())
                .centreNo(r.getCentreNo())
                .schoolIndexNo(r.getSchoolIndexNo())
                .groupName(r.getGroupName())
                .studentName(r.getStudentName())
                .total(r.getTotal())
                .obtained(r.getObtained())
                .obtainedInWords(r.getObtainedInWords())
                .result(r.getResult())
                .corrected(r.getCorrected())
                .date(r.getDate() == null ? null :
                        LocalDate.parse(r.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .build();

        if (r.getGsebSubjects() != null) {
            for (StructuredGSEBSubject s : r.getGsebSubjects()) {

                GSEBSubject subject = GSEBSubject.builder()
                        .subCode(s.getSubCode())
                        .subject(s.getSubject())
                        .total(s.getTotal())
                        .obtained(s.getObtained())
                        .obtainedInWords(s.getObtainedInWords())
                        .grade(s.getGrade())
                        .gseb(gseb) // IMPORTANT (owning side)
                        .build();

                gseb.getGsebSubjects().add(subject);
            }
        }

        return gseb;
    }
}
