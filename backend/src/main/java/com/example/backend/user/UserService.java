package com.example.backend.user;

import com.example.backend.user_project.UserProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserConverter converter;
    private final UserProjectService userProjectService;

    public List<User> saveAll(List<User> userList) {
        return repository.saveAll(userList);
    }

    public Optional<User> findById(String userId) {
        return repository.findById(userId);
    }

    public RegisterUserResponse registerUser(RegisterUserRequest request) {
        User user = converter.user(request);
        User saved = repository.save(user);
        return converter.registerUserResponse(saved);
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
