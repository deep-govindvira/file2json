package com.example.backend.marksheet;

import com.example.backend.mark.UpdateMarkRequest;
import lombok.Data;

import java.util.List;

@Data
public class UpdateMarksheetRequest {
    List<UpdateMarkRequest> markResponseList;
    private String id;
    private String studentName;
    private String fatherName;
    private String motherName;
    private String url;
    private ProcessingStatus processingStatus;
    private String seatNo;
    private String schoolCentreNo;
    private String schoolIndexNo;
    private String group;
    private VerificationStatus verificationStatus;
    private Long year;
    private String board;
    private String verifiedByUser;
    private String corrected;

    // summary
    private Integer yearOfPassing;
    private Integer totalOutOfMarks;
    private Integer totalObtainedMarks;
    private Double obtainedPercentage;
    private Double obtainedPercentile;
    private String obtainedGrade;
    private String resultStatus;
}
