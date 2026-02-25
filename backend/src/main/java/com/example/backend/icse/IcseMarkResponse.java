package com.example.backend.icse;

import com.example.backend.Correction;
import lombok.Data;

import java.util.List;

@Data
public class IcseMarkResponse {

    private String subject;
    private Integer percentageMarks;
    private String percentageMarksInWords;
    private List<Correction> corrected;
}
