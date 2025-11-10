package com.MoveInSync.vendorManagement.scheduler;

import com.MoveInSync.vendorManagement.entity.Document;
import com.MoveInSync.vendorManagement.repository.DocumentRepository;
import com.MoveInSync.vendorManagement.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentExpiryScheduler {

    private final DocumentRepository documentRepository;
    private final NotificationService notificationService;

    // 🕒 Run daily at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkForExpiringDocuments() {
        LocalDate now = LocalDate.now();
        LocalDate sevenDaysLater = now.plusDays(7);

        List<Document> expiringDocs = documentRepository.findAll().stream()
                .filter(doc -> doc.getExpiryDate() != null &&
                        doc.getExpiryDate().isAfter(now) &&
                        doc.getExpiryDate().isBefore(sevenDaysLater))
                .toList();

        for (Document doc : expiringDocs) {
            notificationService.notifyVendorForExpiringDocument(doc);
        }

        System.out.println("✅ Checked for expiring documents at " + now);
    }
}
