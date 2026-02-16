package com.example.backend.marksheet;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MarksheetService {
    List<ProcessMarksheetResponse> processMarksheets(String userId, String projectId);

    ProcessMarksheetResponse processMarksheet(String userId, String projectId, String marksheetId);

    List<UploadMarksheetResponse> storeMarksheets(String userId, String projectId, List<MultipartFile> files);
}
