package com.tpc.tpcgestpaie.localapp.dto.numerisation.parcours;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmployeeParcoursSectionDTO {

    private String categoryCode;
    private String categoryLabel;
    private Integer displayOrder;

    private List<EmployeeParcoursItemDTO> events = new ArrayList<>();
}
