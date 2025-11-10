package com.MoveInSync.vendorManagement.config;

import com.MoveInSync.vendorManagement.entity.Permission;
import com.MoveInSync.vendorManagement.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
public class PermissionSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {
        List<String> perms = List.of(
                "CAN_CREATE_VENDOR",
                "CAN_BLOCK_VENDOR",
                "CAN_ADD_DRIVER",
                "CAN_UPLOAD_DOC",
                "CAN_VERIFY_DOC",
                "CAN_MANAGE_PERMISSIONS"
        );
        for (String p : perms) {
            // Avoid duplicate entries if already present
            if (!permissionRepository.existsByName(p)) {
                permissionRepository.save(new Permission(null, p, "Permission for " + p));
                System.out.println("[Seeder] Seeded permission: " + p);
            } else {
                System.out.println("[Seeder] Permission already exists: " + p);
            }
        }
    }
}
