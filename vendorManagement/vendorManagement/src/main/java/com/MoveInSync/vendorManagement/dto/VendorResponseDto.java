package com.MoveInSync.vendorManagement.dto;

import com.MoveInSync.vendorManagement.enumClass.VendorStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponseDto {
    private Long vendorId;
    private String name;
    private String region;
    private String level;
    private VendorStatus status;
    private LocalDateTime createdAt;
    private Long parentVendorId;
    private String parentVendorName;
    private String roleName;
}
