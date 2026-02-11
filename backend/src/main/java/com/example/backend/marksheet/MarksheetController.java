package com.example.backend.marksheet;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/jobs/{jobId}/marksheets")
public class MarksheetController {
    private final MarksheetService marksheetService;

    @PostMapping
    public ResponseEntity<List<SaveMarksheetResponse>> saveMarksheets(
            @PathVariable String userId, @PathVariable String jobId,
            @RequestParam("files") @NotEmpty(message = "Files must not be empty") List<MultipartFile> files) {
        return marksheetService.saveMarksheets(userId, jobId, files);
    }

    @PostMapping("/process")
    public ResponseEntity<List<ProcessMarksheetResponse>> processMarksheets(
            @PathVariable String userId, @PathVariable String jobId) {
        return marksheetService.processMarksheets(userId, jobId);
    }
}
