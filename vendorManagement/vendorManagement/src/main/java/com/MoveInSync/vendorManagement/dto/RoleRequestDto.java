package com.MoveInSync.vendorManagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleRequestDto {
    private String name;
    private String description;
    private List<Long> permissionIds; // Optional when creating
}
