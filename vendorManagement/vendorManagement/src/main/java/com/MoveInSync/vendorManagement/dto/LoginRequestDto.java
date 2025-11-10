package com.MoveInSync.vendorManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for handling login requests.
 * It typically contains the user's email/username and password.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    private String email;      // or 'username' depending on your design
    private String password;
}
