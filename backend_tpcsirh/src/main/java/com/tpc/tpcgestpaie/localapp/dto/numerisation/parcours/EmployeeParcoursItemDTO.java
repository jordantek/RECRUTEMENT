package com.tpc.tpcgestpaie.localapp.dto.numerisation.parcours;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeParcoursItemDTO {

    private String categoryCode;
    private String categoryLabel;
    private Integer displayOrder;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate eventDate;
    private String label;
    private String proofFilePath;
    private Double confidenceScore;
}
