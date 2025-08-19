// 9. UpdateProfileRequest.java - New DTO for profile updates
package com.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    private String address;
}
