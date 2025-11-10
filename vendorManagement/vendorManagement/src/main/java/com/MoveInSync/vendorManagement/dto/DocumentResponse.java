package com.MoveInSync.vendorManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    private Long documentId;
    private String fileName;
    private String filePath;
    private String type;
    private String status;
    private LocalDateTime uploadedAt;
    private LocalDateTime expiryDate;
    private Long vendorId;
    private Long driverId;
    private Long vehicleId;
}
