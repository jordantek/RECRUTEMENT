package com.tpc.tpcgestpaie.localapp.dto.conge.annulation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Requête d'annulation d'une consommation de congé")
public class AnnulationRequestDTO {

    @Schema(description = "ID de la demande à annuler", example = "1001", required = true)
    @NotNull(message = "L'ID de la demande est obligatoire")
    private Long demandeId;

    @Schema(description = "Motif de l'annulation", example = "Erreur de saisie des dates",
            required = true, maxLength = 500)
    @NotBlank(message = "Le motif est obligatoire")
    @Size(max = 500, message = "Le motif ne doit pas dépasser 500 caractères")
    private String motif;

    @Schema(description = "ID de l'utilisateur demandant l'annulation", example = "42", required = true)
    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long utilisateurId;

    @Schema(description = "Forcer l'annulation même si le congé a déjà commencé", example = "false")
    private Boolean forcerAnnulation = false;

    @Schema(description = "Motif de forçage (si forcerAnnulation=true)",
            example = "Demande du DRH suite erreur système")
    private String motifForcage;
}