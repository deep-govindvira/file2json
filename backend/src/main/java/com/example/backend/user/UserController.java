package com.example.backend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(
            @Valid @RequestBody RegisterUserRequest request) {
        RegisterUserResponse response = service.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> registerUser(
            @Valid @RequestBody LoginUserRequest request) {
        LoginUserResponse response = service.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
