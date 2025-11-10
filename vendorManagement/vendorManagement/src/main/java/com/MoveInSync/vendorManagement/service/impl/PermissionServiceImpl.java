package com.MoveInSync.vendorManagement.service.impl;

import com.MoveInSync.vendorManagement.dto.PermissionRequestDto;
import com.MoveInSync.vendorManagement.entity.Permission;
import com.MoveInSync.vendorManagement.entity.Role;
import com.MoveInSync.vendorManagement.repository.PermissionRepository;
import com.MoveInSync.vendorManagement.repository.RoleRepository;
import com.MoveInSync.vendorManagement.service.interfaces.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    public Permission createPermission(PermissionRequestDto request) {
        if (permissionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Permission already exists!");
        }
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        return permissionRepository.save(permission);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    @Override
    public Permission updatePermission(Long id, PermissionRequestDto request) {
        Permission permission = getPermissionById(id);
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        return permissionRepository.save(permission);
    }

    @Override
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public List<Permission> getPermissionsByRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return new ArrayList<>(role.getPermissions());
    }

    @Override
    public List<Permission> bulkCreate(List<PermissionRequestDto> requests) {
        List<Permission> saved = new ArrayList<>();
        for (PermissionRequestDto r : requests) {
            if (!permissionRepository.existsByName(r.getName())) {
                Permission p = new Permission(null, r.getName(), r.getDescription());
                saved.add(permissionRepository.save(p));
            }
        }
        return saved;
    }
}
