package com.tpc.tpcgestpaie.localapp.dto;

import com.tpc.tpcgestpaie.localapp.model.TraitementSalaireLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TraitementSalaireLogDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private Long departementId;
    private String departementName;
    private String mois;
    private String statut;
    private String message;

    private Long declencheurId;
    private Long verificateurId;
    private LocalDateTime dateVerification;
    private Long validateurId;
    private LocalDateTime dateValidation;
    private Long rejetParId;
    private LocalDateTime dateRejet;
    private String motifRejet;

    private LocalDateTime dateTraitement;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TraitementSalaireLogDTO toDTO(TraitementSalaireLog entity) {
        TraitementSalaireLogDTO dto = new TraitementSalaireLogDTO();
        dto.setId(entity.getId());
        dto.setCompanyId(entity.getCompanyId());
        dto.setCompanyName(entity.getCompanyName());
        dto.setDepartementId(entity.getDepartementId());
        dto.setDepartementName(entity.getDepartementName());
        dto.setMois(entity.getMois());
        dto.setStatut(entity.getStatut());
        dto.setMessage(entity.getMessage());

        dto.setDeclencheurId(entity.getDeclencheurId());
        dto.setVerificateurId(entity.getVerificateurId());
        dto.setDateVerification(entity.getDateVerification());
        dto.setValidateurId(entity.getValidateurId());
        dto.setDateValidation(entity.getDateValidation());
        dto.setRejetParId(entity.getRejetParId());
        dto.setDateRejet(entity.getDateRejet());
        dto.setMotifRejet(entity.getMotifRejet());

        dto.setDateTraitement(entity.getDateTraitement());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Long departementId) {
        this.departementId = departementId;
    }

    public String getDepartementName() {
        return departementName;
    }

    public void setDepartementName(String departementName) {
        this.departementName = departementName;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDeclencheurId() {
        return declencheurId;
    }

    public void setDeclencheurId(Long declencheurId) {
        this.declencheurId = declencheurId;
    }

    public Long getVerificateurId() {
        return verificateurId;
    }

    public void setVerificateurId(Long verificateurId) {
        this.verificateurId = verificateurId;
    }

    public LocalDateTime getDateVerification() {
        return dateVerification;
    }

    public void setDateVerification(LocalDateTime dateVerification) {
        this.dateVerification = dateVerification;
    }

    public Long getValidateurId() {
        return validateurId;
    }

    public void setValidateurId(Long validateurId) {
        this.validateurId = validateurId;
    }

    public LocalDateTime getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
    }

    public Long getRejetParId() {
        return rejetParId;
    }

    public void setRejetParId(Long rejetParId) {
        this.rejetParId = rejetParId;
    }

    public LocalDateTime getDateRejet() {
        return dateRejet;
    }

    public void setDateRejet(LocalDateTime dateRejet) {
        this.dateRejet = dateRejet;
    }

    public String getMotifRejet() {
        return motifRejet;
    }

    public void setMotifRejet(String motifRejet) {
        this.motifRejet = motifRejet;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
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
