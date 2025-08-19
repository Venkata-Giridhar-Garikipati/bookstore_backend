// 6. SignUpResponse.java - Updated response DTO
package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String role;
    private LocalDateTime createdAt;
}

