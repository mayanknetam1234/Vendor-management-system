package com.MoveInSync.vendorManagement.repository;

import com.MoveInSync.vendorManagement.entity.Vehicle;
import com.MoveInSync.vendorManagement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    java.util.List<Vehicle> findByVendor(Vendor vendor);
    Optional<Vehicle> findByRegistrationNo(String registrationNo);
}
