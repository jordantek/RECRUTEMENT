package com.tpc.tpcgestpaie.localapp.dto.numerisation.parcours;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventCategoryDTO {

    private String code;
    private String label;

    private List<EmployeeEventDTO> events;
}
