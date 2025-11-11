package com.MoveInSync.vendorManagement.controller;

import com.MoveInSync.vendorManagement.authorization.RequiresPermission;
import com.MoveInSync.vendorManagement.dto.NotificationResponse;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.repository.NotificationRepository;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final VendorRepository vendorRepository;

    @GetMapping("/list")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<NotificationResponse>> listNotifications(HttpServletRequest request) {
        Long vendorId = (Long) request.getAttribute("vendorId");

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        List<NotificationResponse> response = notificationRepository.findByVendor(vendor)
                .stream()
                .map(n -> new NotificationResponse(
                        n.getId(),
                        n.getMessage(),
                        n.getType(),
                        n.isRead(),
                        n.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
