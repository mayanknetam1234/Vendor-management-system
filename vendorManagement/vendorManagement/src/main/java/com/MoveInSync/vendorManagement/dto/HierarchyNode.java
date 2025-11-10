package com.MoveInSync.vendorManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HierarchyNode {
    private Long vendorId;
    private String vendorName;
    private String status;
    private List<DriverInfo> drivers;
    private List<VehicleInfo> vehicles;
    private List<HierarchyNode> children;
}
