package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.EmployeeDocumentDTO;
import com.tpc.tpcgestpaie.localapp.model.numerisation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SubCategoryWithDocumentsDTO {
    private Long subCategoryId;
    private String subCategoryName;
    private Boolean mandatory;
    private Boolean complete;
    private List<EmployeeDocumentDTO> documents;
}