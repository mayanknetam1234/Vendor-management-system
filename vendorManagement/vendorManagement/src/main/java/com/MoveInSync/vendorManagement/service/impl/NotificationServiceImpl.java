package com.MoveInSync.vendorManagement.service.impl;

import com.MoveInSync.vendorManagement.entity.Document;
import com.MoveInSync.vendorManagement.entity.Notification;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.repository.NotificationRepository;
import com.MoveInSync.vendorManagement.service.interfaces.NotificationService;
import com.MoveInSync.vendorManagement.util.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MailUtil mailUtil;

    @Override
    public void notifyVendorForExpiringDocument(Document document) {
        Vendor vendor = document.getVendor();

        String msg = String.format(
                "Document '%s' is expiring on %s. Please renew it soon.",
                document.getFileName(),
                document.getExpiryDate()
        );

        Notification notification = new Notification();
        notification.setMessage(msg);
        notification.setType("DOCUMENT_EXPIRY");
        notification.setVendor(vendor);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        // Optionally send an email
//        mailUtil.sendMail(vendor.getEmail(), "Document Expiry Reminder", msg);
    }
}
