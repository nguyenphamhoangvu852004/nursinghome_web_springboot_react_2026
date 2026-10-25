package com.eldercare.modules.auth.service.impl;

import com.eldercare.exception.custom.BadRequestException;
import com.eldercare.exception.custom.BadCredentialsException;
import com.eldercare.exception.custom.ResourceNotFoundException;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.auth.dto.request.LoginRequest;
import com.eldercare.modules.auth.dto.response.LoginResponse;
import com.eldercare.modules.auth.service.AuthService;
import com.eldercare.modules.security.SessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionStore sessionStore;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (username == null || password == null) {
            throw new BadRequestException("Username and password are required");
        }

        // Find user by email
        Optional<UserEntity> userOpt = userRepository.findByEmailAndIsDeletedFalse(username.trim());
        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("Invalid credentials. User not found.");
        }

        UserEntity user = userOpt.get();

        // Check if user status is ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new BadCredentialsException("Account is currently " + user.getStatus());
        }

        // Normalize BCrypt hash (handles double '$$' typo and '$2b$' compatibility)
        String storedHash = user.getPasswordHash();
        if (storedHash != null) {
            if (storedHash.startsWith("$$")) {
                storedHash = storedHash.substring(1);
            }
            storedHash = storedHash.replace("$2b$", "$2a$");
        }

        // Verify password
        if (!passwordEncoder.matches(password, storedHash)) {
            throw new BadCredentialsException("Invalid credentials. Please try again.");
        }

        // Update last login time
        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);

        String fullName = user.getFirstName() + (user.getMiddleName() != null ? " " + user.getMiddleName() : "") + " " + user.getLastName();
        String token = "mock-jwt-token-" + UUID.randomUUID().toString().substring(0, 8);
        String sessionId = "sess_" + UUID.randomUUID().toString().substring(0, 6);

        sessionStore.createSession(token, sessionId, user.getId(), user.getEmail(), user.getRole().getRoleName(), fullName);

        return new LoginResponse(
            token,
            sessionId,
            user.getRole().getRoleName(),
            fullName
        );
    }

    @Override
    public void resetPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        Optional<UserEntity> userOpt = userRepository.findByEmailAndIsDeletedFalse(email.trim());
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("Account email does not exist in the database");
        }
    }
}
