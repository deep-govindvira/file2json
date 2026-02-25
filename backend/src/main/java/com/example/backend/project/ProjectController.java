package com.example.backend.project;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    @PostMapping
    public ResponseEntity<CreateProjectResponse> createProject(
            @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createProject(request));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<GetProjectResponse> getProjectInfo(@PathVariable String projectId) {
        return ResponseEntity.ok(service.getProjectInfo(projectId));
    }

    @GetMapping
    public ResponseEntity<List<GetProjectResponse>> getProjectsByUserId() {
        return ResponseEntity.ok(service.getProjectsByUserId());
    }
}
