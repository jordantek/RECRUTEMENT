package com.tpc.tpcgestpaie.localapp.dto.Documents;

import java.util.List;

public record DocumentCreationDTO(
        Long companyId,
        Long employeeId,
        List<DocumentRequestDTO> documents
) {}