package com.MoveInSync.vendorManagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @PostMapping("/upload")
    public ResponseEntity<String> upload() {
        return ResponseEntity.ok("document upload ok");
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<String> download(@PathVariable Long id) {
        return ResponseEntity.ok("document download ok: " + id);
    }

    @GetMapping("/list/{vendorId}")
    public ResponseEntity<String> listByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok("document list ok vendor=" + vendorId);
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<String> verify(@PathVariable Long id) {
        return ResponseEntity.ok("document verify ok: " + id);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable Long id) {
        return ResponseEntity.ok("document reject ok: " + id);
    }

    @GetMapping("/expired")
    public ResponseEntity<String> expired() {
        return ResponseEntity.ok("document expired ok");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok("document delete ok: " + id);
    }

    @PostMapping("/cleanup")
    public ResponseEntity<String> cleanup() {
        return ResponseEntity.ok("document cleanup ok");
    }
}
