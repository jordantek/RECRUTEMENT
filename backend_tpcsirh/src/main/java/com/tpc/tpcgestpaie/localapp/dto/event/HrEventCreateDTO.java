package com.tpc.tpcgestpaie.localapp.dto.event;
import java.time.LocalDate;

public record HrEventCreateDTO(
        Long employeeId,
        Long contratEmployeId,
        Long companyId,
        String type,
        LocalDate eventDate,
        boolean recurring,
        LocalDate endDate
) {}