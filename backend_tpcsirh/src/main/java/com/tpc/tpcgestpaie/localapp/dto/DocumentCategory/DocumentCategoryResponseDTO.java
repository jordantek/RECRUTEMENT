package com.tpc.tpcgestpaie.localapp.dto.DocumentCategory;

import java.time.LocalDateTime;

public record DocumentCategoryResponseDTO(
        Long id,
        String name,
        String code,
        String description,
        String color,
        Integer retentionPeriod,
        Boolean isActive,
        LocalDateTime createdAt
) {}