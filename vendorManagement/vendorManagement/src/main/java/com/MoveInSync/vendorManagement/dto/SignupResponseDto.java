package com.MoveInSync.vendorManagement.dto;

import com.MoveInSync.vendorManagement.enumClass.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for sending response after successful user signup.
 * It avoids exposing sensitive fields like passwordHash.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponseDto {
    private Long userId;
    private String email;
    private Long vendorId;
    private UserStatus status;
    private LocalDateTime createdAt;
}
