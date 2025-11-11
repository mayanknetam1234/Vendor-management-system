package com.MoveInSync.vendorManagement.controller;

import com.MoveInSync.vendorManagement.authorization.RequiresPermission;
import com.MoveInSync.vendorManagement.dto.VendorRequestDto;
import com.MoveInSync.vendorManagement.dto.VendorResponseDto;
import com.MoveInSync.vendorManagement.dto.PermissionDto;
import com.MoveInSync.vendorManagement.entity.Permission;
import com.MoveInSync.vendorManagement.entity.Role;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.enumClass.VendorStatus;
import com.MoveInSync.vendorManagement.repository.RoleRepository;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import com.MoveInSync.vendorManagement.enumClass.ActivityAction;
import com.MoveInSync.vendorManagement.service.interfaces.ActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin
@RequiredArgsConstructor
public class VendorController {

    private final VendorRepository vendorRepository;
    private final RoleRepository roleRepository;
    private final ActivityLogService activityLogService;

    @PostMapping("/create")
    @RequiresPermission("CAN_CREATE_VENDOR")
    public ResponseEntity<VendorResponseDto> createVendor(@RequestBody VendorRequestDto vendorRequest,
                                                          HttpServletRequest request) {

        Long parentVendorId = (Long) request.getAttribute("vendorId");
        Vendor parentVendor = vendorRepository.findById(parentVendorId)
                .orElseThrow(() -> new RuntimeException("Parent vendor not found"));

        Vendor vendor = new Vendor();
        vendor.setName(vendorRequest.getName());
        // Determine parent: if request provides parentId and exists, use it; else fallback to current user's vendor
        Vendor chosenParent = parentVendor;
        if (vendorRequest.getParentId() != null) {
            chosenParent = vendorRepository.findById(vendorRequest.getParentId()).orElse(parentVendor);
        }
        vendor.setParentVendor(chosenParent);
        vendor.setRegion(vendorRequest.getRegion());
        vendor.setLevel(vendorRequest.getLevel());
        vendor.setStatus(vendorRequest.getStatus() != null ? vendorRequest.getStatus() : VendorStatus.ACTIVE);
        vendor.setCreatedAt(java.time.LocalDateTime.now());

        // Role selection: use requested role if exists; otherwise inherit parent's role
        Role chosenRole = null;
        if (vendorRequest.getRoleName() != null && !vendorRequest.getRoleName().isBlank()) {
            chosenRole = roleRepository.findByName(vendorRequest.getRoleName()).orElse(null);
        }
        if (chosenRole == null) {
            chosenRole = parentVendor.getRole();
        }
        vendor.setRole(chosenRole);

        Vendor saved = vendorRepository.save(vendor);

        VendorResponseDto response = new VendorResponseDto(
                saved.getVendorId(),
                saved.getName(),
                saved.getRegion(),
                saved.getLevel(),
                saved.getStatus(),
                saved.getCreatedAt(),
                chosenParent.getVendorId(),
                chosenParent.getName(),
                saved.getRole() != null ? saved.getRole().getName() : null
        );

        return ResponseEntity.ok(response);
    }

    // ✅ 2️⃣ Fetch a single vendor
    @GetMapping("/{id}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<VendorResponseDto> getVendor(@PathVariable Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Vendor parent = vendor.getParentVendor();
        VendorResponseDto response = new VendorResponseDto(
                vendor.getVendorId(),
                vendor.getName(),
                vendor.getRegion(),
                vendor.getLevel(),
                vendor.getStatus(),
                vendor.getCreatedAt(),
                parent != null ? parent.getVendorId() : null,
                parent != null ? parent.getName() : null,
                vendor.getRole() != null ? vendor.getRole().getName() : null
        );

        return ResponseEntity.ok(response);
    }

    // ✅ 3️⃣ Get all vendors accessible to current user
    @GetMapping("/list")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<VendorResponseDto>> listVendor(HttpServletRequest request) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        Vendor currentVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        // Get vendor + children recursively
        List<Vendor> allVendors = getVendorHierarchy(currentVendor);

        List<VendorResponseDto> dtos = new ArrayList<>();
        for (Vendor v : allVendors) {
            Vendor parent = v.getParentVendor();
            dtos.add(new VendorResponseDto(
                    v.getVendorId(),
                    v.getName(),
                    v.getRegion(),
                    v.getLevel(),
                    v.getStatus(),
                    v.getCreatedAt(),
                    parent != null ? parent.getVendorId() : null,
                    parent != null ? parent.getName() : null,
                    v.getRole() != null ? v.getRole().getName() : null
            ));
        }

        return ResponseEntity.ok(dtos);
    }
    private List<Vendor> getVendorHierarchy(Vendor parent) {
        List<Vendor> vendors = new ArrayList<>();
        vendors.add(parent);
        List<Vendor> children = vendorRepository.findByParentVendor(parent);
        for (Vendor child : children) {
            vendors.addAll(getVendorHierarchy(child));
        }
        return vendors;
    }

    // ✅ 4️⃣ Reassign vendor under new parent
    @PutMapping("/{vendorId}/parent")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<VendorResponseDto> reassignVendorParent(@PathVariable Long vendorId,
                                                                  @RequestParam Long newParentId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        Vendor newParent = vendorRepository.findById(newParentId)
                .orElseThrow(() -> new RuntimeException("New parent not found"));

        vendor.setParentVendor(newParent);
        vendorRepository.save(vendor);

        Vendor parentOfNewParent = newParent.getParentVendor();
        VendorResponseDto response = new VendorResponseDto(
                newParent.getVendorId(),
                newParent.getName(),
                newParent.getRegion(),
                newParent.getLevel(),
                newParent.getStatus(),
                newParent.getCreatedAt(),
                parentOfNewParent != null ? parentOfNewParent.getVendorId() : null,
                parentOfNewParent != null ? parentOfNewParent.getName() : null,
                newParent.getRole() != null ? newParent.getRole().getName() : null
        );

        return ResponseEntity.ok(response);
    }

    // ✅ 5️⃣ Block vendor and its subtree
    @PutMapping("/{vendorId}/block")
    @RequiresPermission("CAN_BLOCK_VENDOR")
    public ResponseEntity<String> blockVendorTree(@PathVariable Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        blockRecursively(vendor);
        return ResponseEntity.ok("🚫 Vendor and subtree blocked successfully!");
    }

    private void blockRecursively(Vendor vendor) {
        vendor.setStatus(VendorStatus.BLOCKED);
        vendorRepository.save(vendor);
        // log
        activityLogService.logAction(null, vendor.getVendorId(), ActivityAction.VENDOR_BLOCKED,
                "Blocked vendor " + vendor.getVendorId());
        List<Vendor> children = vendorRepository.findByParentVendor(vendor);
        for (Vendor child : children) {
            blockRecursively(child);
        }
    }

    // ✅ 6️⃣ Unblock vendor and subtree
    @PutMapping("/{vendorId}/unblock")
    @RequiresPermission("CAN_BLOCK_VENDOR")
    public ResponseEntity<String> unblockVendorTree(@PathVariable Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        unblockRecursively(vendor);
        return ResponseEntity.ok("🟢 Vendor and subtree unblocked successfully!");
    }

    private void unblockRecursively(Vendor vendor) {
        vendor.setStatus(VendorStatus.ACTIVE);
        vendorRepository.save(vendor);
        // log
        activityLogService.logAction(null, vendor.getVendorId(), ActivityAction.VENDOR_UNBLOCKED,
                "Unblocked vendor " + vendor.getVendorId());
        List<Vendor> children = vendorRepository.findByParentVendor(vendor);
        for (Vendor child : children) {
            unblockRecursively(child);
        }
    }

    // ✅ 7️⃣ Soft delete vendor
    @DeleteMapping("/{vendorId}")
    @RequiresPermission("CAN_BLOCK_VENDOR")
    public ResponseEntity<String> deleteVendor(@PathVariable Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setStatus(VendorStatus.INACTIVE);
        vendorRepository.save(vendor);
        // log
        activityLogService.logAction(null, vendor.getVendorId(), ActivityAction.VENDOR_DELETED,
                "Soft-deleted vendor " + vendor.getVendorId());
        return ResponseEntity.ok("🗑 Vendor soft-deleted successfully!");
    }

    // ✅ 8️⃣ Fetch permissions assigned to vendor
    @GetMapping("/permissions/{vendorId}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<PermissionDto>> getVendorPermissions(@PathVariable Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        Set<Permission> permissions = vendor.getRole().getPermissions();
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(p -> new PermissionDto(p.getPermissionId(), p.getName(), p.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionDtos);
    }


}
