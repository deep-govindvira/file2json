package com.example.backend.sse;

import com.example.backend.auth.service.AuthService;
import com.example.backend.auth.service.JwtService;
import com.example.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthService authService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return sseService.createEmitter(authService.getCurrentUserId());
    }

//    @PostMapping("/test/marksheet")
//    public void marksheetUpdate() {
//        sseService.sendEvent("marksheet-update", "Marksheet updated successfully");
//    }
//
//    @PostMapping("/test/project")
//    public void projectUpdate() {
//        sseService.sendEvent("project-update", "Project status changed");
//    }
//
//    @PostMapping("/test/progress")
//    public void processingProgress() {
//        sseService.sendEvent("processing-progress", 65);
//    }
//
//    @PostMapping("/test/verification")
//    public void verificationUpdate() {
//        sseService.sendEvent("verification-update", "Verification completed");
//    }
}