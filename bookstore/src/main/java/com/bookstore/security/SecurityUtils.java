// 17. Enhanced SecurityUtils.java - Utility class for security operations
package com.bookstore.security;

import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * Get the currently authenticated user
     * @return User object or null if no user is authenticated
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Get the ID of the currently authenticated user
     * @return User ID or null if no user is authenticated
     */
    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * Get the username of the currently authenticated user
     * @return Username or null if no user is authenticated
     */
    public static String getCurrentUsername() {
        User user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * Get the email of the currently authenticated user
     * @return Email or null if no user is authenticated
     */
    public static String getCurrentUserEmail() {
        User user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    /**
     * Get the name of the currently authenticated user
     * @return Name or null if no user is authenticated
     */
    public static String getCurrentUserName() {
        User user = getCurrentUser();
        return user != null ? user.getName() : null;
    }

    /**
     * Check if the current user has admin role
     * @return true if current user is admin, false otherwise
     */
    public static boolean isCurrentUserAdmin() {
        User user = getCurrentUser();
        return user != null && Role.ADMIN.equals(user.getRole());
    }

    /**
     * Check if the current user has user role
     * @return true if current user has USER role, false otherwise
     */
    public static boolean isCurrentUserRegular() {
        User user = getCurrentUser();
        return user != null && Role.USER.equals(user.getRole());
    }

    /**
     * Check if the given userId matches the current user's ID
     * @param userId the user ID to check
     * @return true if the userId matches current user's ID, false otherwise
     */
    public static boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }

    /**
     * Check if the current user can access the given user's data
     * (either it's their own data or they are an admin)
     * @param userId the user ID to check access for
     * @return true if access is allowed, false otherwise
     */
    public static boolean canAccessUserData(Long userId) {
        return isCurrentUserAdmin() || isCurrentUser(userId);
    }

    /**
     * Check if there is an authenticated user
     * @return true if a user is authenticated, false otherwise
     */
    public static boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String); // Anonymous users have String principal
    }

    /**
     * Get the role of the current user
     * @return Role of current user or null if no user is authenticated
     */
    public static Role getCurrentUserRole() {
        User user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }
}