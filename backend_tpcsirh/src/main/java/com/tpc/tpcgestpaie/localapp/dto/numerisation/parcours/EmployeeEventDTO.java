package com.tpc.tpcgestpaie.localapp.dto.numerisation.parcours;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class EmployeeEventDTO {

    private String label;

    private LocalDate eventDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private String documentPath;
}
