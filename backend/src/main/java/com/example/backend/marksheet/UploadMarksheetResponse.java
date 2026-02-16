package com.example.backend.marksheet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadMarksheetResponse {
    private String marksheetId;
    private ProcessingStatus processingStatus;
    private VerificationStatus verificationStatus;
}
