package com.tpc.tpcgestpaie.localapp.dto.Documents;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocumentResponseDTO(
        Long id,
        String reference,
        String title,
        String description,
        String fileName,
        String filePath,
        Long fileSize,
        String fileType,
        Long employeeId,
        Long modelId,
        String modelName,
        Long categoryId,
        String categoryName,
        LocalDate signatureDate,
        LocalDate validFrom,
        LocalDate validTo,
        Boolean isScanned,
        Long companyId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}