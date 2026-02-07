package com.example.backend.processed;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface ProcessedFileService {
    // Check if a file has already been processed
    boolean isProcessed(File file);

    // Mark a file as processed
    ProcessedFile markAsProcessed(File file);

    // Find a processed file by path
    Optional<ProcessedFile> findByFilePath(String filePath);

    // Get all unprocessed files
    List<ProcessedFile> getUnprocessedFiles();

}
