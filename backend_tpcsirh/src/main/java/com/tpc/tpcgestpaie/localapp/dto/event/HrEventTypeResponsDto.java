package com.tpc.tpcgestpaie.localapp.dto.event;

import java.time.LocalDateTime;

public record HrEventTypeResponsDto(
        Long id,
        String slug,
        String label,
        String description,
        String icon,
        String color,
        Boolean actionRequired,
        Boolean recurring,
        Boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
