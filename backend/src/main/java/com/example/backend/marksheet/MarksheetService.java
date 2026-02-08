package com.example.backend.marksheet;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MarksheetService {

    List<SaveMarksheetResponse> saveMarksheets(String userId, String jobId, List<MultipartFile> files);

    List<ProcessMarksheetResponse> processMarksheets(String userId, String jobId);
}
