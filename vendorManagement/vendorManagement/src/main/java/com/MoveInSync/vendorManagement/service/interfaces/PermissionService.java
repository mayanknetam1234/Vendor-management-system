package com.MoveInSync.vendorManagement.service.interfaces;

import com.MoveInSync.vendorManagement.dto.PermissionRequestDto;
import com.MoveInSync.vendorManagement.entity.Permission;

import java.util.List;

public interface PermissionService {

    Permission createPermission(PermissionRequestDto request);

    List<Permission> getAllPermissions();

    Permission getPermissionById(Long id);

    Permission updatePermission(Long id, PermissionRequestDto request);

    void deletePermission(Long id);

    List<Permission> getPermissionsByRole(Long roleId);

    List<Permission> bulkCreate(List<PermissionRequestDto> requests);
}
