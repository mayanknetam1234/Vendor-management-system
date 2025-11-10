package com.MoveInSync.vendorManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponseDto {
    private Long vehicleId;
    private String registrationNo;
    private String model;
    private String type;
    private String status;
    private Long vendorId;
    private String vendorName;
}
