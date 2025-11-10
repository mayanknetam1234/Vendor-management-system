package com.MoveInSync.vendorManagement.controller;

import com.MoveInSync.vendorManagement.authorization.RequiresPermission;
import com.MoveInSync.vendorManagement.dto.ActivityLogResponse;
import com.MoveInSync.vendorManagement.entity.ActivityLog;
import com.MoveInSync.vendorManagement.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityLogRepository activityLogRepository;

    @GetMapping
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<ActivityLogResponse>> listAll(HttpServletRequest request) {
        Long vendorId = (Long) request.getAttribute("vendorId");

        List<ActivityLogResponse> logs = activityLogRepository.findByVendor_VendorId(vendorId)
                .stream()
                .map(log -> new ActivityLogResponse(
                        log.getLogId(),
                        log.getUser() != null ? log.getUser().getUserId() : null,
                        log.getVendor() != null ? log.getVendor().getVendorId() : null,
                        log.getAction(),
                        log.getDetails(),
                        log.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(logs);
    }
}
