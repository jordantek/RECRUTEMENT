package com.tpc.tpcgestpaie.localapp.dto.dashboard;

import java.util.Map;

public class PaieDashboardDTO {

    // Clé = statut (VALIDATED / PENDING), valeur = totaux pour ce statut
    private Map<String, PaieParStatutDTO> parStatut;

    // Constructeurs
    public PaieDashboardDTO() {}

    public PaieDashboardDTO(Map<String, PaieParStatutDTO> parStatut) {
        this.parStatut = parStatut;
    }

    // Getters / Setters
    public Map<String, PaieParStatutDTO> getParStatut() { return parStatut; }
    public void setParStatut(Map<String, PaieParStatutDTO> parStatut) { this.parStatut = parStatut; }
}
