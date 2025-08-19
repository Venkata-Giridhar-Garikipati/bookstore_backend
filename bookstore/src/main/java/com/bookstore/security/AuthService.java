// 10. Enhanced AuthService.java - Updated for new fields
package com.bookstore.security;

import com.bookstore.dto.*;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }

    public SignUpResponse signup(SignupRequest signupRequest) {
        // Check if user already exists by username or email
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Default to USER role if not specified
        Role userRole = signupRequest.getRole() != null ? signupRequest.getRole() : Role.USER;

        User user = userRepository.save(User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .name(signupRequest.getName())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .address(signupRequest.getAddress())
                .role(userRole)
                .build()
        );

        return new SignUpResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }

    // Method to create admin user
    public User createAdminUser(String username, String email, String name, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Admin username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Admin email already exists");
        }

        return userRepository.save(User.builder()
                .username(username)
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .build()
        );
    }
}
