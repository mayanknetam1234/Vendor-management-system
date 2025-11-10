package com.MoveInSync.vendorManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String type;              // e.g. "DOCUMENT_EXPIRY", "INFO", "ALERT"
    @Column(name = "is_read")
    private boolean read = false;     // to mark seen/unseen
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;            // vendor who receives the notification
}
