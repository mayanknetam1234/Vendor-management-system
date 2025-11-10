package com.MoveInSync.vendorManagement.controller;

import com.MoveInSync.vendorManagement.authorization.RequiresPermission;
import com.MoveInSync.vendorManagement.dto.HierarchyNode;
import com.MoveInSync.vendorManagement.dto.HierarchyStatsResponse;
import com.MoveInSync.vendorManagement.service.interfaces.HierarchyService;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.util.VendorHierarchyHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hierarchy")
public class HierarchyController {

    private final HierarchyService hierarchyService;
    private final VendorRepository vendorRepository;
    private final VendorHierarchyHelper hierarchyHelper;

    public HierarchyController(HierarchyService hierarchyService,
                               VendorRepository vendorRepository,
                               VendorHierarchyHelper hierarchyHelper) {
        this.hierarchyService = hierarchyService;
        this.vendorRepository = vendorRepository;
        this.hierarchyHelper = hierarchyHelper;
    }

    // ✅ 1️⃣ Get full vendor hierarchy tree (vendor → driver → vehicle)
    @GetMapping("/{vendorId}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<HierarchyNode> getHierarchy(HttpServletRequest request, @PathVariable Long vendorId) {
        Long actingVendorId = (Long) request.getAttribute("vendorId");
        Vendor acting = vendorRepository.findById(actingVendorId)
                .orElseThrow(() -> new RuntimeException("Acting vendor not found"));
        Vendor target = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Target vendor not found"));
        if (!target.getVendorId().equals(actingVendorId) &&
                !hierarchyHelper.isAncestor(acting, target)) {
            throw new RuntimeException("Access denied — can only view your own subtree");
        }
        return ResponseEntity.ok(hierarchyService.getVendorHierarchy(vendorId));
    }

    // ✅ 2️⃣ Get hierarchy statistics
    @GetMapping("/stats/{vendorId}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<HierarchyStatsResponse> getStats(HttpServletRequest request, @PathVariable Long vendorId) {
        Long actingVendorId = (Long) request.getAttribute("vendorId");
        Vendor acting = vendorRepository.findById(actingVendorId)
                .orElseThrow(() -> new RuntimeException("Acting vendor not found"));
        Vendor target = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Target vendor not found"));
        if (!target.getVendorId().equals(actingVendorId) &&
                !hierarchyHelper.isAncestor(acting, target)) {
            throw new RuntimeException("Access denied — can only view your own subtree");
        }
        return ResponseEntity.ok(hierarchyService.getHierarchyStats(vendorId));
    }
}
