package com.MoveInSync.vendorManagement.controller;

import com.MoveInSync.vendorManagement.authorization.RequiresPermission;
import com.MoveInSync.vendorManagement.dto.DocumentRequest;
import com.MoveInSync.vendorManagement.dto.DocumentResponse;
import com.MoveInSync.vendorManagement.service.interfaces.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin
public class DocumentController {

    private final DocumentService documentService;

    // ✅ Upload document (multipart form via @ModelAttribute)
    @PostMapping("/upload")
    @RequiresPermission("CAN_UPLOAD_DOC")
    public ResponseEntity<DocumentResponse> upload(HttpServletRequest request,
                                                   @ModelAttribute DocumentRequest docRequest) throws IOException {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(documentService.upload(vendorId, docRequest));
    }

    // ✅ Download document
    ///
    @GetMapping("/download/{id}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<byte[]> download(HttpServletRequest request, @PathVariable Long id,@RequestParam Boolean status) throws IOException {
        Long vendorId = (Long) request.getAttribute("vendorId");
        byte[] data = documentService.download(vendorId, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    // ✅ List documents for a vendor
    @GetMapping("/list/{vendorId}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<DocumentResponse>> listDocuments(HttpServletRequest request,
                                                                @PathVariable Long vendorId) {
        Long actingVendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(documentService.listDocuments(actingVendorId, vendorId));
    }

    // ✅ Verify a document
    @PutMapping("/{id}/verify")
    @RequiresPermission("CAN_VERIFY_DOC")
    public ResponseEntity<DocumentResponse> verifyDocument(HttpServletRequest request, @PathVariable Long id) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(documentService.verifyDocument(vendorId, id));
    }

    // ✅ Reject a document
    @PutMapping("/{id}/reject")
    @RequiresPermission("CAN_VERIFY_DOC")
    public ResponseEntity<DocumentResponse> rejectDocument(HttpServletRequest request, @PathVariable Long id) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(documentService.rejectDocument(vendorId, id));
    }

    // ✅ List expired documents
    @GetMapping("/expired")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<DocumentResponse>> expired(HttpServletRequest request) {
        Long vendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(documentService.listExpiredDocuments(vendorId));
    }

    // ✅ Fetch Vendor-level documents
    @GetMapping("/vendor/{vendorId}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<DocumentResponse>> getVendorDocuments(
            HttpServletRequest request, @PathVariable Long vendorId) {
        Long actingVendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(documentService.listVendorDocuments(actingVendorId, vendorId));
    }

    // ✅ Fetch Driver-level documents
    @GetMapping("/driver/{driverId}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<DocumentResponse>> getDriverDocuments(
            HttpServletRequest request, @PathVariable Long driverId) {
        Long actingVendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(documentService.listDriverDocuments(actingVendorId, driverId));
    }

    // ✅ Fetch Vehicle-level documents
    @GetMapping("/vehicle/{vehicleId}")
    @RequiresPermission("CAN_VIEW_VENDOR")
    public ResponseEntity<List<DocumentResponse>> getVehicleDocuments(
            HttpServletRequest request, @PathVariable Long vehicleId) {
        Long actingVendorId = (Long) request.getAttribute("vendorId");
        return ResponseEntity.ok(documentService.listVehicleDocuments(actingVendorId, vehicleId));
    }
}
