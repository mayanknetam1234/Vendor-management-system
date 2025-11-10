package com.MoveInSync.vendorManagement.repository;

import com.MoveInSync.vendorManagement.entity.Role;
import com.MoveInSync.vendorManagement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);


}
