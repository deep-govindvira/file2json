package com.example.backend.project;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/verifier/projects")
public class VerifierProjectController {
    private final ProjectService service;

    @GetMapping
    public ResponseEntity<List<GetProjectResponseForVerifier>> getProjectsByUserId() {
        return ResponseEntity.ok(service.getProjectsByUserIdForVerifier());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<GetProjectResponseForVerifier> getProjectInfoForVerifier(
            @PathVariable String projectId
    ) {
        return ResponseEntity.ok(service.getProjectInfoForVerifier(projectId));
    }
}
