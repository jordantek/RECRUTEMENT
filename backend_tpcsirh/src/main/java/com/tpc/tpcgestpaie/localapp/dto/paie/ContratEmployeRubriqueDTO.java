package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.model.ContratEmployeRubrique.Statut;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContratEmployeRubriqueDTO {

    private Long id;
    private String libelle;
    private Long contratEmployeId;
    private Long rubriqueId;
    private BigDecimal montant;
    private BigDecimal montant_ajouter;
    private BigDecimal montant_actuel;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Statut statut;
    private Long addedById;
    private Long companyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters & Setters

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public Long getRubriqueId() {
        return rubriqueId;
    }

    public void setRubriqueId(Long rubriqueId) {
        this.rubriqueId = rubriqueId;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
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
    public BigDecimal getMontant_ajouter() {
        return montant_ajouter;
    }

    public void setMontant_ajouter(BigDecimal montant_ajouter) {
        this.montant_ajouter = montant_ajouter;
    }
    // Conversion methods

    public static ContratEmployeRubriqueDTO fromEntity(ContratEmployeRubrique entity) {
        ContratEmployeRubriqueDTO dto = new ContratEmployeRubriqueDTO();
        dto.setId(entity.getId());
        dto.setLibelle(entity.getLibelle());
        dto.setCompanyId(entity.getCompany()!=null? entity.getCompany().getId():null);
        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setRubriqueId(entity.getRubrique() != null ? entity.getRubrique().getId() : null);
        dto.setMontant(entity.getMontant());
        dto.setMontant_ajouter(entity.getMontant_ajout());
        dto.setMontant_actuel(entity.getMontant().add(entity.getMontant_ajout()));
        dto.setDateDebut(entity.getDateDebut());
        dto.setDateFin(entity.getDateFin());
        dto.setStatut(entity.getStatut());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }


    public ContratEmployeRubrique toEntity(
            ContratEmploye contratEmploye,
            Rubrique rubrique,
            User addedBy,
            Company company
    ) {
        ContratEmployeRubrique entity = new ContratEmployeRubrique();
        entity.setId(this.id);
        entity.setLibelle(this.libelle);
        entity.setContratEmploye(contratEmploye);
        entity.setCompany(company);
        entity.setRubrique(rubrique);
        entity.setMontant(this.montant);
        entity.setMontant_ajout(this.montant_ajouter);
        entity.setDateDebut(this.dateDebut);
        entity.setDateFin(this.dateFin);
        entity.setStatut(this.statut != null ? this.statut : Statut.ACTIF); // Valeur par défaut si null
        entity.setAdded_by(addedBy);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        return entity;
    }


    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getMontant_actuel() {
        return montant_actuel;
    }

    public void setMontant_actuel(BigDecimal montant_actuel) {
        this.montant_actuel = montant_actuel;
    }
}
