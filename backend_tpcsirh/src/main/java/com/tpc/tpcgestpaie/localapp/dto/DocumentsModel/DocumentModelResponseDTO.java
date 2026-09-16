package com.tpc.tpcgestpaie.localapp.dto.DocumentsModel;

import java.time.LocalDateTime;

public record DocumentModelResponseDTO(
        Long id,
        String name,
        String code,
        Long categoryId,
        String categoryName,
        String description,
        Boolean isActive,
        Boolean ocrEnabled,
        Boolean aiExtraction,
        Boolean showOnEmployeeProfile,
        Long companyId,
        LocalDateTime createdAt
) {}