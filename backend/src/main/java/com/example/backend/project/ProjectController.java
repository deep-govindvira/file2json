package com.example.backend.project;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/projects")
public class ProjectController {

    private final ProjectService service;

    @PostMapping
    public ResponseEntity<CreateProjectResponse> createProject(
            @PathVariable String userId,
            @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                service.createProject(userId, request)
        );
    }
}
