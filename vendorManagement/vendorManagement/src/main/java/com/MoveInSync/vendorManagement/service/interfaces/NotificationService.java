package com.MoveInSync.vendorManagement.service.interfaces;

import com.MoveInSync.vendorManagement.entity.Document;

public interface NotificationService {
    void notifyVendorForExpiringDocument(Document document);
}
