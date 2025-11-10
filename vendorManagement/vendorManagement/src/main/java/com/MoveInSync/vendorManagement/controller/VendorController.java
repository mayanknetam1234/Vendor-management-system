package com.MoveInSync.vendorManagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @PostMapping("/create")
    public ResponseEntity<String> createVendor() {
        return ResponseEntity.ok("vendor create ok");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getVendor(@PathVariable Long id) {
        return ResponseEntity.ok("vendor get ok: " + id);
    }

    @GetMapping("/list")
    public ResponseEntity<String> listVendors() {
        return ResponseEntity.ok("vendor list ok");
    }

    @PutMapping("/{vendorId}/parent")
    public ResponseEntity<String> reassignParent(@PathVariable Long vendorId) {
        return ResponseEntity.ok("vendor reassign parent ok: " + vendorId);
    }

    @PutMapping("/{vendorId}/block")
    public ResponseEntity<String> blockVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok("vendor block ok: " + vendorId);
    }

    @PutMapping("/{vendorId}/unblock")
    public ResponseEntity<String> unblockVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok("vendor unblock ok: " + vendorId);
    }

    @DeleteMapping("/{vendorId}")
    public ResponseEntity<String> deleteVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok("vendor delete ok: " + vendorId);
    }

    @GetMapping("/permissions/{vendorId}")
    public ResponseEntity<String> getPermissions(@PathVariable Long vendorId) {
        return ResponseEntity.ok("vendor permissions get ok: " + vendorId);
    }

    @PutMapping("/permissions/{vendorId}")
    public ResponseEntity<String> updatePermissions(@PathVariable Long vendorId) {
        return ResponseEntity.ok("vendor permissions update ok: " + vendorId);
    }
}
