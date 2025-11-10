package com.MoveInSync.vendorManagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @PostMapping("/add")
    public ResponseEntity<String> addDriver() {
        return ResponseEntity.ok("driver add ok");
    }

    @GetMapping("/list")
    public ResponseEntity<String> listDrivers() {
        return ResponseEntity.ok("driver list ok");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getDriver(@PathVariable Long id) {
        return ResponseEntity.ok("driver get ok: " + id);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<String> updateDriver(@PathVariable Long id) {
        return ResponseEntity.ok("driver update ok: " + id);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveDriver(@PathVariable Long id) {
        return ResponseEntity.ok("driver approve ok: " + id);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectDriver(@PathVariable Long id) {
        return ResponseEntity.ok("driver reject ok: " + id);
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable String status) {
        return ResponseEntity.ok("driver status change ok: " + id + " -> " + status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable Long id) {
        return ResponseEntity.ok("driver delete ok: " + id);
    }
}
