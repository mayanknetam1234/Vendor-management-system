package com.MoveInSync.vendorManagement.repository;

import com.MoveInSync.vendorManagement.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByVendor_VendorId(Long vendorId);
}
