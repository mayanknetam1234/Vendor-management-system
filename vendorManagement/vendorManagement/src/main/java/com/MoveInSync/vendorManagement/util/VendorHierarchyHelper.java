package com.MoveInSync.vendorManagement.util;

import com.MoveInSync.vendorManagement.entity.Vendor;
import com.MoveInSync.vendorManagement.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VendorHierarchyHelper {

    private final VendorRepository vendorRepository;

    // Check if parentVendor is ancestor of targetVendor
    public boolean isAncestor(Vendor parentVendor, Vendor targetVendor) {
        Vendor current = targetVendor != null ? targetVendor.getParentVendor() : null;
        while (current != null) {
            if (current.getVendorId().equals(parentVendor.getVendorId())) {
                return true; // Found ancestor in chain
            }
            current = current.getParentVendor();
        }
        return false;
    }
}
