package com.example.backend.job;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/jobs")
public class JobController {
    private final JobService service;

    @PostMapping
    public ResponseEntity<CreateJobResponse> addJobToUser(@PathVariable String userId) {
        CreateJobResponse response = service.addJobToUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}