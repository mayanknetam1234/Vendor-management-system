package com.MoveInSync.vendorManagement.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleResponseDto {
    private Long roleId;
    private String name;
    private String description;
    private List<String> permissions; // permission names
    private LocalDateTime createdAt;
}
