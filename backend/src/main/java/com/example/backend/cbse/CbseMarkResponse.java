package com.example.backend.cbse;

import com.example.backend.Correction;
import lombok.Data;

import java.util.List;

@Data
public class CbseMarkResponse {
    private String subCode;
    private String subject;
    private Integer theory;
    private Integer practical;
    private Integer total;
    private String totalInWords;
    private String positionalGrade;
    private List<Correction> corrected;
}
