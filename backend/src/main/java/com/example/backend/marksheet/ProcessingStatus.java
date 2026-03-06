package com.example.backend.marksheet;

public enum ProcessingStatus {
    UNPROCESSED,      // Not yet submitted
    QUEUED,           // Submitted to thread/executor, waiting to start
    PROCESSING,       // Actually being processed
    COMPLETED,
    FAILED
}