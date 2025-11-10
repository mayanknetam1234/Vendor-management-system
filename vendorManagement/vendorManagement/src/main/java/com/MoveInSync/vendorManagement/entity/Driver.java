package com.MoveInSync.vendorManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.MoveInSync.vendorManagement.enumClass.DriverStatus;

@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    private String name;
    private String phone;
    private String licenseNo;

    @ManyToOne
    @JoinColumn(name = "assigned_vehicle_id")
    private Vehicle assignedVehicle;

    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.PENDING; // PENDING, ACTIVE, BLOCKED, INACTIVE
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "driver")
    private List<Document> documents;
}
