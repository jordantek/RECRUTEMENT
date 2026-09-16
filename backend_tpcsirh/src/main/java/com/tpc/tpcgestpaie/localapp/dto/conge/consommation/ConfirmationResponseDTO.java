package com.tpc.tpcgestpaie.localapp.dto.conge.consommation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Réponse complète de confirmation de départ en congé")
public class ConfirmationResponseDTO {

    // ====== Infos demande ======
    @Schema(description = "ID de la demande", example = "1001")
    private Long demandeId;

    @Schema(description = "Nouveau statut de la demande", example = "EN_COURS")
    private String nouveauStatut;

    @Schema(description = "Date effective de départ", example = "2025-03-10")
    private LocalDate dateDebutEffective;

    @Schema(description = "Heure effective de départ", example = "08:30:00")
    private String heureDepartEffective;

    // ====== Infos provisions ======
    @Schema(description = "Jours totaux déduits", example = "4.0")
    private BigDecimal joursDeduits;

    @Schema(description = "Montant total alloué (€)", example = "410.00")
    private BigDecimal montantAllocation;

    @Schema(description = "Détail par mois de provision")
    private List<DetailConsommationMoisDTO> repartition;

    // ====== Solde après opération ======
    @Schema(description = "Solde restant en jours", example = "8.5")
    private BigDecimal soldeJoursRestant;

    @Schema(description = "Solde restant estimé en €", example = "906.67")
    private BigDecimal soldeMontantEstime;

    // ====== Messages ======
    @Schema(description = "Message de confirmation", example = "Départ confirmé. Votre congé a été déduit de vos provisions.")
    private String messageConfirmation;

    @Schema(description = "Message détaillé pour RH", example = "4 jours déduits: 2.5j sur Janvier 2025 (250€) + 1.5j sur Février 2025 (160€)")
    private String messageDetailleRH;

    @Schema(description = "Timestamp de l'opération")
    private LocalDateTime timestamp;
}