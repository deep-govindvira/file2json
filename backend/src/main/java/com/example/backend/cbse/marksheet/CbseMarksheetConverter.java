package com.example.backend.cbse.marksheet;

public class CbseMarksheetConverter {
    public static CbseMarksheet toCbseMarksheet(CbseMarksheetResponse cbseMarksheetResponse) {
        return CbseMarksheet.builder()
                .board(cbseMarksheetResponse.getBoard())
                .corrected(cbseMarksheetResponse.getCorrected())
                .date(cbseMarksheetResponse.getDate())
                .fatherName(cbseMarksheetResponse.getFatherName())
                .mothersName(cbseMarksheetResponse.getMothersName())
                .result(cbseMarksheetResponse.getResult())
                .rollNo(cbseMarksheetResponse.getRollNo())
                .school(cbseMarksheetResponse.getSchool())
                .studentsName(cbseMarksheetResponse.getStudentsName())
                .build();
    }
}
