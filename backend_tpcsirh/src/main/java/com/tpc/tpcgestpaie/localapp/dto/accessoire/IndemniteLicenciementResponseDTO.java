package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import com.tpc.tpcgestpaie.localapp.model.IndemniteLicenciement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IndemniteLicenciementResponseDTO {

    private Long id;
    private Long idContratEmploye;
    private Long idCompany;
    private String typeLicencement;
    private String moisCalculSalaire;  // String "yyyy-MM"
    private BigDecimal indemniteSelonAnciennete;
    private double anciennete;
    private BigDecimal montantMoyen;
    private BigDecimal montantTotal;
    private String montantsMensuels;  // JSON string

    private String nomEmploye;
    private String prenomEmploye;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeur depuis entité
    public IndemniteLicenciementResponseDTO(IndemniteLicenciement entity) {
        this.id = entity.getId();
        this.idContratEmploye = entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null;
        this.idCompany = entity.getCompany() != null ? entity.getCompany().getId() : null;
        this.typeLicencement = entity.getTypeLicencement();
        this.moisCalculSalaire = entity.getMoisCalculSalaire() != null ? entity.getMoisCalculSalaire().toString() : null;
        this.indemniteSelonAnciennete = entity.getIndemniteSelonAnciennete();
        this.anciennete = entity.getAnciennete();
        this.montantMoyen = entity.getMontantMoyen();
        this.montantTotal = entity.getMontantTotal();
        this.montantsMensuels = entity.getMontantsMensuels();

        // Récupération nom/prénom si possible
        if (entity.getContratEmploye() != null && entity.getContratEmploye().getEmploye() != null) {
            this.nomEmploye = entity.getContratEmploye().getEmploye().getNom();
            this.prenomEmploye = entity.getContratEmploye().getEmploye().getPrenom();
        }

        this.createdAt = entity.getCreated_at();
        this.updatedAt = entity.getUpdated_at();
    }

    // getters / setters...
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdContratEmploye() {
        return idContratEmploye;
    }

    public void setIdContratEmploye(Long idContratEmploye) {
        this.idContratEmploye = idContratEmploye;
    }

    public Long getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(Long idCompany) {
        this.idCompany = idCompany;
    }

    public String getTypeLicencement() {
        return typeLicencement;
    }

    public void setTypeLicencement(String typeLicencement) {
        this.typeLicencement = typeLicencement;
    }

    public String getMoisCalculSalaire() {
        return moisCalculSalaire;
    }

    public void setMoisCalculSalaire(String moisCalculSalaire) {
        this.moisCalculSalaire = moisCalculSalaire;
    }

    public BigDecimal getIndemniteSelonAnciennete() {
        return indemniteSelonAnciennete;
    }

    public void setIndemniteSelonAnciennete(BigDecimal indemniteSelonAnciennete) {
        this.indemniteSelonAnciennete = indemniteSelonAnciennete;
    }

    public double getAnciennete() {
        return anciennete;
    }

    public void setAnciennete(double anciennete) {
        this.anciennete = anciennete;
    }

    public BigDecimal getMontantMoyen() {
        return montantMoyen;
    }

    public void setMontantMoyen(BigDecimal montantMoyen) {
        this.montantMoyen = montantMoyen;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getMontantsMensuels() {
        return montantsMensuels;
    }

    public void setMontantsMensuels(String montantsMensuels) {
        this.montantsMensuels = montantsMensuels;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
