package com.example.backend.auth.service;

import com.example.backend.auth.dto.AuthResponse;
import com.example.backend.auth.dto.LoginRequest;
import com.example.backend.auth.dto.RegisterRequest;
import com.example.backend.auth.entity.RefreshToken;
import com.example.backend.auth.entity.RefreshTokenRepository;
import com.example.backend.auth.entity.Role;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        User user = User.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(Role.VERIFIER)
                .build();

        userRepo.save(user);

        return generateTokens(user);
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepo.findByEmail(request.getEmail()).get();

        return generateTokens(user);
    }

    private AuthResponse generateTokens(User user) {

        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiryDate(Instant.now().plusMillis(604800000))
                .revoked(false)
                .build();

        refreshRepo.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenValue, user.getRole().name());
    }

    public AuthResponse refresh(String token) {

        RefreshToken refreshToken = refreshRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked() ||
                refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(newAccessToken, token, user.getRole().name());
    }

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId().toString();
    }

    public void logout(String refreshToken) {
        String userId = getCurrentUserId();

        RefreshToken token = refreshRepo.findByToken(refreshToken).orElseThrow();
        if (token.getUser().getId().toString().equals(userId)) {
            refreshRepo.delete(token);
        } else
            throw new RuntimeException(
                    "User with userId " + userId + " has no relation with refreshToken " + refreshToken);
    }
}

