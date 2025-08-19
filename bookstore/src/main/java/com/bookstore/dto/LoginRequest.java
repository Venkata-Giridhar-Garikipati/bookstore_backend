// 5. LoginRequest.java - Updated to allow email or username
package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username/Email is required")
    private String usernameOrEmail; // Can be either username or email

    @NotBlank(message = "Password is required")
    private String password;

    // Keep old getters for backward compatibility
    public String getUsername() {
        return usernameOrEmail;
    }

    public void setUsername(String username) {
        this.usernameOrEmail = username;
    }
}
