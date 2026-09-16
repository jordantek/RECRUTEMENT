package com.tpc.tpcgestpaie.localapp.dto.alertes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlerteAbsenceDTO {
    // Absence
    private Long absenceId;
    private String libelle;
    private String typeAbsence;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String modeJouissance;
    private Integer dureeJours;

    // Employé
    private Long employeId;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    // Contrat
    private Long contratId;
    private String typeContrat;

    // Poste
    private String poste;
    private String departement;

    // Entreprise
    private Long companyId;
    private String companyName;


    public String getNomComplet() {
        return prenom + " " + nom;
    }

}