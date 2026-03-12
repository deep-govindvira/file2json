package com.example.backend.user;

import com.example.backend.auth.entity.Role;
import com.example.backend.auth.service.AuthService;
import com.example.backend.department.DepartmentService;
import com.example.backend.user_project.UserProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserConverter converter;
    private final UserProjectService userProjectService;
    private final PasswordEncoder encoder;
    private final AuthService authService;
    private final DepartmentService departmentService;

    public User save(User user) {
        return repository.save(user);
    }

    public List<User> saveAll(List<User> userList) {
        return repository.saveAll(userList);
    }

    public Optional<User> findById(String userId) {
        return repository.findById(UUID.fromString(userId));
    }

    public RegisterUserResponse registerUser(RegisterUserRequest request) {
        User user = converter.user(request);
        User saved = repository.save(user);
        return converter.registerUserResponse(saved);
    }

    public String updateProfile(UpdateUserRequest request) {

        String id = authService.getCurrentUserId();

        User user = repository.findById(UUID.fromString(id)).orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(encoder.encode(request.getPassword()));
        }

        repository.save(user);

        return "Profile updated successfully";
    }


    public RegisterUserResponse registerAdmin(RegisterAdminRequest request) {

        Optional<User> existingUser = repository.findByEmail(request.getEmail());

        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setRole(Role.ADMIN);
            user.setDepartment(departmentService.getDepartmentById(UUID.fromString(request.getDepartment())));
        } else {
            user = User.builder()
                    .name(request.getEmail())
                    .email(request.getEmail())
                    .password(encoder.encode(request.getEmail()))
                    .department(departmentService.getDepartmentById(UUID.fromString(request.getDepartment())))
                    .role(Role.ADMIN)
                    .build();
        }

        User saved = repository.save(user);
        return converter.registerUserResponse(saved);
    }

    public List<GetUserResponse> getAllAdmins() {
        return repository.findByRole(Role.ADMIN)
                .stream()
                .map(converter::getUserResponse)
                .toList();
    }

    public LoginUserResponse loginUser(LoginUserRequest request) {
        User user = converter.user(request);
        User existingUser = repository.findByEmail(user.getEmail()).orElseThrow();
        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("User not found");
        }
        return converter.loginUserResponse(existingUser);
    }

    public GetUserResponse getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = findByEmail(email).orElseThrow();
        return converter.getUserResponse(user);
    }

    public List<GetUserResponse> getUsersForProject(String projectId) {
        List<User> userList = userProjectService.getUsersByProjectId(projectId);
        List<GetUserResponse> responseList = new ArrayList<>();
        for (User user : userList) {
            responseList.add(converter.getUserResponse(user));
        }
        return responseList;
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
