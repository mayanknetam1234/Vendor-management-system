package com.MoveInSync.vendorManagement.service.impl;

import com.MoveInSync.vendorManagement.dto.DriverRequestDto;
import com.MoveInSync.vendorManagement.dto.DriverResponseDto;
import com.MoveInSync.vendorManagement.entity.Driver;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.repository.DriverRepository;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import com.MoveInSync.vendorManagement.service.interfaces.DriverService;
import com.MoveInSync.vendorManagement.util.VendorHierarchyHelper;
import com.MoveInSync.vendorManagement.service.interfaces.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final VendorRepository vendorRepository;
    private final VendorHierarchyHelper hierarchyHelper;
    private final ActivityLogService activityLogService;

    @Override
    public DriverResponseDto addDriver(Long vendorId, DriverRequestDto request) {
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Vendor targetVendor;
        if (request.getTargetVendorId() != null) {
            targetVendor = vendorRepository.findById(request.getTargetVendorId())
                    .orElseThrow(() -> new RuntimeException("Target vendor not found"));

            if (!targetVendor.getVendorId().equals(vendorId) &&
                    !hierarchyHelper.isAncestor(actingVendor, targetVendor)) {
                throw new RuntimeException("Access denied — cannot add driver for unrelated vendor!");
            }
        } else {
            targetVendor = actingVendor; // default self
        }

        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setPhone(request.getPhone());
        driver.setLicenseNo(request.getLicenseNo());
        // assignedVehicle stored as entity in Driver; here we keep string in response only
        driver.setStatus(com.MoveInSync.vendorManagement.enumClass.DriverStatus.ACTIVE);
        driver.setCreatedAt(LocalDateTime.now());
        driver.setVendor(targetVendor);

        driverRepository.save(driver);
        return mapToResponse(driver, request.getAssignedVehicle());
    }

    @Override
    public List<DriverResponseDto> listDrivers(Long vendorId) {
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        List<Vendor> accessibleVendors = getAllChildVendors(actingVendor);
        return driverRepository.findAll().stream()
                .filter(d -> accessibleVendors.contains(d.getVendor()))
                .map(d -> mapToResponse(d, d.getAssignedVehicle() != null ? d.getAssignedVehicle().getRegistrationNo() : null))
                .collect(Collectors.toList());
    }

    @Override
    public DriverResponseDto getDriver(Long vendorId, Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!driver.getVendor().getVendorId().equals(vendorId) &&
                !hierarchyHelper.isAncestor(actingVendor, driver.getVendor())) {
            throw new RuntimeException("Access denied — cannot view driver of unrelated vendor!");
        }

        return mapToResponse(driver, driver.getAssignedVehicle() != null ? driver.getAssignedVehicle().getRegistrationNo() : null);
    }

    @Override
    public DriverResponseDto updateDriver(Long vendorId, Long driverId, DriverRequestDto request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!driver.getVendor().getVendorId().equals(vendorId) &&
                !hierarchyHelper.isAncestor(actingVendor, driver.getVendor())) {
            throw new RuntimeException("Access denied — cannot update driver of unrelated vendor!");
        }

        driver.setName(request.getName());
        driver.setPhone(request.getPhone());
        driver.setLicenseNo(request.getLicenseNo());
        // assignedVehicle update skipped in this phase

        driverRepository.save(driver);
        return mapToResponse(driver, request.getAssignedVehicle());
    }

    @Override
    public DriverResponseDto changeStatus(Long vendorId, Long driverId, String status) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!driver.getVendor().getVendorId().equals(vendorId) &&
                !hierarchyHelper.isAncestor(actingVendor, driver.getVendor())) {
            throw new RuntimeException("Access denied — cannot change status for unrelated vendor driver!");
        }

        driver.setStatus(com.MoveInSync.vendorManagement.enumClass.DriverStatus.valueOf(status.toUpperCase()));
        driverRepository.save(driver);
        // Log activity
        activityLogService.logAction(
                null,
                driver.getVendor().getVendorId(),
                com.MoveInSync.vendorManagement.enumClass.ActivityAction.DRIVER_STATUS_CHANGED,
                "Driver " + driver.getDriverId() + " status -> " + driver.getStatus().name()
        );
        return mapToResponse(driver, driver.getAssignedVehicle() != null ? driver.getAssignedVehicle().getRegistrationNo() : null);
    }

    @Override
    public void deleteDriver(Long vendorId, Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!driver.getVendor().getVendorId().equals(vendorId) &&
                !hierarchyHelper.isAncestor(actingVendor, driver.getVendor())) {
            throw new RuntimeException("Access denied — cannot delete driver of unrelated vendor!");
        }

        driver.setStatus(com.MoveInSync.vendorManagement.enumClass.DriverStatus.INACTIVE);
        driverRepository.save(driver);
        // Log activity
        activityLogService.logAction(
                null,
                driver.getVendor().getVendorId(),
                com.MoveInSync.vendorManagement.enumClass.ActivityAction.DRIVER_DELETED,
                "Soft-deleted driver " + driver.getDriverId()
        );
    }

    private List<Vendor> getAllChildVendors(Vendor parent) {
        List<Vendor> result = new ArrayList<>();
        result.add(parent);
        List<Vendor> children = vendorRepository.findByParentVendor(parent);
        for (Vendor child : children) {
            result.addAll(getAllChildVendors(child));
        }
        return result;
    }

    private DriverResponseDto mapToResponse(Driver driver, String assignedVehicleName) {
        return new DriverResponseDto(
                driver.getDriverId(),
                driver.getName(),
                driver.getPhone(),
                driver.getLicenseNo(),
                assignedVehicleName,
                driver.getStatus().name(),
                driver.getVendor().getVendorId(),
                driver.getVendor().getName()
        );
    }
}
