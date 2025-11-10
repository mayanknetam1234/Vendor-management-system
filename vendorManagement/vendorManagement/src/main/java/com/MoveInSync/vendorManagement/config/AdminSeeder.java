package com.MoveInSync.vendorManagement.config;

import com.MoveInSync.vendorManagement.entity.Role;
import com.MoveInSync.vendorManagement.entity.User;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.enumClass.UserStatus;
import com.MoveInSync.vendorManagement.enumClass.VendorStatus;
import com.MoveInSync.vendorManagement.repository.RoleRepository;
import com.MoveInSync.vendorManagement.repository.UserRepository;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Order(3)
public class AdminSeeder implements CommandLineRunner {

    private final VendorRepository vendorRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1️⃣ Check if Super Vendor already exists
        Optional<Vendor> superVendorOpt = vendorRepository.findByName("ABC Fleet Pvt. Ltd.");
        Vendor superVendor;

        if (superVendorOpt.isEmpty()) {
            // 2️⃣ Get SUPER_VENDOR_ROLE
            Role superRole = roleRepository.findByName("SUPER_VENDOR_ROLE")
                    .orElseThrow(() -> new RuntimeException("SUPER_VENDOR_ROLE not found!"));

            // 3️⃣ Create Super Vendor
            superVendor = new Vendor();
            superVendor.setName("ABC Fleet Pvt. Ltd.");
            superVendor.setRegion("Global HQ");
            superVendor.setLevel("SUPER");
            superVendor.setStatus(VendorStatus.ACTIVE);
            superVendor.setCreatedAt(LocalDateTime.now());
            superVendor.setRole(superRole);

            vendorRepository.save(superVendor);
            System.out.println("✅ Super Vendor created!");
        } else {
            superVendor = superVendorOpt.get();
        }

        // 4️⃣ Create a Super Admin user if not exists
        Optional<User> superAdminOpt = userRepository.findByEmail("netammayank547@gmail.com");

        if (superAdminOpt.isEmpty()) {
            User admin = new User();
            admin.setEmail("netammayank547@gmail.com");
            admin.setPasswordHash(passwordEncoder.encode("mayank1234"));
            admin.setVendor(superVendor);
            admin.setStatus(UserStatus.ACTIVE);

            userRepository.save(admin);
            System.out.println("✅ Super Admin user created!");
        } else {
            System.out.println("ℹ️ Super Admin user already exists.");
        }
    }
}
