package com.MoveInSync.vendorManagement.service.interfaces;

import com.MoveInSync.vendorManagement.dto.DriverRequestDto;
import com.MoveInSync.vendorManagement.dto.DriverResponseDto;

import java.util.List;

public interface DriverService {
    DriverResponseDto addDriver(Long vendorId, DriverRequestDto request);
    List<DriverResponseDto> listDrivers(Long vendorId);
    DriverResponseDto getDriver(Long vendorId, Long driverId);
    DriverResponseDto updateDriver(Long vendorId, Long driverId, DriverRequestDto request);
    DriverResponseDto changeStatus(Long vendorId, Long driverId, String status);
    void deleteDriver(Long vendorId, Long driverId);

    List<DriverResponseDto> listAllDriversInTree(Long vendorId);
}
