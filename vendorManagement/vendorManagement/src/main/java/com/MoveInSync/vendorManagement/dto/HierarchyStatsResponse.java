package com.MoveInSync.vendorManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HierarchyStatsResponse {
    private Long totalVendors;
    private Long totalDrivers;
    private Long totalVehicles;
    private Long blockedVendors;
    private Long inactiveDrivers;
    private Long inactiveVehicles;
}
