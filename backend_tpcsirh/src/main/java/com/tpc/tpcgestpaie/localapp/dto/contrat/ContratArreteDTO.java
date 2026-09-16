package com.tpc.tpcgestpaie.localapp.dto.contrat;

import java.time.LocalDate;

public class ContratArreteDTO {
    private Long id;
    private String motif;
    private LocalDate dateArret;
    private String status;
    // Constructeur complet
    public ContratArreteDTO(Long id, String motifArretContrat, LocalDate dateArretContrat, String statusContrat) {
        this.id = id;
        this.motif = motifArretContrat;
        this.dateArret = dateArretContrat;
        this.status = statusContrat;
    }

    // constructeur, getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDate getDateArret() {
        return dateArret;
    }

    public void setDateArret(LocalDate dateArret) {
        this.dateArret = dateArret;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
