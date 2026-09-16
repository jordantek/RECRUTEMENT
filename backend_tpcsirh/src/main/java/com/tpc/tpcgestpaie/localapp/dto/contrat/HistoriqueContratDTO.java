package com.tpc.tpcgestpaie.localapp.dto.contrat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HistoriqueContratDTO {
    private Long id;
    private Long contratEmployeId;
    private String typeModification;
    private String motif;
    private String preuve;
    private LocalDate dateEffet;
    private LocalDateTime createdAt;
    private String snapshotJson;

    // Informations supplémentaires pour l'affichage
    private String typeContrat;
    private Double salaireBase;
    private Double salaireBrut;
    private LocalDate dateFinContrat;
    private String nomDepartement;
    private String nomPoste;
    private String nomCategorie;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getContratEmployeId() { return contratEmployeId; }
    public void setContratEmployeId(Long contratEmployeId) { this.contratEmployeId = contratEmployeId; }

    public String getTypeModification() { return typeModification; }
    public void setTypeModification(String typeModification) { this.typeModification = typeModification; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getPreuve() { return preuve; }
    public void setPreuve(String preuve) { this.preuve = preuve; }

    public LocalDate getDateEffet() { return dateEffet; }
    public void setDateEffet(LocalDate dateEffet) { this.dateEffet = dateEffet; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }

    public String getTypeContrat() { return typeContrat; }
    public void setTypeContrat(String typeContrat) { this.typeContrat = typeContrat; }

    public Double getSalaireBase() { return salaireBase; }
    public void setSalaireBase(Double salaireBase) { this.salaireBase = salaireBase; }

    public Double getSalaireBrut() { return salaireBrut; }
    public void setSalaireBrut(Double salaireBrut) { this.salaireBrut = salaireBrut; }

    public LocalDate getDateFinContrat() { return dateFinContrat; }
    public void setDateFinContrat(LocalDate dateFinContrat) { this.dateFinContrat = dateFinContrat; }

    public String getNomDepartement() { return nomDepartement; }
    public void setNomDepartement(String nomDepartement) { this.nomDepartement = nomDepartement; }

    public String getNomPoste() { return nomPoste; }
    public void setNomPoste(String nomPoste) { this.nomPoste = nomPoste; }

    public String getNomCategorie() { return nomCategorie; }
    public void setNomCategorie(String nomCategorie) { this.nomCategorie = nomCategorie; }
}