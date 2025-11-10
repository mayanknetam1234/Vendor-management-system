package com.MoveInSync.vendorManagement.service.interfaces;

import com.MoveInSync.vendorManagement.dto.HierarchyNode;
import com.MoveInSync.vendorManagement.dto.HierarchyStatsResponse;

public interface HierarchyService {
    HierarchyNode getVendorHierarchy(Long vendorId);
    HierarchyStatsResponse getHierarchyStats(Long vendorId);
}
