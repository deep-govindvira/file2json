package com.example.backend.marksheet;

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
}
