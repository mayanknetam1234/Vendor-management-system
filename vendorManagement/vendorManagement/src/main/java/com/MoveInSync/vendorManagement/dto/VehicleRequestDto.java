package com.MoveInSync.vendorManagement.dto;

import lombok.Data;

@Data
public class VehicleRequestDto {
    private String registrationNo;
    private String model;
    private String type; // e.g., Sedan, SUV, Truck
    private Long targetVendorId; // optional — for parent to add under child vendor
}
