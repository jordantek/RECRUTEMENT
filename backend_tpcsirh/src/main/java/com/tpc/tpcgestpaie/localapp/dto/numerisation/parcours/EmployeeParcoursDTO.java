package com.tpc.tpcgestpaie.localapp.dto.numerisation.parcours;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmployeeParcoursDTO {

    private Long employeId;
    private String nom;
    private String prenom;


    private List<EmployeeParcoursSectionDTO> sections;
}
