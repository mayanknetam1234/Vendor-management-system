package com.MoveInSync.vendorManagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @GetMapping("/list")
    public ResponseEntity<String> list() {
        return ResponseEntity.ok("notifications list ok");
    }

    @PostMapping("/send")
    public ResponseEntity<String> send() {
        return ResponseEntity.ok("notifications send ok");
    }

    @GetMapping("/unread")
    public ResponseEntity<String> unread() {
        return ResponseEntity.ok("notifications unread ok");
    }

    @PutMapping("/mark-read/{id}")
    public ResponseEntity<String> markRead(@PathVariable Long id) {
        return ResponseEntity.ok("notifications mark-read ok: " + id);
    }

    @GetMapping("/expiring-docs")
    public ResponseEntity<String> expiringDocs() {
        return ResponseEntity.ok("notifications expiring-docs ok");
    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcast() {
        return ResponseEntity.ok("notifications broadcast ok");
    }
}
