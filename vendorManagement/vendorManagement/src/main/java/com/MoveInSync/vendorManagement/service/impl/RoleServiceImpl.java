package com.MoveInSync.vendorManagement.service.impl;

import com.MoveInSync.vendorManagement.dto.RoleRequestDto;
import com.MoveInSync.vendorManagement.entity.Permission;
import com.MoveInSync.vendorManagement.entity.Role;
import com.MoveInSync.vendorManagement.repository.PermissionRepository;
import com.MoveInSync.vendorManagement.repository.RoleRepository;
import com.MoveInSync.vendorManagement.service.interfaces.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public Role createRole(RoleRequestDto request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Override
    public Role updateRole(Long id, RoleRequestDto request) {
        Role role = getRoleById(id);
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        roleRepository.delete(role);
    }

    @Override
    public Role updateRolePermissions(Long roleId, List<Long> permissionIds) {
        Role role = getRoleById(roleId);
        Set<Permission> newPermissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.setPermissions(newPermissions);
        return roleRepository.save(role);
    }

    @Override
    public Role addPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = getRoleById(roleId);
        Set<Permission> existing = role.getPermissions();
        existing.addAll(new HashSet<>(permissionRepository.findAllById(permissionIds)));
        role.setPermissions(existing);
        return roleRepository.save(role);
    }

    @Override
    public Role removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        Role role = getRoleById(roleId);
        Set<Permission> updated = role.getPermissions().stream()
                .filter(p -> !permissionIds.contains(p.getPermissionId()))
                .collect(Collectors.toSet());
        role.setPermissions(updated);
        return roleRepository.save(role);
    }
}
