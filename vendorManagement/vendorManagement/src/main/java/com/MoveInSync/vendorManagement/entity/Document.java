package com.MoveInSync.vendorManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.MoveInSync.vendorManagement.enumClass.DocumentStatus;
import com.MoveInSync.vendorManagement.enumClass.DocumentType;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;           // ✅ always required


    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver;           // optional

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = true)
    private Vehicle vehicle;


    @Enumerated(EnumType.STRING)
    private DocumentType type; // LICENSE, RC, INSURANCE
    private String filePath; // local storage path
    private LocalDate expiryDate;
    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.ACTIVE; // ACTIVE, EXPIRED, DELETED, PENDING

    @ManyToOne
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}
