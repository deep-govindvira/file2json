package com.example.backend.converter;

import com.example.backend.dtos.GsebMarksheetResponse;
import com.example.backend.entity.GsebMarksheet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class GsebMarksheetConverter {
    public static GsebMarksheet toGsebMarksheet(GsebMarksheetResponse gsebMarksheetResponse) {
        return GsebMarksheet.builder()
                .board(Optional.ofNullable(gsebMarksheetResponse.getBoard()).orElse("Unknown Board"))
                .monthYearExam(gsebMarksheetResponse.getMonthAndYearOfExam())
                .seatNo(gsebMarksheetResponse.getSeatNo())
                .centreNo(gsebMarksheetResponse.getCentreNo())
                .schoolIndexNo(gsebMarksheetResponse.getSchoolIndexNo())
                .groupName(gsebMarksheetResponse.getGroup())
                .studentName(gsebMarksheetResponse.getStudentsName())
                .totalMarks(gsebMarksheetResponse.getTotal())
                .obtainedMarks(gsebMarksheetResponse.getObtained())
                .obtainedInWords(gsebMarksheetResponse.getObtainedInWords())
                .result(gsebMarksheetResponse.getResult())
                .correctedNotes(gsebMarksheetResponse.getCorrected())
                .examDate(Optional.ofNullable(gsebMarksheetResponse.getDate())
                        .map(date -> LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .orElse(null))
                .build();
    }
}

