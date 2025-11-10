package com.MoveInSync.vendorManagement.repository;

import com.MoveInSync.vendorManagement.entity.Document;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.entity.Driver;
import com.MoveInSync.vendorManagement.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByVendor(Vendor vendor);
    List<Document> findByExpiryDateBefore(LocalDate date);
    List<Document> findByDriver(Driver driver);
    List<Document> findByVehicle(Vehicle vehicle);
}
