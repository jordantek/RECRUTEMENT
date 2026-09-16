package com.tpc.tpcgestpaie.localapp.dto.conge.initialisation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneImportSoldeDTO {

    private String matricule;              // ⭐ Clé d'identification

    private LocalDate dateDebutContrat;
    private LocalDate dateReference;
    private BigDecimal joursDejaPris;
    private BigDecimal joursRestant;
    private BigDecimal montantRestant;
    private String commentaire;

    // Champs de résultat (remplis après traitement)
    private Long employeId;                // Trouvé via matricule
    private String nomEmploye;             // Pour rapport
    private String statut;                 // SUCCESS, ERROR, WARNING
    private String messageErreur;          // Si erreur
}