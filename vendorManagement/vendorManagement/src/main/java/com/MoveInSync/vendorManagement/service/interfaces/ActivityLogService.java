package com.MoveInSync.vendorManagement.service.interfaces;

import com.MoveInSync.vendorManagement.enumClass.ActivityAction;

public interface ActivityLogService {
    void logAction(Long userId, Long vendorId, String action, String details);
    void logAction(Long userId, Long vendorId, ActivityAction action, String details);
}
