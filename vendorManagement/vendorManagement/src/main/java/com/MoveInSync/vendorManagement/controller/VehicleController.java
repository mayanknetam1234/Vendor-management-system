package com.MoveInSync.vendorManagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @PostMapping("/register")
    public ResponseEntity<String> registerVehicle() {
        return ResponseEntity.ok("vehicle register ok");
    }

    @GetMapping("/list")
    public ResponseEntity<String> listVehicles() {
        return ResponseEntity.ok("vehicle list ok");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getVehicle(@PathVariable Long id) {
        return ResponseEntity.ok("vehicle get ok: " + id);
    }

    @PutMapping("/{id}/assign-driver/{driverId}")
    public ResponseEntity<String> assignDriver(@PathVariable Long id, @PathVariable Long driverId) {
        return ResponseEntity.ok("vehicle assign-driver ok: vehicle=" + id + ", driver=" + driverId);
    }

    @PutMapping("/{id}/unassign-driver")
    public ResponseEntity<String> unassignDriver(@PathVariable Long id) {
        return ResponseEntity.ok("vehicle unassign-driver ok: " + id);
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable String status) {
        return ResponseEntity.ok("vehicle status change ok: " + id + " -> " + status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
        return ResponseEntity.ok("vehicle delete ok: " + id);
    }
}
