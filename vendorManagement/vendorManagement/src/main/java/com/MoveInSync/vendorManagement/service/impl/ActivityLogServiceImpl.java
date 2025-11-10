package com.MoveInSync.vendorManagement.service.impl;

import com.MoveInSync.vendorManagement.entity.ActivityLog;
import com.MoveInSync.vendorManagement.entity.User;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.repository.ActivityLogRepository;
import com.MoveInSync.vendorManagement.repository.UserRepository;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import com.MoveInSync.vendorManagement.service.interfaces.ActivityLogService;
import com.MoveInSync.vendorManagement.enumClass.ActivityAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;

    @Override
    public void logAction(Long userId, Long vendorId, String action, String details) {
        ActivityLog log = new ActivityLog();
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            log.setUser(user);
        }
        if (vendorId != null) {
            Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
            log.setVendor(vendor);
        }
        log.setAction(action);
        log.setDetails(details);
        log.setEntityType(null);
        log.setEntityId(null);
        log.setCreatedAt(LocalDateTime.now());
        activityLogRepository.save(log);
    }

    @Override
    public void logAction(Long userId, Long vendorId, ActivityAction action, String details) {
        ActivityLog log = new ActivityLog();
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            log.setUser(user);
        }
        if (vendorId != null) {
            Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
            log.setVendor(vendor);
        }
        log.setAction(action.name());
        log.setActionEnum(action);
        log.setDetails(details);
        log.setEntityType(null);
        log.setEntityId(null);
        log.setCreatedAt(LocalDateTime.now());
        activityLogRepository.save(log);
    }
}
