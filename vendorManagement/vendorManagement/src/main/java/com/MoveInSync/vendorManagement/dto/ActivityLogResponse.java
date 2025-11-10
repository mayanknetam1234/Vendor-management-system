package com.MoveInSync.vendorManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLogResponse {
    private Long logId;
    private Long userId;
    private Long vendorId;
    private String action;
    private String details;
    private LocalDateTime timestamp;
}
