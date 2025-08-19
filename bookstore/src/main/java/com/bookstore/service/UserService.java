// 13. UserService.java - New service for user management
package com.bookstore.service;

import com.bookstore.dto.UpdateProfileRequest;
import com.bookstore.dto.UserProfileResponse;
import com.bookstore.entity.User;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getCurrentUserProfile() {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }
        return mapToProfileResponse(currentUser);
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        // Users can only view their own profile, admins can view any profile
        if (!SecurityUtils.isCurrentUserAdmin() && !SecurityUtils.isCurrentUser(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return mapToProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        // Check if email is being changed and if it's already taken
        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            currentUser.setEmail(request.getEmail());
        }

        if (request.getName() != null) {
            currentUser.setName(request.getName());
        }

        if (request.getAddress() != null) {
            currentUser.setAddress(request.getAddress());
        }

        User updatedUser = userRepository.save(currentUser);
        return mapToProfileResponse(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("Cannot delete your own account");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        userRepository.deleteById(userId);
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getAddress(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
