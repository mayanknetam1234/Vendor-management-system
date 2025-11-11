package com.MoveInSync.vendorManagement.controller;

import com.MoveInSync.vendorManagement.authorization.RequiresPermission;
import com.MoveInSync.vendorManagement.dto.DriverRequestDto;
import com.MoveInSync.vendorManagement.dto.DriverResponseDto;
import com.MoveInSync.vendorManagement.service.interfaces.DriverService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // ✅ 1️⃣ Add driver (auto-links to current vendor)
    @PostMapping("/add")
    @RequiresPermission("CAN_ADD_DRIVER")
    public ResponseEntity<DriverResponseDto> addDriver(HttpServletRequest request,
                                                       @RequestBody DriverRequestDto driverRequest) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        DriverResponseDto response = driverService.addDriver(vendorId, driverRequest);
        return ResponseEntity.ok(response);
    }

    // ✅ 2️⃣ Get all drivers under current vendor
    @GetMapping("/list")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<java.util.List<DriverResponseDto>> listDrivers(HttpServletRequest request) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(driverService.listDrivers(vendorId));
    }

    // ✅ 3️⃣ Fetch driver details
    @GetMapping("/{id}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<DriverResponseDto> getDriver(HttpServletRequest request, @PathVariable Long id) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(driverService.getDriver(vendorId, id));
    }

    // ✅ 4️⃣ Update driver details
    @PutMapping("/{id}/update")
    @RequiresPermission("CAN_ADD_DRIVER")
    public ResponseEntity<DriverResponseDto> updateDriver(HttpServletRequest request,
                                                          @PathVariable Long id,
                                                          @RequestBody DriverRequestDto driverRequest) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(driverService.updateDriver(vendorId, id, driverRequest));
    }

    // ✅ 5️⃣ Change driver status (activate / deactivate / block)
    @PutMapping("/{id}/status/{status}")
    @RequiresPermission("CAN_ADD_DRIVER")
    public ResponseEntity<DriverResponseDto> changeStatus(HttpServletRequest request,
                                                          @PathVariable Long id,
                                                          @PathVariable String status) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(driverService.changeStatus(vendorId, id, status));
    }

    // ✅ 6️⃣ Soft delete driver
    @DeleteMapping("/{id}")
    @RequiresPermission("CAN_ADD_DRIVER")
    public ResponseEntity<String> deleteDriver(HttpServletRequest request, @PathVariable Long id) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        driverService.deleteDriver(vendorId, id);
        return ResponseEntity.ok("🗑 Driver soft-deleted successfully!");
    }

    @GetMapping("/tree")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<DriverResponseDto>> listAllDriversInTree(HttpServletRequest request) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(driverService.listAllDriversInTree(vendorId));
    }
}
