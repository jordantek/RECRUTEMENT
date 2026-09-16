package com.tpc.tpcgestpaie.localapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour afficher un employé subordonné
 * Utilise le mapping direct via constructeur JPA
 */
@Data
@NoArgsConstructor
public class SubordonneResponseDTO {

    // ========================================
    // INFORMATIONS EMPLOYÉ
    // ========================================
    private Long employeId;
    private String matricule;
    private String nomComplet;
    private String titre;
    private String nom;
    private String prenom;
    private String sexe;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;
    private Integer age;

    // ========================================
    // INFORMATIONS CONTRAT
    // ========================================
    private Long contratEmployeId;
    private String typeContrat;
    private String statusContrat;
    private LocalDate dateEmbauche;

    // ========================================
    // POSTE ET DÉPARTEMENT
    // ========================================
    private Long posteId;
    private String posteLibelle;
    private Long departementId;
    private String departementLibelle;
    private String categorieEmploye;

    // ========================================
    // ENTREPRISE
    // ========================================
    private Long companyId;
    private String companyName;

    // ========================================
    // HIÉRARCHIE
    // ========================================
    private Integer niveauHierarchique; // Niveau dans la hiérarchie (1 = N+1, 2 = N+2, etc.)

    // ========================================
    // MÉTADONNÉES
    // ========================================
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ========================================
    // CONSTRUCTEUR POUR MAPPING JPA DIRECT
    // ========================================

    /**
     * Constructeur complet utilisé par JPA pour le mapping direct
     *
     * Utilisation dans le repository:
     * @Query("SELECT new ...SubordonneResponseDTO(e.id, e.matricule, ...) FROM ...")
     */
    public SubordonneResponseDTO(
            Long employeId,
            String matricule,
            String nom,
            String prenom,
            String titre,
            String sexe,
            String telephone,
            String email,
            LocalDate dateNaissance,
            Long contratEmployeId,
            String typeContrat,
            String statusContrat,
            LocalDate dateEmbauche,
            Long posteId,
            String posteLibelle,
            Long departementId,
            String departementLibelle,
            String categorieEmploye,
            Long companyId,
            String companyName,
            Boolean isActive,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.employeId = employeId;
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.titre = titre;
        this.sexe = sexe;
        this.telephone = telephone;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.contratEmployeId = contratEmployeId;
        this.typeContrat = typeContrat;
        this.statusContrat = statusContrat;
        this.dateEmbauche = dateEmbauche;

        this.posteId = posteId;
        this.posteLibelle = posteLibelle;
        this.departementId = departementId;
        this.departementLibelle = departementLibelle;
        this.categorieEmploye = categorieEmploye;
        this.companyId = companyId;
        this.companyName = companyName;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        // Calculs automatiques
        calculerChampsDerivés();
    }

    // ========================================
    // MÉTHODES UTILITAIRES
    // ========================================

    /**
     * Calcule les champs dérivés
     */
    private void calculerChampsDerivés() {
        // Nom complet
        this.nomComplet = String.format("%s %s",
                this.prenom != null ? this.prenom : "",
                this.nom != null ? this.nom : ""
        ).trim();

        // Âge
        if (this.dateNaissance != null) {
            this.age = java.time.Period.between(this.dateNaissance, LocalDate.now()).getYears();
        }


    }


    /**
     * Retourne le statut d'activité en format lisible
     */
    public String getStatutActivite() {
        return Boolean.TRUE.equals(isActive) ? "Actif" : "Inactif";
    }
}