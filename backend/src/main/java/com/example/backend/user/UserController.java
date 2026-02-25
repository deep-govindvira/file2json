package com.example.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<GetUserResponse>> getUsersByProject(
            @PathVariable String projectId
    ) {
        return ResponseEntity.ok(service.getUsersForProject(projectId));
    }
}
