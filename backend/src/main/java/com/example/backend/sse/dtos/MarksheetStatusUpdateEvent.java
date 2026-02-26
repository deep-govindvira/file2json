package com.example.backend.sse.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MarksheetStatusUpdateEvent {
    private String marksheetId;
    private String processingStatus;
}
