package com.example.backend.marksheet;

import com.example.backend.board.Board;
import com.example.backend.mark.GetMarkResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GetMarksheetResponse {
    List<GetMarkResponse> markResponseList;
    private String id;
    private String studentName;
    private String fatherName;
    private String motherName;
    private String url;
    private ProcessingStatus processingStatus;
    private LocalDateTime processingStartedAt;
    private Long processingDuration;
    private String seatNo;
    private String schoolCentreNo;
    private String schoolIndexNo;
    private String group;
    private VerificationStatus verificationStatus;
    private Long year;
    private Board board;
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

    private String createdAt;
    private String updatedAt;
}
