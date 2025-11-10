package com.MoveInSync.vendorManagement.repository;

import com.MoveInSync.vendorManagement.entity.Driver;
import com.MoveInSync.vendorManagement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    java.util.List<Driver> findByVendor(Vendor vendor);
}
