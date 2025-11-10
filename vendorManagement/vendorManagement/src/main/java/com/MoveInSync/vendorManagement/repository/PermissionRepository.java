package com.MoveInSync.vendorManagement.repository;

import com.MoveInSync.vendorManagement.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByName(String name);
    Optional<Permission> findByName(String name);
    List<Permission> findByNameIn(List<String> names);
}
