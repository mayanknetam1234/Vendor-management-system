package com.MoveInSync.vendorManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending login response back to the client.
 * Typically includes token and basic user info.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String token;
    private String tokenType = "Bearer"; // optional, can be fixed as 'Bearer'
    private String username;
    private String role;
}
