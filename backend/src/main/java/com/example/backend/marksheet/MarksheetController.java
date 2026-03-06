package com.example.backend.marksheet;

import com.example.backend.config.AppProps;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/marksheets")
public class MarksheetController {

    private final MarksheetService service;
    private final AppProps props;

    @PutMapping("/{marksheetId}")
    public void updateMarksheet(
            @PathVariable String projectId,
            @PathVariable String marksheetId,
            @RequestBody UpdateMarksheetRequest request) {
        service.updateMarksheet(UUID.fromString(projectId), UUID.fromString(marksheetId), request);
    }

    @GetMapping
    public ResponseEntity<List<GetMarksheetStatusResponse>> getMarksheetResponseResponseEntity(
            @PathVariable String projectId) {
        return ResponseEntity.ok(service.getMarksheetResponseList(projectId));
    }

    @GetMapping("/{marksheetId}")
    public ResponseEntity<GetMarksheetResponse> getMarksheetResponseResponseEntity(
            @PathVariable String projectId,
            @PathVariable String marksheetId) {
        return ResponseEntity.ok(service.getMarksheetInfoById(projectId, marksheetId));
    }

    @PostMapping("/{marksheetId}/process")
    public ResponseEntity<ProcessMarksheetResponse> processMarksheet(
            @PathVariable String projectId,
            @PathVariable String marksheetId) {
        ProcessMarksheetResponse response = service.processMarksheet(projectId, marksheetId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process")
    public ResponseEntity<List<ProcessMarksheetResponse>> processMarksheets(
            @PathVariable String projectId) {
        List<ProcessMarksheetResponse> responseList = service.processMarksheets(projectId);
        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> storeMarksheets(
            @PathVariable String projectId,
            @RequestParam("files")
            @NotEmpty(message = "Files must not be empty")
            List<MultipartFile> files) {

        String maxFileSizeConfig = props.getSpring().getServlet().getMultipart().getMaxFileSize();
        String maxRequestSizeConfig = props.getSpring().getServlet().getMultipart().getMaxRequestSize();

        long maxFileSize = parseSizeToBytes(maxFileSizeConfig);
        long maxRequestSize = parseSizeToBytes(maxRequestSizeConfig);

        // Check individual file size
        for (MultipartFile file : files) {
            if (file.getSize() > maxFileSize) {
                return ResponseEntity
                        .status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("File " + file.getOriginalFilename() +
                                " is too large! Maximum allowed size per file is " + maxFileSizeConfig + ".");
            }
        }

        // Check total request size
        long totalSize = files.stream().mapToLong(MultipartFile::getSize).sum();
        if (totalSize > maxRequestSize) {
            return ResponseEntity
                    .status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("Total upload size exceeds the maximum allowed of " + maxRequestSizeConfig + ".");
        }

        // Proceed with storing files
        List<UploadMarksheetResponse> responses = service.storeMarksheets(projectId, files);
        return ResponseEntity.ok(responses);
    }

    // Helper method to parse sizes like "5MB", "10KB" to bytes
    private long parseSizeToBytes(String size) {
        size = size.toUpperCase().trim();
        if (size.endsWith("KB")) {
            return Long.parseLong(size.replace("KB", "").trim()) * 1024;
        } else if (size.endsWith("MB")) {
            return Long.parseLong(size.replace("MB", "").trim()) * 1024 * 1024;
        } else if (size.endsWith("GB")) {
            return Long.parseLong(size.replace("GB", "").trim()) * 1024 * 1024 * 1024;
        }
        return Long.parseLong(size); // assume bytes if no suffix
    }

}