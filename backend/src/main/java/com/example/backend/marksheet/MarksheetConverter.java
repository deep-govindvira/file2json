package com.example.backend.marksheet;

import com.example.backend.marksheet_summary.MarksheetSummary;
import org.springframework.stereotype.Component;

@Component
public class MarksheetConverter {
    public UploadMarksheetResponse uploadMarksheetResponse(Marksheet marksheet) {
        return UploadMarksheetResponse.builder()
                .marksheetId(marksheet.getId())
                .processingStatus(marksheet.getProcessingStatus())
                .verificationStatus(marksheet.getVerificationStatus())
                .build();
    }

    public ProcessMarksheetResponse processMarksheetResponse(Marksheet marksheet) {
        return ProcessMarksheetResponse.builder()
                .marksheetId(marksheet.getId())
                .marksheetUrl(marksheet.getUrl())
                .processingStartedAt(marksheet.getProcessingStartedAt())
                .processingStatus(marksheet.getProcessingStatus())
                .verificationStatus(marksheet.getVerificationStatus())
                .year(marksheet.getYear())
                .build();
    }

    public GetMarksheetResponse getMarksheetResponse(Marksheet marksheet) {
        MarksheetSummary summary = marksheet.getMarksheetSummary();


        return GetMarksheetResponse.builder()
                .board(marksheet.getBoard())
                .corrected(marksheet.getCorrected())
                .fatherName(marksheet.getFatherName())
                .group(marksheet.getGroup())
                .id(marksheet.getId())
                .motherName(marksheet.getMotherName())
                .processingDuration(marksheet.getProcessingDuration())
                .processingStartedAt(marksheet.getProcessingStartedAt())
                .processingStatus(marksheet.getProcessingStatus())
                .schoolCentreNo(marksheet.getSchoolCentreNo())
                .schoolIndexNo(marksheet.getSchoolIndexNo())
                .seatNo(marksheet.getSeatNo())
                .studentName(marksheet.getStudentName())
                .url(marksheet.getUrl())
                .createdAt(String.valueOf(marksheet.getCreatedAt()))
                .updatedAt(String.valueOf(marksheet.getUpdatedAt()))
                .verificationStatus(marksheet.getVerificationStatus())
                .verifiedByUser(
                        marksheet.getVerifiedByUser() != null
                                ? marksheet.getVerifiedByUser().getName()
                                : null
                ).year(marksheet.getYear())
                .obtainedGrade(summary != null ? summary.getObtainedGrade() : null)
                .obtainedPercentage(summary != null ? summary.getObtainedPercentage() : null)
                .obtainedPercentile(summary != null ? summary.getObtainedPercentile() : null)
                .resultStatus(summary != null ? summary.getResultStatus() : null)
                .totalObtainedMarks(summary != null ? summary.getTotalObtainedMarks() : null)
                .totalOutOfMarks(summary != null ? summary.getTotalOutOfMarks() : null)
                .yearOfPassing(summary != null ? summary.getYearOfPassing() : null)
                .build();
    }
}
