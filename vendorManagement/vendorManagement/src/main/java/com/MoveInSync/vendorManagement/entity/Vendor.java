package com.MoveInSync.vendorManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import com.MoveInSync.vendorManagement.enumClass.VendorStatus;

@Entity
@Table(name = "vendors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vendorId;

    private String name;
    private String region;//JUST FOR UI
    private String level; // e.g., SUPER, REGIONAL, CITY//JUST FOR UI
    @Enumerated(EnumType.STRING)
    private VendorStatus status; // ACTIVE, BLOCKED, INACTIVE

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_vendor_id")
    private Vendor parentVendor;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Relationships
    @OneToMany(mappedBy = "vendor")
    private List<User> users;

    @OneToMany(mappedBy = "vendor")
    private List<Driver> drivers;

    @OneToMany(mappedBy = "vendor")
    private List<Vehicle> vehicles;
}
