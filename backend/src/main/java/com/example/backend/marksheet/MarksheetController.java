package com.example.backend.marksheet;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/jobs/{jobId}/marksheets")
public class MarksheetController {
    private final MarksheetService marksheetService;

    @PostMapping
    public ResponseEntity<List<SaveMarksheetResponse>> saveMarksheets(
            @PathVariable String userId, @PathVariable String jobId,
            @RequestParam("files") @NotEmpty(message = "Files must not be empty") List<MultipartFile> files) {
        List<SaveMarksheetResponse> responses = marksheetService.saveMarksheets(userId, jobId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}
