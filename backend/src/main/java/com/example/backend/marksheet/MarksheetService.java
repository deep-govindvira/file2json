package com.example.backend.marksheet;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MarksheetService {

    ResponseEntity<List<SaveMarksheetResponse>> saveMarksheets(String userId, String jobId, List<MultipartFile> files);

    ResponseEntity<List<ProcessMarksheetResponse>> processMarksheets(String userId, String jobId);
}
