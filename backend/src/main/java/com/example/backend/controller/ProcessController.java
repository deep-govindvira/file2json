package com.example.backend.controller;

import com.example.backend.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

    @PostMapping("/process")
    public ResponseEntity<String> processFolderTrigger() {
        processService.process();
        return ResponseEntity.ok("Processing started in background");
    }
}
