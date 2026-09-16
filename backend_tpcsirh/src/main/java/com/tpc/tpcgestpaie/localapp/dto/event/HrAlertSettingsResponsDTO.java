package com.tpc.tpcgestpaie.localapp.dto.event;

public record HrAlertSettingsResponsDTO(
        Long id,
        Long companyId,
        Long userId,
        Integer upcomingEventsDays,
        Integer requiredActionsDays,
        Boolean enabled
) {
}
