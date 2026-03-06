package com.example.backend.project;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    @PutMapping("/{projectId}")
    public void updateProject(
            @PathVariable String projectId,
            @RequestBody UpdateProjectRequest request
    ) {
        service.updateProject(UUID.fromString(projectId), request);
    }

    @PutMapping("/{projectId}/addUser")
    public void addUserToProject(@PathVariable String projectId,
                                 @RequestBody AccessForUserToProjectRequest request) {
        service.addUserToProject(UUID.fromString(projectId), request);
    }

    @PutMapping("/{projectId}/removeUser")
    public void removeUserToProject(@PathVariable String projectId,
                                    @RequestBody AccessForUserToProjectRequest request) {
        service.removeUserFromProject(UUID.fromString(projectId), request);
    }

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
