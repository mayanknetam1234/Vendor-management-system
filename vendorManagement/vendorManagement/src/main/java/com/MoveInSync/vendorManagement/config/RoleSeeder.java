package com.MoveInSync.vendorManagement.config;

import com.MoveInSync.vendorManagement.entity.Permission;
import com.MoveInSync.vendorManagement.entity.Role;
import com.MoveInSync.vendorManagement.repository.PermissionRepository;
import com.MoveInSync.vendorManagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

import java.util.*;

@Component
@RequiredArgsConstructor
@Order(2)
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {
        // Step 1️⃣: Define all roles
        Map<String, List<String>> rolePermissionsMap = new LinkedHashMap<>();

        rolePermissionsMap.put("SUPER_VENDOR_ROLE", List.of(
                "CAN_CREATE_VENDOR",
                "CAN_BLOCK_VENDOR",
                "CAN_ADD_DRIVER",
                "CAN_UPLOAD_DOC",
                "CAN_VERIFY_DOC",
                "CAN_MANAGE_PERMISSIONS"
        ));

        rolePermissionsMap.put("REGIONAL_VENDOR_ROLE", List.of(
                "CAN_ADD_DRIVER",
                "CAN_UPLOAD_DOC",
                "CAN_VERIFY_DOC"
        ));

        rolePermissionsMap.put("CITY_VENDOR_ROLE", List.of(
                "CAN_ADD_DRIVER",
                "CAN_UPLOAD_DOC"
        ));

        // Step 2️⃣: Iterate through all roles and assign permissions
        for (Map.Entry<String, List<String>> entry : rolePermissionsMap.entrySet()) {
            String roleName = entry.getKey();
            List<String> permissions = entry.getValue();

            // Check if role exists already
            Role role = roleRepository.findByName(roleName).orElse(null);
            if (role == null) {
                role = new Role();
                role.setName(roleName);
                role.setDescription("Auto-generated role: " + roleName);
            }

            // Fetch permissions from DB
            Set<Permission> permissionSet = new HashSet<>(permissionRepository.findByNameIn(permissions));
            role.setPermissions(permissionSet);

            roleRepository.save(role);
            System.out.println("[Seeder] Role saved: " + roleName + " with permissions: " + permissions);
        }

        System.out.println("✅ Roles and permissions seeded successfully!");
    }
}
