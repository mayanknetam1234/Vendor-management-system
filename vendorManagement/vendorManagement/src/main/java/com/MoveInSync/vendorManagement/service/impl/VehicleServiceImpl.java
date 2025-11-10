package com.MoveInSync.vendorManagement.service.impl;

import com.MoveInSync.vendorManagement.dto.VehicleRequestDto;
import com.MoveInSync.vendorManagement.dto.VehicleResponseDto;
import com.MoveInSync.vendorManagement.entity.Vehicle;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.repository.VehicleRepository;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import com.MoveInSync.vendorManagement.service.interfaces.VehicleService;
import com.MoveInSync.vendorManagement.util.VendorHierarchyHelper;
import com.MoveInSync.vendorManagement.service.interfaces.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VendorRepository vendorRepository;
    private final VendorHierarchyHelper hierarchyHelper;
    private final ActivityLogService activityLogService;

    @Override
    public VehicleResponseDto addVehicle(Long vendorId, VehicleRequestDto request) {
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Vendor targetVendor;
        if (request.getTargetVendorId() != null) {
            targetVendor = vendorRepository.findById(request.getTargetVendorId())
                    .orElseThrow(() -> new RuntimeException("Target vendor not found"));

            if (!targetVendor.getVendorId().equals(vendorId) &&
                    !hierarchyHelper.isAncestor(actingVendor, targetVendor)) {
                throw new RuntimeException("Access denied — cannot add vehicle for unrelated vendor!");
            }
        } else {
            targetVendor = actingVendor;
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNo(request.getRegistrationNo());
        vehicle.setModel(request.getModel());
        vehicle.setType(request.getType());
        vehicle.setStatus("active");
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setVendor(targetVendor);

        vehicleRepository.save(vehicle);
        return mapToResponse(vehicle);
    }

    @Override
    public List<VehicleResponseDto> listVehicles(Long vendorId) {
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        List<Vendor> accessibleVendors = getAllChildVendors(actingVendor);
        return vehicleRepository.findAll().stream()
                .filter(v -> accessibleVendors.contains(v.getVendor()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponseDto getVehicle(Long vendorId, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!vehicle.getVendor().getVendorId().equals(vendorId) &&
                !hierarchyHelper.isAncestor(actingVendor, vehicle.getVendor())) {
            throw new RuntimeException("Access denied — cannot view vehicle of unrelated vendor!");
        }

        return mapToResponse(vehicle);
    }

    @Override
    public VehicleResponseDto updateVehicle(Long vendorId, Long vehicleId, VehicleRequestDto request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!vehicle.getVendor().getVendorId().equals(vendorId) &&
                !hierarchyHelper.isAncestor(actingVendor, vehicle.getVendor())) {
            throw new RuntimeException("Access denied — cannot update vehicle of unrelated vendor!");
        }

        vehicle.setModel(request.getModel());
        vehicle.setType(request.getType());
        vehicle.setRegistrationNo(request.getRegistrationNo());

        vehicleRepository.save(vehicle);
        return mapToResponse(vehicle);
    }

    @Override
    public VehicleResponseDto changeStatus(Long vendorId, Long vehicleId, String status) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!vehicle.getVendor().getVendorId().equals(vendorId) &&
                !hierarchyHelper.isAncestor(actingVendor, vehicle.getVendor())) {
            throw new RuntimeException("Access denied — cannot change status for unrelated vendor!");
        }

        vehicle.setStatus(status);
        vehicleRepository.save(vehicle);
        // Log activity
        activityLogService.logAction(
                null,
                vehicle.getVendor().getVendorId(),
                com.MoveInSync.vendorManagement.enumClass.ActivityAction.VEHICLE_STATUS_CHANGED,
                "Vehicle " + vehicle.getVehicleId() + " status -> " + vehicle.getStatus()
        );
        return mapToResponse(vehicle);
    }

    @Override
    public void deleteVehicle(Long vendorId, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        Vendor actingVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!vehicle.getVendor().getVendorId().equals(vendorId) &&
                !hierarchyHelper.isAncestor(actingVendor, vehicle.getVendor())) {
            throw new RuntimeException("Access denied — cannot delete vehicle of unrelated vendor!");
        }

        vehicle.setStatus("inactive");
        vehicleRepository.save(vehicle);
        // Log activity
        activityLogService.logAction(
                null,
                vehicle.getVendor().getVendorId(),
                com.MoveInSync.vendorManagement.enumClass.ActivityAction.VEHICLE_DELETED,
                "Soft-deleted vehicle " + vehicle.getVehicleId()
        );
    }

    private VehicleResponseDto mapToResponse(Vehicle vehicle) {
        return new VehicleResponseDto(
                vehicle.getVehicleId(),
                vehicle.getRegistrationNo(),
                vehicle.getModel(),
                vehicle.getType(),
                vehicle.getStatus(),
                vehicle.getVendor().getVendorId(),
                vehicle.getVendor().getName()
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
