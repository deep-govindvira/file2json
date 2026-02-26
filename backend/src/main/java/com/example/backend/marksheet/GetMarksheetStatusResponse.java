package com.example.backend.marksheet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetMarksheetStatusResponse {
    private String id;
    private ProcessingStatus processingStatus;
}
