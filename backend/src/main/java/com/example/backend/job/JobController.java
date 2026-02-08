package com.example.backend.job;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/jobs")
public class JobController {
    private final JobService service;

    @PostMapping
    public ResponseEntity<CreateJobResponse> addJobToUser(@PathVariable String userId, @RequestBody CreateJobRequest request) {
        CreateJobResponse response = service.addJobToUser(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}