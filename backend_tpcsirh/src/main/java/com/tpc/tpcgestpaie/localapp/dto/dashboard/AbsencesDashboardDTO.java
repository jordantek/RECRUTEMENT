package com.tpc.tpcgestpaie.localapp.dto.dashboard;

import java.util.Map;

public class AbsencesDashboardDTO {

    private Map<String, Long> absencesEnCoursParType;
    private Map<String, Long> absencesVsConges;
    private Long nombreAccidentsTravail;


    public AbsencesDashboardDTO() {}

    public AbsencesDashboardDTO(Map<String, Long> absencesEnCoursParType,
                                Map<String, Long> absencesVsConges,
                                Long nombreAccidentsTravail) {
        this.absencesEnCoursParType = absencesEnCoursParType;
        this.absencesVsConges = absencesVsConges;
        this.nombreAccidentsTravail = nombreAccidentsTravail;
    }

    public Map<String, Long> getAbsencesEnCoursParType() {
        return absencesEnCoursParType;
    }

    public void setAbsencesEnCoursParType(Map<String, Long> absencesEnCoursParType) {
        this.absencesEnCoursParType = absencesEnCoursParType;
    }

    public Map<String, Long> getAbsencesVsConges() {
        return absencesVsConges;
    }

    public void setAbsencesVsConges(Map<String, Long> absencesVsConges) {
        this.absencesVsConges = absencesVsConges;
    }

    public Long getNombreAccidentsTravail() {
        return nombreAccidentsTravail;
    }

    public void setNombreAccidentsTravail(Long nombreAccidentsTravail) {
        this.nombreAccidentsTravail = nombreAccidentsTravail;
    }
}
