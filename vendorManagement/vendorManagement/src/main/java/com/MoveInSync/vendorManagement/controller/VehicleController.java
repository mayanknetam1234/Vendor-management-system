package com.MoveInSync.vendorManagement.controller;

import com.MoveInSync.vendorManagement.authorization.RequiresPermission;
import com.MoveInSync.vendorManagement.dto.VehicleRequestDto;
import com.MoveInSync.vendorManagement.dto.VehicleResponseDto;
import com.MoveInSync.vendorManagement.service.interfaces.VehicleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // ✅ Add Vehicle
    @PostMapping("/add")
    @RequiresPermission("CAN_ADD_DRIVER")
    public ResponseEntity<VehicleResponseDto> addVehicle(HttpServletRequest request,
                                                         @RequestBody VehicleRequestDto vehicleRequest) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(vehicleService.addVehicle(vendorId, vehicleRequest));
    }

    // ✅ List all vehicles accessible to current vendor
    @GetMapping("/list")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<java.util.List<VehicleResponseDto>> listVehicles(HttpServletRequest request) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(vehicleService.listVehicles(vendorId));
    }

    // ✅ Get specific vehicle details
    @GetMapping("/{id}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<VehicleResponseDto> getVehicle(HttpServletRequest request, @PathVariable Long id) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(vehicleService.getVehicle(vendorId, id));
    }

    // ✅ Update vehicle details
    @PutMapping("/{id}/update")
    @RequiresPermission("CAN_ADD_DRIVER")
    public ResponseEntity<VehicleResponseDto> updateVehicle(HttpServletRequest request,
                                                            @PathVariable Long id,
                                                            @RequestBody VehicleRequestDto vehicleRequest) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(vehicleService.updateVehicle(vendorId, id, vehicleRequest));
    }

    // ✅ Change vehicle status
    @PutMapping("/{id}/status/{status}")
    @RequiresPermission("CAN_ADD_DRIVER")
    public ResponseEntity<VehicleResponseDto> changeStatus(HttpServletRequest request,
                                                           @PathVariable Long id,
                                                           @PathVariable String status) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(vehicleService.changeStatus(vendorId, id, status));
    }

    // ✅ Soft delete vehicle
    @DeleteMapping("/{id}")
    @RequiresPermission("CAN_ADD_DRIVER")
    public ResponseEntity<String> deleteVehicle(HttpServletRequest request, @PathVariable Long id) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        vehicleService.deleteVehicle(vendorId, id);
        return ResponseEntity.ok("🗑 Vehicle soft-deleted successfully!");
    }
}
