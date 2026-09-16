package com.tpc.tpcgestpaie.localapp.dto.Documents;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record DocumentRequestDTO(
        String reference,
        Long modelId,
        String title,
        String description,
        LocalDate signatureDate,
        LocalDate validFrom,
        LocalDate validTo,
        MultipartFile file
) {}