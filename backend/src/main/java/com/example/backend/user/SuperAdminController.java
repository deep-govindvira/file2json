package com.example.backend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/superadmin")
public class SuperAdminController {

    private final UserService service;

    @PostMapping("/registerAdmin")
    public ResponseEntity<RegisterUserResponse> registerUser(
            @Valid @RequestBody RegisterAdminRequest request) {
        RegisterUserResponse response = service.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<GetUserResponse>> getAllAdmins() {
        List<GetUserResponse> admins = service.getAllAdmins();
        return ResponseEntity.ok(admins);
    }
}
