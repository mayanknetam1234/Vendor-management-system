package com.MoveInSync.vendorManagement.service.impl;

import com.MoveInSync.vendorManagement.dto.DocumentRequest;
import com.MoveInSync.vendorManagement.dto.DocumentResponse;
import com.MoveInSync.vendorManagement.entity.Document;
import com.MoveInSync.vendorManagement.entity.Driver;
import com.MoveInSync.vendorManagement.entity.Vehicle;
import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.enumClass.DocumentStatus;
import com.MoveInSync.vendorManagement.enumClass.DocumentType;
import com.MoveInSync.vendorManagement.repository.DocumentRepository;
import com.MoveInSync.vendorManagement.repository.DriverRepository;
import com.MoveInSync.vendorManagement.repository.VehicleRepository;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import com.MoveInSync.vendorManagement.service.interfaces.DocumentService;
import com.MoveInSync.vendorManagement.util.VendorHierarchyHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final VendorRepository vendorRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final VendorHierarchyHelper hierarchyHelper;

    private static final String UPLOAD_BASE_PATH =
            System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

    @Override
    public DocumentResponse upload(Long actingVendorId, DocumentRequest request) throws IOException {
        Vendor targetVendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Vendor actingVendor = vendorRepository.findById(actingVendorId)
                .orElseThrow(() -> new RuntimeException("Acting vendor not found"));

        if (!targetVendor.getVendorId().equals(actingVendor.getVendorId()) &&
                !hierarchyHelper.isAncestor(actingVendor, targetVendor)) {
            throw new RuntimeException("Access denied — cannot upload for unrelated vendor!");
        }

        MultipartFile file = request.getFile();

        // ✅ Make sure directory exists
        String dirPath = UPLOAD_BASE_PATH + "vendor_" + targetVendor.getVendorId();
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create upload directory: " + dirPath);
        }

        String filePath = dirPath + File.separator + file.getOriginalFilename();
        file.transferTo(new File(filePath)); // ✅ Now safe

        // 🧩 Save document metadata
        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setFilePath(filePath);
        doc.setType(DocumentType.valueOf(request.getType().toUpperCase()));
        doc.setStatus(DocumentStatus.PENDING);
        doc.setUploadedAt(LocalDateTime.now());

        if (request.getExpiryDate() != null) {
            doc.setExpiryDate(request.getExpiryDate().toLocalDate());
        }

        doc.setVendor(targetVendor);
        if (request.getDriverId() != null) {
            Driver driver = driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            doc.setDriver(driver);
        }
        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            doc.setVehicle(vehicle);
        }

        documentRepository.save(doc);
        return mapToResponse(doc);
    }

    @Override
    public byte[] download(Long actingVendorId, Long documentId) throws IOException {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Vendor vendor = doc.getVendor();
        Vendor actingVendor = vendorRepository.findById(actingVendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!vendor.getVendorId().equals(actingVendor.getVendorId()) &&
                !hierarchyHelper.isAncestor(actingVendor, vendor)) {
            throw new RuntimeException("Access denied — cannot download document!");
        }

        return FileCopyUtils.copyToByteArray(new FileInputStream(doc.getFilePath()));
    }

    @Override
    public List<DocumentResponse> listDocuments(Long actingVendorId, Long vendorId) {
        Vendor targetVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        Vendor actingVendor = vendorRepository.findById(actingVendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!targetVendor.getVendorId().equals(actingVendor.getVendorId()) &&
                !hierarchyHelper.isAncestor(actingVendor, targetVendor)) {
            throw new RuntimeException("Access denied — cannot view documents!");
        }

        return documentRepository.findByVendor(targetVendor)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentResponse verifyDocument(Long actingVendorId, Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        doc.setStatus(DocumentStatus.VERIFIED);
        documentRepository.save(doc);
        return mapToResponse(doc);
    }

    @Override
    public DocumentResponse rejectDocument(Long actingVendorId, Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        doc.setStatus(DocumentStatus.REJECTED);
        documentRepository.save(doc);
        return mapToResponse(doc);
    }

    @Override
    public List<DocumentResponse> listExpiredDocuments(Long actingVendorId) {
        List<Document> expiredDocs = documentRepository.findByExpiryDateBefore(LocalDate.now());
        return expiredDocs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponse> listVendorDocuments(Long actingVendorId, Long vendorId) {
        Vendor targetVendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        Vendor actingVendor = vendorRepository.findById(actingVendorId)
                .orElseThrow(() -> new RuntimeException("Acting vendor not found"));

        if (!targetVendor.getVendorId().equals(actingVendor.getVendorId()) &&
                !hierarchyHelper.isAncestor(actingVendor, targetVendor)) {
            throw new RuntimeException("Access denied — cannot view vendor documents!");
        }

        return documentRepository.findByVendor(targetVendor)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponse> listDriverDocuments(Long actingVendorId, Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Vendor driverVendor = driver.getVendor();
        Vendor actingVendor = vendorRepository.findById(actingVendorId)
                .orElseThrow(() -> new RuntimeException("Acting vendor not found"));

        if (!driverVendor.getVendorId().equals(actingVendor.getVendorId()) &&
                !hierarchyHelper.isAncestor(actingVendor, driverVendor)) {
            throw new RuntimeException("Access denied — cannot view driver documents!");
        }

        return documentRepository.findByDriver(driver)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponse> listVehicleDocuments(Long actingVendorId, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        Vendor vehicleVendor = vehicle.getVendor();
        Vendor actingVendor = vendorRepository.findById(actingVendorId)
                .orElseThrow(() -> new RuntimeException("Acting vendor not found"));

        if (!vehicleVendor.getVendorId().equals(actingVendor.getVendorId()) &&
                !hierarchyHelper.isAncestor(actingVendor, vehicleVendor)) {
            throw new RuntimeException("Access denied — cannot view vehicle documents!");
        }

        return documentRepository.findByVehicle(vehicle)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private DocumentResponse mapToResponse(Document doc) {
        return new DocumentResponse(
                doc.getDocumentId(),
                doc.getFileName(),
                doc.getFilePath(),
                doc.getType().name(),
                doc.getStatus().name(),
                doc.getUploadedAt(),
                doc.getExpiryDate() != null ? doc.getExpiryDate().atStartOfDay() : null,
                doc.getVendor().getVendorId(),
                doc.getDriver() != null ? doc.getDriver().getDriverId() : null,
                doc.getVehicle() != null ? doc.getVehicle().getVehicleId() : null
        );
    }
}
