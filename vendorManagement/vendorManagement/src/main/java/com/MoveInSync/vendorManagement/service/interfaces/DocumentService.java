package com.MoveInSync.vendorManagement.service.interfaces;

import com.MoveInSync.vendorManagement.dto.DocumentRequest;
import com.MoveInSync.vendorManagement.dto.DocumentResponse;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    DocumentResponse upload(Long actingVendorId, DocumentRequest request) throws IOException;
    byte[] download(Long actingVendorId, Long documentId) throws IOException;
    List<DocumentResponse> listDocuments(Long actingVendorId, Long vendorId);
    DocumentResponse verifyDocument(Long actingVendorId, Long documentId);
    DocumentResponse rejectDocument(Long actingVendorId, Long documentId);
    List<DocumentResponse> listExpiredDocuments(Long actingVendorId);
    List<DocumentResponse> listVendorDocuments(Long actingVendorId, Long vendorId);
    List<DocumentResponse> listDriverDocuments(Long actingVendorId, Long driverId);
    List<DocumentResponse> listVehicleDocuments(Long actingVendorId, Long vehicleId);
}
