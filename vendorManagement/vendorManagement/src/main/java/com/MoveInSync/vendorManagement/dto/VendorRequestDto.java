package com.MoveInSync.vendorManagement.dto;

import com.MoveInSync.vendorManagement.enumClass.VendorStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorRequestDto {
    private String name;
    private String region;
    private String level;          // e.g., SUPER, REGIONAL, CITY (UI-defined string)
    private VendorStatus status;   // Uses enum; optional in requests
    private String roleName;       // Optional: desired role name for the new vendor
    private Long parentId;         // Optional: parent vendor ID; fallback to current vendor if null or not found
}
