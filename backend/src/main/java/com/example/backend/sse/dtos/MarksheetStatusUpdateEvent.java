package com.example.backend.sse.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarksheetStatusUpdateEvent {
    private String projectId;
    private String marksheetId;
    private String processingStatus;
    private String verificationStatus;
    private String verifiedByUser;
}
