package com.MoveInSync.vendorManagement.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class DocumentRequest {
    private MultipartFile file;
    private String type;
    private Long vendorId;
    private Long driverId;   // optional
    private Long vehicleId;  // optional
    private LocalDateTime expiryDate;
}
