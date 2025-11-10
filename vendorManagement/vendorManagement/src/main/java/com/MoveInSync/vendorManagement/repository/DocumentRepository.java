package com.MoveInSync.vendorManagement.repository;

import com.MoveInSync.vendorManagement.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
}
