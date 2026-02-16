package com.example.backend.icse;

import lombok.Data;

@Data
public class IcseMarkResponse {

    private String subject;
    private Integer percentageMarks;
    private String percentageMarksInWords;

}
