package com.tpc.tpcgestpaie.localapp.dto.soldeToutCompte;

import java.math.BigDecimal;

public class IndemniteResumeDTO {

    private Long idContratEmploye;
    private String nomEmploye;
    private String prenomEmploye;

    private BigDecimal indemniteSelonAnciennete;
    private BigDecimal montantMoyenLicenciement;
    private BigDecimal montantTotalConge;
    private BigDecimal salaireBrutBulletin;

    public IndemniteResumeDTO(Long idContratEmploye,
                              String nomEmploye,
                              String prenomEmploye,
                              BigDecimal indemniteSelonAnciennete,
                              BigDecimal montantMoyenLicenciement,
                              BigDecimal montantTotalConge,
                              BigDecimal salaireBrutBulletin) {
        this.idContratEmploye = idContratEmploye;
        this.nomEmploye = nomEmploye;
        this.prenomEmploye = prenomEmploye;
        this.indemniteSelonAnciennete = indemniteSelonAnciennete;
        this.montantMoyenLicenciement = montantMoyenLicenciement;
        this.montantTotalConge = montantTotalConge;
        this.salaireBrutBulletin = salaireBrutBulletin;
    }

    public IndemniteResumeDTO() {
    }

    public Long getIdContratEmploye() {
        return idContratEmploye;
    }

    public void setIdContratEmploye(Long idContratEmploye) {
        this.idContratEmploye = idContratEmploye;
    }

    public String getNomEmploye() {
        return nomEmploye;
    }

    public void setNomEmploye(String nomEmploye) {
        this.nomEmploye = nomEmploye;
    }

    public String getPrenomEmploye() {
        return prenomEmploye;
    }

    public void setPrenomEmploye(String prenomEmploye) {
        this.prenomEmploye = prenomEmploye;
    }

    public BigDecimal getIndemniteSelonAnciennete() {
        return indemniteSelonAnciennete;
    }

    public void setIndemniteSelonAnciennete(BigDecimal indemniteSelonAnciennete) {
        this.indemniteSelonAnciennete = indemniteSelonAnciennete;
    }

    public BigDecimal getMontantMoyenLicenciement() {
        return montantMoyenLicenciement;
    }

    public void setMontantMoyenLicenciement(BigDecimal montantMoyenLicenciement) {
        this.montantMoyenLicenciement = montantMoyenLicenciement;
    }

    public BigDecimal getMontantTotalConge() {
        return montantTotalConge;
    }

    public void setMontantTotalConge(BigDecimal montantTotalConge) {
        this.montantTotalConge = montantTotalConge;
    }

    public BigDecimal getSalaireBrutBulletin() {
        return salaireBrutBulletin;
    }

    public void setSalaireBrutBulletin(BigDecimal salaireBrutBulletin) {
        this.salaireBrutBulletin = salaireBrutBulletin;
    }
}
