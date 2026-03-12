package com.example.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService service;

//    @PostMapping("/register")
//    public ResponseEntity<RegisterUserResponse> registerUser(
//            @Valid @RequestBody RegisterUserRequest request) {
//        RegisterUserResponse response = service.registerUser(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<LoginUserResponse> registerUser(
//            @Valid @RequestBody LoginUserRequest request) {
//        LoginUserResponse response = service.loginUser(request);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping
    public ResponseEntity<GetUserResponse> getUserInfo() {
        GetUserResponse response = service.getUserInfo();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GetUserResponse>> getUsersByProject(
            @PathVariable String projectId
    ) {
        return ResponseEntity.ok(service.getUsersForProject(projectId));
    }

    @PutMapping
    public String updateProfile(@RequestBody UpdateUserRequest request) {
        return service.updateProfile(request);
    }
}
