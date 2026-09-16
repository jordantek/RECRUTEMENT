package com.tpc.tpcgestpaie.localapp.dto.administration;

import com.tpc.tpcgestpaie.localapp.model.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AbsenceDTO {

    private Long id;
    private Long employeId;
    private String employeNom;
    private String employePrenom;
    private Long contratEmployeId;
    private Long companyId;
    private Long motifAbsenceId;
    private Long typeAbsenceId;
    private String typeAbsence;
    private Long creditCongeId;

    private String libelle;
    private String modeJouissance;
    private String conditionAcceptation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String duree;

    private Long addedById;
    private LocalDateTime createdAt;

    public static AbsenceDTO fromEntity(Absence entity) {
        AbsenceDTO dto = new AbsenceDTO();
        dto.setId(entity.getId());
        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);

        // Ajout du nom et prénom de l'employé
        if (entity.getEmploye() != null) {
            dto.setEmployeNom(entity.getEmploye().getNom());
            dto.setEmployePrenom(entity.getEmploye().getPrenom());
        }

        if (entity.getTypeAbsence() != null) {
            dto.setTypeAbsence(entity.getTypeAbsence().getLibelle());
        }

        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        dto.setTypeAbsenceId(entity.getTypeAbsence() != null ? entity.getTypeAbsence().getId() : null);
        dto.setMotifAbsenceId(entity.getMotifAbsence() != null ? entity.getMotifAbsence().getId() : null);
        dto.setCreditCongeId(entity.getCreditConge() != null ? entity.getCreditConge().getId() : null);

        dto.setLibelle(entity.getLibelle());
        dto.setConditionAcceptation(entity.getConditionAcceptation());
        dto.setModeJouissance(entity.getModeJouissance());
        dto.setDateDebut(entity.getDateDebut());
        dto.setDateFin(entity.getDateFin());
        dto.setDuree(entity.getDuree());
        dto.setCreatedAt(entity.getCreated_at());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        return dto;
    }

    public Absence toEntity(Employe employe,
                            ContratEmploye contratEmploye,
                            Company company,
                            TypeAbsence typeAbsence,
                            MotifAbsence motifAbsence,
                            CreditConge creditConge,
                            User addedBy) {
        Absence entity = new Absence();
        entity.setId(this.id);
        entity.setEmploye(employe);
        entity.setContratEmploye(contratEmploye);
        entity.setCompany(company);
        entity.setTypeAbsence(typeAbsence);
        entity.setMotifAbsence(motifAbsence);
        entity.setCreditConge(creditConge);
        entity.setLibelle(this.libelle);
        entity.setModeJouissance(this.modeJouissance);
        entity.setConditionAcceptation(this.conditionAcceptation);
        entity.setDateDebut(this.dateDebut);
        entity.setDateFin(this.dateFin);
        entity.setDuree(this.duree);
        entity.setAdded_by(addedBy);
        entity.setCreated_at(this.createdAt);
        return entity;
    }

    // Méthode utilitaire pour obtenir le nom complet de l'employé
    public String getEmployeNomComplet() {
        if (employeNom != null && employePrenom != null) {
            return employeNom + " " + employePrenom;
        }
        return null;
    }

    // Getters et Setters (générés par @Data de Lombok)


    public String getTypeAbsence() {
        return typeAbsence;
    }

    public void setTypeAbsence(String typeAbsence) {
        this.typeAbsence = typeAbsence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public String getEmployeNom() {
        return employeNom;
    }

    public void setEmployeNom(String employeNom) {
        this.employeNom = employeNom;
    }

    public String getEmployePrenom() {
        return employePrenom;
    }

    public void setEmployePrenom(String employePrenom) {
        this.employePrenom = employePrenom;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getMotifAbsenceId() {
        return motifAbsenceId;
    }

    public void setMotifAbsenceId(Long motifAbsenceId) {
        this.motifAbsenceId = motifAbsenceId;
    }

    public Long getTypeAbsenceId() {
        return typeAbsenceId;
    }

    public void setTypeAbsenceId(Long typeAbsenceId) {
        this.typeAbsenceId = typeAbsenceId;
    }

    public Long getCreditCongeId() {
        return creditCongeId;
    }

    public void setCreditCongeId(Long creditCongeId) {
        this.creditCongeId = creditCongeId;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getModeJouissance() {
        return modeJouissance;
    }

    public void setModeJouissance(String modeJouissance) {
        this.modeJouissance = modeJouissance;
    }

    public String getConditionAcceptation() {
        return conditionAcceptation;
    }

    public void setConditionAcceptation(String conditionAcceptation) {
        this.conditionAcceptation = conditionAcceptation;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}