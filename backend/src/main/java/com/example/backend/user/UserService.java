package com.example.backend.user;

import com.example.backend.auth.entity.Role;
import com.example.backend.auth.service.AuthService;
import com.example.backend.config.AppProps;
import com.example.backend.department.DepartmentService;
import com.example.backend.notification.NotificationPort;
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
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserConverter converter;
    private final UserProjectService userProjectService;
    private final PasswordEncoder encoder;
    private final AuthService authService;
    private final DepartmentService departmentService;
    private final NotificationPort notificationPort;
    private final ExecutorService executorService;
    private final AppProps props;

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

            executorService.submit(() -> {
                try {

                    String profileLink = props.getAllowedOrigin() + "/profile";

                    String htmlMessage = """
                            <html>
                            <body style="font-family: Arial, sans-serif; background-color: #f5f6fa; padding: 20px;">
                            
                                <div style="max-width: 600px; margin: auto; background: #ffffff; padding: 24px; border-radius: 10px;">
                            
                                    <h2 style="color: #2f3640; margin-bottom: 10px;">Admin Account Created</h2>
                            
                                    <p>Hello <b>%s</b>,</p>
                            
                                    <p>Your admin account has been successfully created.</p>
                            
                                    <div style="background:#f1f2f6; padding:15px; border-radius:8px; margin:20px 0;">
                                        <p style="margin:5px 0;"><b>Email:</b> %s</p>
                                        <p style="margin:5px 0;"><b>Temporary Password:</b> %s</p>
                                    </div>
                            
                                    <p style="color:#d63031; font-weight:bold;">
                                        ⚠️ For security reasons, please change your password immediately after login.
                                    </p>
                            
                                    <div style="text-align:center; margin: 30px 0;">
                                        <a href="%s"
                                           style="background-color:#0984e3;color:#fff;padding:14px 24px;
                                                  text-decoration:none;border-radius:6px;font-weight:bold;
                                                  display:inline-block;">
                                            Change Password
                                        </a>
                                    </div>
                            
                                    <p style="font-size: 13px; color: #555;">
                                        If you did not expect this account, please contact your administrator.
                                    </p>
                            
                                    <hr style="margin: 20px 0;"/>
                            
                                    <p style="font-size:12px;color:gray;">
                                        This is an automated email. Please do not reply.
                                    </p>
                            
                                </div>
                            
                            </body>
                            </html>
                            """.formatted(
                            request.getEmail(),
                            request.getEmail(),
                            request.getEmail(), // since password = email initially
                            profileLink
                    );

                    notificationPort.notify(
                            "Admin Account Created",
                            request.getEmail(),
                            htmlMessage
                    );

                } catch (Exception e) {
                    System.err.println("Failed to send email: " + e.getMessage());
                }
            });
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

    public void deleteAdmin(String userId) {
        UUID id = UUID.fromString(userId);

        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }

        repository.delete(user);
    }
}
