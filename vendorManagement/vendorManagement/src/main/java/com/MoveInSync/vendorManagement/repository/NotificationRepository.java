package com.MoveInSync.vendorManagement.repository;

import com.MoveInSync.vendorManagement.entity.Notification;
import com.MoveInSync.vendorManagement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByVendor(Vendor vendor);
}
