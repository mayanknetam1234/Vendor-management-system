package com.MoveInSync.vendorManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    private String registrationNo;
    private String model;
    private String status = "active"; // active, maintenance, blocked
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "vehicle")
    private List<Document> documents;
}
