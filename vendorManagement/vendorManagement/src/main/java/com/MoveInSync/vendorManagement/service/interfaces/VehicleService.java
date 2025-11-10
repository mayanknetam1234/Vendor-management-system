package com.MoveInSync.vendorManagement.service.interfaces;

import com.MoveInSync.vendorManagement.dto.VehicleRequestDto;
import com.MoveInSync.vendorManagement.dto.VehicleResponseDto;

import java.util.List;

public interface VehicleService {
    VehicleResponseDto addVehicle(Long vendorId, VehicleRequestDto request);
    List<VehicleResponseDto> listVehicles(Long vendorId);
    VehicleResponseDto getVehicle(Long vendorId, Long vehicleId);
    VehicleResponseDto updateVehicle(Long vendorId, Long vehicleId, VehicleRequestDto request);
    VehicleResponseDto changeStatus(Long vendorId, Long vehicleId, String status);
    void deleteVehicle(Long vendorId, Long vehicleId);
}
