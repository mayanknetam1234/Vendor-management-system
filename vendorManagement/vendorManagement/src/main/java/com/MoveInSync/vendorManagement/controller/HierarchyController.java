package com.MoveInSync.vendorManagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hierarchy")
public class HierarchyController {

    @GetMapping("/{vendorId}")
    public ResponseEntity<String> fullTree(@PathVariable Long vendorId) {
        return ResponseEntity.ok("hierarchy full ok vendor=" + vendorId);
    }

    @GetMapping("/subtree/{vendorId}")
    public ResponseEntity<String> subTree(@PathVariable Long vendorId) {
        return ResponseEntity.ok("hierarchy subtree ok vendor=" + vendorId);
    }

    @GetMapping("/drivers/{vendorId}")
    public ResponseEntity<String> drivers(@PathVariable Long vendorId) {
        return ResponseEntity.ok("hierarchy drivers ok vendor=" + vendorId);
    }

    @GetMapping("/vehicles/{vendorId}")
    public ResponseEntity<String> vehicles(@PathVariable Long vendorId) {
        return ResponseEntity.ok("hierarchy vehicles ok vendor=" + vendorId);
    }

    @GetMapping("/stats/{vendorId}")
    public ResponseEntity<String> stats(@PathVariable Long vendorId) {
        return ResponseEntity.ok("hierarchy stats ok vendor=" + vendorId);
    }
}
