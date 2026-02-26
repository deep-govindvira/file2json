package com.example.backend.sse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SseController {

    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return sseService.createEmitter();
    }

    @PostMapping("/test/marksheet")
    public void marksheetUpdate() {
        sseService.sendEvent("marksheet-update", "Marksheet updated successfully");
    }

    @PostMapping("/test/project")
    public void projectUpdate() {
        sseService.sendEvent("project-update", "Project status changed");
    }

    @PostMapping("/test/progress")
    public void processingProgress() {
        sseService.sendEvent("processing-progress", 65);
    }

    @PostMapping("/test/verification")
    public void verificationUpdate() {
        sseService.sendEvent("verification-update", "Verification completed");
    }
}