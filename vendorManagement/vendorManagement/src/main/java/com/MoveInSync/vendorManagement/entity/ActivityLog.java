package com.MoveInSync.vendorManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.MoveInSync.vendorManagement.enumClass.ActivityAction;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    private String entityType; // e.g., Driver, Document, Vendor
    private Long entityId;     // e.g., driverId or documentId
    private String action;     // e.g., "Uploaded Document", "Blocked Vendor"
    @Enumerated(EnumType.STRING)
    private ActivityAction actionEnum; // standardized action
    private String details;    // extra info (e.g. file name, reason)
    private LocalDateTime createdAt = LocalDateTime.now();
}
