package com.tpc.tpcgestpaie.localapp.dto.absence;

import com.tpc.tpcgestpaie.localapp.model.absence.SuiviAbsence;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SuiviAbsenceResponseDTO {

    private Long id;
    private Long demandeAbsenceId;

    // Départ
    private LocalDate dateDepartPrevue;
    private LocalDate dateDepartEffective;
    private Boolean departConfirme;
    private LocalDateTime dateConfirmationDepart;
    private String commentaireDepart;

    // Retour
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private Boolean retourConfirme;
    private LocalDateTime dateConfirmationRetour;
    private String commentaireRetour;

    // Calculs
    private Integer joursPrevu;
    private Integer joursEffectifs;
    private Integer ecartJours;

    // Report
    private Boolean aEteReporte;
    private String motifReport;

    public SuiviAbsenceResponseDTO(SuiviAbsence suivi) {
        this.id = suivi.getId();
        this.demandeAbsenceId = suivi.getDemandeAbsence().getId();

        this.dateDepartPrevue = suivi.getDateDepartPrevue();
        this.dateDepartEffective = suivi.getDateDepartEffective();
        this.departConfirme = suivi.getDepartConfirme();
        this.dateConfirmationDepart = suivi.getDateConfirmationDepart();
        this.commentaireDepart = suivi.getCommentaireDepart();

        this.dateRetourPrevue = suivi.getDateRetourPrevue();
        this.dateRetourEffective = suivi.getDateRetourEffective();
        this.retourConfirme = suivi.getRetourConfirme();
        this.dateConfirmationRetour = suivi.getDateConfirmationRetour();
        this.commentaireRetour = suivi.getCommentaireRetour();

        this.joursPrevu = suivi.getJoursPrevu();
        this.joursEffectifs = suivi.getJoursEffectifs();
        this.ecartJours = suivi.getEcartJours();

        this.aEteReporte = suivi.getAEteReporte();
        this.motifReport = suivi.getMotifReport();
    }
}