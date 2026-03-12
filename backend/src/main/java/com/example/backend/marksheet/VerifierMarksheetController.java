package com.example.backend.marksheet;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/verifier/projects/{projectId}/marksheets")
public class VerifierMarksheetController {

    private final MarksheetService service;

    @GetMapping
    public ResponseEntity<List<GetMarksheetStatusResponse>> getMarksheetResponseResponseEntity(
            @PathVariable String projectId) {
        return ResponseEntity.ok(service.getMarksheetResponseListForVerifier(projectId));
    }

    @GetMapping("/{marksheetId}")
    public ResponseEntity<GetMarksheetResponse> getMarksheetResponseResponseEntity(
            @PathVariable String projectId,
            @PathVariable String marksheetId) {
        return ResponseEntity.ok(service.getMarksheetInfoByIdForVerifier(projectId, marksheetId));
    }
}
