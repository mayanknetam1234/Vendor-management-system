package com.MoveInSync.vendorManagement.service.interfaces;

import com.MoveInSync.vendorManagement.dto.RoleRequestDto;
import com.MoveInSync.vendorManagement.entity.Role;

import java.util.List;

public interface RoleService {

    Role createRole(RoleRequestDto request);

    List<Role> getAllRoles();

    Role getRoleById(Long id);

    Role updateRole(Long id, RoleRequestDto request);

    void deleteRole(Long id);

    Role updateRolePermissions(Long roleId, List<Long> permissionIds);

    Role addPermissionsToRole(Long roleId, List<Long> permissionIds);

    Role removePermissionsFromRole(Long roleId, List<Long> permissionIds);
}
