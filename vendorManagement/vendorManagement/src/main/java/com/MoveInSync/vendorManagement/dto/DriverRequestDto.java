package com.MoveInSync.vendorManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverRequestDto {
    private String name;
    private String phone;
    private String licenseNo;
    private String assignedVehicle; // optional: vehicle ID or name; not linked yet
    private Long targetVendorId; // optional: parent adds for child
}
