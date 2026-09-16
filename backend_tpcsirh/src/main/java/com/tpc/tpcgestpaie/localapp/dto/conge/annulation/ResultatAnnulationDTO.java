package com.tpc.tpcgestpaie.localapp.dto.conge.annulation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Résultat d'une annulation de consommation de provisions")
public class ResultatAnnulationDTO {

    @Schema(description = "ID de la demande annulée", example = "1001")
    private Long demandeId;

    @Schema(description = "Jours totaux restaurés", example = "4.0")
    private BigDecimal joursTotalRestaures;

    @Schema(description = "Montant total restauré (€)", example = "410.00")
    private BigDecimal montantTotalRestaure;

    @Schema(description = "Nombre de mois restaurés", example = "2")
    private Integer nombreMoisTouches;

    @Schema(description = "Détail de la restauration par mois")
    private List<DetailRestaurationMoisDTO> detailsParMois;

    @Schema(description = "Motif de l'annulation", example = "Erreur de saisie des dates")
    private String motifAnnulation;

    @Schema(description = "Date et heure de l'annulation")
    private LocalDateTime dateAnnulation;

    @Schema(description = "ID de l'utilisateur ayant effectué l'annulation", example = "42")
    private Long annulePar;

    @Schema(description = "Nom de l'utilisateur ayant effectué l'annulation", example = "DUPONT Marie")
    private String annuleParNom;

    @Schema(description = "Nouveau statut de la demande", example = "ANNULEE")
    private String nouveauStatutDemande;

    @Schema(description = "Solde après restauration (jours)", example = "12.5")
    private BigDecimal soldeApresJours;

    @Schema(description = "Solde après restauration (€)", example = "1316.67")
    private BigDecimal soldeApresMontant;

    @Schema(description = "Message récapitulatif", example = "Annulation effectuée. 4 jours restaurés sur 2 mois.")
    private String message;

    @Schema(description = "Succès de l'opération", example = "true")
    private Boolean succes;

    @Schema(description = "Timestamp", example = "2025-03-11T14:22:10")
    private String timestamp;
}