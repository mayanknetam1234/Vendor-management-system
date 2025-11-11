package com.MoveInSync.vendorManagement.controller;

import com.MoveInSync.vendorManagement.authorization.RequiresPermission;
import com.MoveInSync.vendorManagement.dto.PermissionRequestDto;
import com.MoveInSync.vendorManagement.entity.Permission;
import com.MoveInSync.vendorManagement.service.interfaces.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@CrossOrigin
public class PermissionController {

    private final PermissionService permissionService;

    // ✅ 1️⃣ Create new permission
    @PostMapping("/create")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<Permission> createPermission(@RequestBody PermissionRequestDto request) {
        Permission permission = permissionService.createPermission(request);
        return ResponseEntity.ok(permission);
    }

    // ✅ 2️⃣ Get all permissions
    @GetMapping("/list")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    // ✅ 3️⃣ Get a single permission by ID
    @GetMapping("/{permissionId}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<Permission> getPermission(@PathVariable Long permissionId) {
        Permission permission = permissionService.getPermissionById(permissionId);
        return ResponseEntity.ok(permission);
    }

    // ✅ 4️⃣ Update permission details
    @PutMapping("/{permissionId}")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<Permission> updatePermission(@PathVariable Long permissionId,
                                                       @RequestBody PermissionRequestDto request) {
        Permission updated = permissionService.updatePermission(permissionId, request);
        return ResponseEntity.ok(updated);
    }

    // ✅ 5️⃣ Delete a permission
    @DeleteMapping("/{permissionId}")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<String> deletePermission(@PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
        return ResponseEntity.ok("🗑 Permission deleted successfully!");
    }

    // ✅ 6️⃣ Get all permissions assigned to a specific role
    @GetMapping("/role/{roleId}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<Permission>> getPermissionsByRole(@PathVariable Long roleId) {
        List<Permission> permissions = permissionService.getPermissionsByRole(roleId);
        return ResponseEntity.ok(permissions);
    }

    // ✅ 7️⃣ Bulk create permissions (useful during setup)
    @PostMapping("/bulk-create")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<List<Permission>> bulkCreate(@RequestBody List<PermissionRequestDto> requests) {
        List<Permission> created = permissionService.bulkCreate(requests);
        return ResponseEntity.ok(created);
    }
}
