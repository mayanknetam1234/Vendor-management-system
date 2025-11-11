package com.MoveInSync.vendorManagement.controller;

import com.MoveInSync.vendorManagement.authorization.RequiresPermission;
import com.MoveInSync.vendorManagement.dto.RoleRequestDto;
import com.MoveInSync.vendorManagement.dto.RoleResponseDto;
import com.MoveInSync.vendorManagement.entity.Role;
import com.MoveInSync.vendorManagement.service.interfaces.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController

@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // ✅ 1️⃣ Create a new role
    @PostMapping("/create")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<RoleResponseDto> createRole(@RequestBody RoleRequestDto request) {
        Role newRole = roleService.createRole(request);
        return ResponseEntity.ok(mapToResponse(newRole));
    }

    // ✅ 2️⃣ Get all roles
    @GetMapping("/list")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        List<RoleResponseDto> roles = roleService.getAllRoles().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    // ✅ 3️⃣ Get a single role by ID (with permissions)
    @GetMapping("/{roleId}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<RoleResponseDto> getRole(@PathVariable Long roleId) {
        Role role = roleService.getRoleById(roleId);
        return ResponseEntity.ok(mapToResponse(role));
    }

    // ✅ 4️⃣ Update role info (name, description)
    @PutMapping("/{roleId}")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Long roleId, @RequestBody RoleRequestDto request) {
        Role updated = roleService.updateRole(roleId, request);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    // ✅ 5️⃣ Update the list of permissions (replace existing)
    @PutMapping("/{roleId}/permissions")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<RoleResponseDto> updateRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        System.out.println("hiii");
        Role updated = roleService.updateRolePermissions(roleId, permissionIds);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    // ✅ 6️⃣ Add one or more permissions to a role
    @PostMapping("/{roleId}/permissions/add")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<RoleResponseDto> addPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        Role updated = roleService.addPermissionsToRole(roleId, permissionIds);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    // ✅ 7️⃣ Remove one or more permissions from a role
    @DeleteMapping("/{roleId}/permissions/remove")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<RoleResponseDto> removePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        Role updated = roleService.removePermissionsFromRole(roleId, permissionIds);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    // ✅ 8️⃣ Soft delete role
    @DeleteMapping("/{roleId}")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    public ResponseEntity<String> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok("🗑 Role deleted successfully!");
    }

    private RoleResponseDto mapToResponse(Role role) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setRoleId(role.getRoleId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setCreatedAt(role.getCreatedAt());
        // map permissions as names to avoid exposing entities
        dto.setPermissions(role.getPermissions().stream()
                .map(p -> p.getName())
                .collect(Collectors.toList()));
        return dto;
    }
}
