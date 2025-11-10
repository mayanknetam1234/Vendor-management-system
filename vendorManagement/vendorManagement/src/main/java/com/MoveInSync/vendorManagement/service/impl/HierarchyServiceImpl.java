package com.MoveInSync.vendorManagement.service.impl;

import com.MoveInSync.vendorManagement.dto.*;
import com.MoveInSync.vendorManagement.entity.Driver;
import com.MoveInSync.vendorManagement.entity.Vehicle;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.repository.DriverRepository;
import com.MoveInSync.vendorManagement.repository.VehicleRepository;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import com.MoveInSync.vendorManagement.service.interfaces.HierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HierarchyServiceImpl implements HierarchyService {

    private final VendorRepository vendorRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public HierarchyNode getVendorHierarchy(Long vendorId) {
        Vendor rootVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        return buildHierarchyNode(rootVendor);
    }

    private HierarchyNode buildHierarchyNode(Vendor vendor) {
        List<DriverInfo> drivers = driverRepository.findByVendor(vendor)
                .stream()
                .map(d -> new DriverInfo(d.getDriverId(), d.getName(), d.getStatus().name()))
                .collect(Collectors.toList());

        List<VehicleInfo> vehicles = vehicleRepository.findByVendor(vendor)
                .stream()
                .map(v -> new VehicleInfo(v.getVehicleId(), v.getRegistrationNo(), v.getStatus()))
                .collect(Collectors.toList());

        List<HierarchyNode> children = vendorRepository.findByParentVendor(vendor)
                .stream()
                .map(this::buildHierarchyNode)
                .collect(Collectors.toList());

        return new HierarchyNode(
                vendor.getVendorId(),
                vendor.getName(),
                vendor.getStatus().name(),
                drivers,
                vehicles,
                children
        );
    }

    @Override
    public HierarchyStatsResponse getHierarchyStats(Long vendorId) {
        Vendor rootVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        List<Vendor> allVendors = getAllChildVendors(rootVendor);

        long totalDrivers = allVendors.stream()
                .mapToLong(v -> driverRepository.findByVendor(v).size())
                .sum();

        long totalVehicles = allVendors.stream()
                .mapToLong(v -> vehicleRepository.findByVendor(v).size())
                .sum();

        long blockedVendors = allVendors.stream()
                .filter(v -> com.MoveInSync.vendorManagement.enumClass.VendorStatus.BLOCKED.equals(v.getStatus()))
                .count();

        long inactiveDrivers = allVendors.stream()
                .flatMap(v -> driverRepository.findByVendor(v).stream())
                .filter(d -> com.MoveInSync.vendorManagement.enumClass.DriverStatus.INACTIVE.equals(d.getStatus()))
                .count();

        long inactiveVehicles = allVendors.stream()
                .flatMap(v -> vehicleRepository.findByVendor(v).stream())
                .filter(veh -> "inactive".equalsIgnoreCase(veh.getStatus()))
                .count();

        return new HierarchyStatsResponse(
                (long) allVendors.size(),
                totalDrivers,
                totalVehicles,
                blockedVendors,
                inactiveDrivers,
                inactiveVehicles
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
}
