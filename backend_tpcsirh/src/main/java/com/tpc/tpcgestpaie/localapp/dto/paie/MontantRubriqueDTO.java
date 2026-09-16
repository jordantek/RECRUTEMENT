package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MontantRubriqueDTO {

    private Long id;

    private LocalDate dateRubrique;

    private String moisRubrique;

    private Long contratEmployeId;

    private Long employeId;

    private EmployeDTO employe;

    // Informations simplifiées de l'employé
    private String employeMatricule;
    private String employeNom;
    private String employePrenom;
    private String employeNomComplet;

    private Long companyId;

    private Long rubriqueId;

    private String rubriqueName;

    private BigDecimal montantRubrique;

    private LocalDate debutPeriodeReference;

    private LocalDate finPeriodeReference;

    private BigDecimal montantBrutJournalier;

    private double nombreJourPaye;

    private Long addedById;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    // Getters et setters (omission pour la concision)

    // Conversion entity -> DTO
    public static MontantRubriqueDTO fromEntity(MontantRubrique entity) {
        if (entity == null) return null;

        MontantRubriqueDTO dto = new MontantRubriqueDTO();
        dto.setId(entity.getId());
        dto.setDateRubrique(entity.getDateRubrique());
        dto.setMoisRubrique(entity.getMoisRubrique());
        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);

        dto.setEmployeMatricule(entity.getEmploye() != null ? entity.getEmploye().getMatricule() : null);
        dto.setEmployeNom(entity.getEmploye() != null ? entity.getEmploye().getNom() : null);
        dto.setEmployePrenom(entity.getEmploye() != null ? entity.getEmploye().getPrenom() : null);
        dto.setEmployeNomComplet(entity.getEmploye() != null ? entity.getEmploye().getNom() + " " + entity.getEmploye().getPrenom()  : null);


        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        dto.setRubriqueId(entity.getRubrique() != null ? entity.getRubrique().getId() : null);
        dto.setRubriqueName(entity.getRubrique().getLibelle());
        dto.setMontantRubrique(entity.getMontantRubrique());
        dto.setDebutPeriodeReference(entity.getDebutPeriodeReference());
        dto.setFinPeriodeReference(entity.getFinPeriodeReference());
        dto.setMontantBrutJournalier(entity.getMontantBrutJournalier());
        dto.setNombreJourPaye(entity.getNombreJourPaye());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        dto.setCreatedAt(entity.getCreated_at());
        dto.setUpdatedAt(entity.getUpdated_at());
        dto.setDeletedAt(entity.getDeleted_at());

        return dto;
    }

    // Conversion DTO -> entity
    public MontantRubrique toEntity(ContratEmploye contratEmploye,
                                    Employe employe,
                                    Company company,
                                    Rubrique rubrique,
                                    User addedBy) {
        MontantRubrique entity = new MontantRubrique();
        entity.setId(this.getId());
        entity.setDateRubrique(this.getDateRubrique());
        entity.setMoisRubrique(this.getMoisRubrique());
        entity.setContratEmploye(contratEmploye);
        entity.setEmploye(employe);
        entity.setCompany(company);
        entity.setRubrique(rubrique);
        entity.setMontantRubrique(this.getMontantRubrique());
        entity.setDebutPeriodeReference(this.getDebutPeriodeReference());
        entity.setFinPeriodeReference(this.getFinPeriodeReference());
        entity.setMontantBrutJournalier(this.getMontantBrutJournalier());
        entity.setNombreJourPaye(this.getNombreJourPaye());
        entity.setAdded_by(addedBy);
        entity.setCreated_at(this.getCreatedAt());
        entity.setUpdated_at(this.getUpdatedAt());
        entity.setDeleted_at(this.getDeletedAt());

        return entity;
    }

    // Exemple d’un getter et setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRubriqueName() {
        return rubriqueName;
    }

    public void setRubriqueName(String rubriqueName) {
        this.rubriqueName = rubriqueName;
    }

    public EmployeDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeDTO employe) {
        this.employe = employe;
    }

    public LocalDate getDateRubrique() {
        return dateRubrique;
    }

    public void setDateRubrique(LocalDate dateRubrique) {
        this.dateRubrique = dateRubrique;
    }

    public String getMoisRubrique() {
        return moisRubrique;
    }

    public void setMoisRubrique(String moisRubrique) {
        this.moisRubrique = moisRubrique;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getRubriqueId() {
        return rubriqueId;
    }

    public void setRubriqueId(Long rubriqueId) {
        this.rubriqueId = rubriqueId;
    }

    public BigDecimal getMontantRubrique() {
        return montantRubrique;
    }

    public void setMontantRubrique(BigDecimal montantRubrique) {
        this.montantRubrique = montantRubrique;
    }

    public LocalDate getDebutPeriodeReference() {
        return debutPeriodeReference;
    }

    public void setDebutPeriodeReference(LocalDate debutPeriodeReference) {
        this.debutPeriodeReference = debutPeriodeReference;
    }

    public LocalDate getFinPeriodeReference() {
        return finPeriodeReference;
    }

    public void setFinPeriodeReference(LocalDate finPeriodeReference) {
        this.finPeriodeReference = finPeriodeReference;
    }

    public BigDecimal getMontantBrutJournalier() {
        return montantBrutJournalier;
    }

    public void setMontantBrutJournalier(BigDecimal montantBrutJournalier) {
        this.montantBrutJournalier = montantBrutJournalier;
    }

    public double getNombreJourPaye() {
        return nombreJourPaye;
    }

    public void setNombreJourPaye(double nombreJourPaye) {
        this.nombreJourPaye = nombreJourPaye;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getEmployeMatricule() {
        return employeMatricule;
    }

    public void setEmployeMatricule(String employeMatricule) {
        this.employeMatricule = employeMatricule;
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

    public String getEmployeNomComplet() {
        return employeNomComplet;
    }

    public void setEmployeNomComplet(String employeNomComplet) {
        this.employeNomComplet = employeNomComplet;
    }
}
