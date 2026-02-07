package com.example.backend.marksheet;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface MarksheetService {

    void save(ResponseEntity<Map> response);

    List<SaveMarksheetResponse> saveMarksheets(String userId, String jobId, List<MultipartFile> files);
}
