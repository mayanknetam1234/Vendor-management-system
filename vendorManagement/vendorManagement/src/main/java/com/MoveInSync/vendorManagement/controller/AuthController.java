package com.MoveInSync.vendorManagement.controller;

import com.MoveInSync.vendorManagement.authorization.RequiresPermission;
import com.MoveInSync.vendorManagement.dto.ChangePasswordRequestDto;
import com.MoveInSync.vendorManagement.dto.LoginRequestDto;
import com.MoveInSync.vendorManagement.dto.SignupRequestDto;
import com.MoveInSync.vendorManagement.dto.UserDto;
import com.MoveInSync.vendorManagement.dto.SignupResponseDto;
import com.MoveInSync.vendorManagement.entity.Permission;
import com.MoveInSync.vendorManagement.entity.Role;
import com.MoveInSync.vendorManagement.entity.User;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.enumClass.UserStatus;
import com.MoveInSync.vendorManagement.repository.RoleRepository;
import com.MoveInSync.vendorManagement.repository.UserRepository;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import com.MoveInSync.vendorManagement.security.JwtService;
import com.MoveInSync.vendorManagement.util.MailUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final MailUtil mailUtil;

    @PostMapping("/signup")
    @RequiresPermission("CAN_CREATE_VENDOR")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto request) {
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setVendor(vendor);
        newUser.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(newUser);

        // Send welcome/confirmation email to the newly created user (HTML formatted)
        String emailBody = "<p>Hello,</p>"
                + "<p>Your account has been created successfully.</p>"
                + "<p><strong>Email:</strong> " + saved.getEmail() + "<br/>"
                + "<strong>Password:</strong> " + request.getPassword() + "</p>"
                + "<p>Regards,<br/>Vendor Management</p>";

        mailUtil.sendMail(
                saved.getEmail(),
                "Welcome to Vendor Management",
                emailBody
        );

        SignupResponseDto resp = new SignupResponseDto(
                saved.getUserId(),
                saved.getEmail(),
                saved.getVendor().getVendorId(),
                saved.getStatus(),
                saved.getCreatedAt()
        );


        return ResponseEntity.ok(resp);


    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vendor vendor = user.getVendor();
        Role role = vendor.getRole();

        // ✅ Collect all permissions for the vendor's role
        List<String> permissionNames = role.getPermissions()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        // ✅ Prepare JWT claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("vendorId", vendor.getVendorId());
        claims.put("vendorLevel", vendor.getLevel());
        claims.put("role", role.getName());
        claims.put("permissions", permissionNames);

        // ✅ Generate JWT
        String token = jwtService.generateToken(user.getEmail(), claims);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", user.getEmail(),
                "vendorId", vendor.getVendorId(),
                "vendorLevel", vendor.getLevel(),
                "role", role.getName(),
                "permissions", permissionNames
        ));

    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long vendorId = (Long) request.getAttribute("vendorId");
        String vendorLevel = (String) request.getAttribute("vendorLevel");
        List<String> permissions = (List<String>) request.getAttribute("permissions");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(new UserDto(role, vendorId, vendorLevel, permissions, email));
    }
    @PostMapping("/assign-role/{userId}")
    @RequiresPermission("CAN_MANAGE_PERMISSIONS")
    ///  /assign-role/2?roleId=1

    //TODO: in this whichever tree user belong to find the vendor which has the role that user want and add user
    //in that vendor tree
    public ResponseEntity<String> assignRoleToUser(
            @PathVariable Long userId,
            @RequestParam Long roleId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Update vendor’s role (since role is linked to vendor)
        Vendor vendor = user.getVendor();
        vendor.setRole(role);
        vendorRepository.save(vendor);

        return ResponseEntity.ok("✅ Role " + role.getName() +
                " assigned to vendor " + vendor.getName());
    }


    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            HttpServletRequest request,
            @RequestBody ChangePasswordRequestDto body ) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(body.getOldPassword(), user.getPasswordHash())) {
            return ResponseEntity.badRequest().body("❌ Incorrect current password!");
        }

        user.setPasswordHash(passwordEncoder.encode(body.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("✅ Password changed successfully!");
    }
    @PostMapping("/forgot-password")
    //TODO: do it
    public ResponseEntity<String> forgotPassword() {
        return ResponseEntity.ok("forgot-password ok");
    }
}
