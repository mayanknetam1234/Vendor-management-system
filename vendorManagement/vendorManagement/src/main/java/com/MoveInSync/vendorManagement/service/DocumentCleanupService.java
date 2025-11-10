package com.MoveInSync.vendorManagement.service;

import com.MoveInSync.vendorManagement.entity.Document;
import com.MoveInSync.vendorManagement.repository.DocumentRepository;
import com.MoveInSync.vendorManagement.service.interfaces.ActivityLogService;
import com.MoveInSync.vendorManagement.enumClass.ActivityAction;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentCleanupService {

    private final DocumentRepository documentRepository;
    private final ActivityLogService activityLogService;

    // 🕒 Runs daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredDocuments() {
        List<Document> expiredDocs = documentRepository.findByExpiryDateBefore(LocalDate.now());

        for (Document doc : expiredDocs) {
            try {
                File file = new File(doc.getFilePath());
                if (file.exists()) file.delete();

                activityLogService.logAction(
                        null,
                        doc.getVendor().getVendorId(),
                        ActivityAction.DOCUMENT_DELETED,
                        "Deleted expired document: " + doc.getFileName()
                );

                documentRepository.delete(doc);

            } catch (Exception e) {
                System.err.println("Failed to delete document: " + doc.getFileName());
            }
        }

        System.out.println("🧹 Expired document cleanup completed at " + LocalDateTime.now());
    }
}
