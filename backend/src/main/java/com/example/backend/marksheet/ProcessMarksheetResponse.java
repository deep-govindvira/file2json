package com.example.backend.marksheet;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProcessMarksheetResponse {
    private String marksheetId;
    private String marksheetUrl;
    private ProcessingStatus processingStatus;
    private LocalDateTime processingStartedAt;
    private VerificationStatus verificationStatus;
    private Long year;
}
