package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DocumentExplorerDTO {
    private Long employeeId;
    private String employeeFullName;
    private String employeeMatricule;
    private String position;
    private List<CategoryWithDocumentsDTO> categories;
}
