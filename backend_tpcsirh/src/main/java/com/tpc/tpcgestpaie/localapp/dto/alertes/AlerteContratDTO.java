package com.tpc.tpcgestpaie.localapp.dto.alertes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlerteContratDTO {
    // Contrat
    private Long contratId;
    private String typeContrat;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate finEssai;

    // Employé
    private Long employeId;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    // Poste
    private String poste;
    private String departement;

    // Entreprise
    private Long companyId;
    private String companyName;

    /*// Calculés
    private Integer joursAvantFin;
    private Boolean estEnEssai;
    private Boolean estExpire;*/

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean isProcheFin() {
        if (dateFin == null) return false;
        LocalDate now = LocalDate.now();
        return dateFin.isAfter(now) && dateFin.isBefore(now.plusDays(30));
    }

    public boolean isEssaiProche() {
        if (finEssai == null) return false;
        LocalDate now = LocalDate.now();
        return finEssai.isAfter(now) && finEssai.isBefore(now.plusDays(15));
    }
}