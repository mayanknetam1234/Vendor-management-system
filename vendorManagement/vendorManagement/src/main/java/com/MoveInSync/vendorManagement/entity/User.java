package com.MoveInSync.vendorManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.MoveInSync.vendorManagement.enumClass.UserStatus;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    private String email;
    private String passwordHash;

    private boolean temporaryPassword = true;
    @Enumerated(EnumType.STRING)
    private UserStatus status; // ACTIVE, BLOCKED, INACTIVE

    private LocalDateTime lastLogin;
    private LocalDateTime createdAt = LocalDateTime.now();
}
