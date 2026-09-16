package com.tpc.tpcgestpaie.localapp.dto.contrat;
import java.time.LocalDate;

public class ArretContratDTO {

    private Long contratId;
    private Long motifId;
    private LocalDate dateArret; // date de l'arrêt (optionnelle, default = aujourd'hui)

    public ArretContratDTO() {}

    public ArretContratDTO( Long contratId,Long motifId , LocalDate dateArret) {
        this.contratId = contratId;
        this.motifId = motifId;
        this.dateArret = dateArret;
    }

    public Long getContratId() {
        return contratId;
    }

    public void setContratId(Long contratId) {
        this.contratId = contratId;
    }

    public Long getMotifId() {
        return motifId;
    }

    public void setMotifId(Long motifId) {
        this.motifId = motifId;
    }

    public LocalDate getDateArret() {
        return dateArret;
    }

    public void setDateArret(LocalDate dateArret) {
        this.dateArret = dateArret;
    }
}
