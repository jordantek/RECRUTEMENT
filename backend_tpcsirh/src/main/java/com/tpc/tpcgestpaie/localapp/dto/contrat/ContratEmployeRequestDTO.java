package com.tpc.tpcgestpaie.localapp.dto.contrat;

import com.tpc.tpcgestpaie.localapp.dto.ContratEmployeRubriqueUpdateDTO;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record ContratEmployeRequestDTO(

        // ID (obligatoire pour update)
        @NotNull(message = "L'identifiant du contrat est obligatoire")
        Long id,

        // Employé / organisation
        @NotNull(message = "La catégorie est requise")
        Long categorieEmployeId,

        // ❗ optionnel
        Long departementId,

        @NotNull(message = "Le poste est requis")
        Long posteId,

        // Enum type contrat
        @NotNull(message = "Le type de contrat est requis")
        String typeContrat,

        @NotNull(message = "La nature du contrat est requise")
        Long natureContratId,

        @NotNull(message = "Le mode de paiement est requis")
        Long modeDePaiementId,

        @NotNull(message = "La banque est requise")
        Long banqueId,

        @NotBlank(message = "Le numéro de compte est requis")
        String numeroCompte,

        // Dates
        @NotNull(message = "La date de début est requise")
        LocalDate dateDebut,

        LocalDate dateFin,

        LocalDate debutEssai,
        LocalDate finEssai,
        LocalDate dateEmbauche,

        // Financier
        Double aibContratEmploye,
        Double cautionContratEmploye,
        Double transfertContratEmploye,

        // ⏰ Horaire de travail
        String horaireTravail,

        // 🏢 Nature juridique de l'employeur
        String natureJuridiqueEmployeur,

        // 📝 Situations pour avenants
        String ancienneSituation,
        String nouvelleSituation,

        // 💬 Commentaire libre
        String commentaire,

        // Relations
        List<ContratEmployeRubriqueUpdateDTO> rubriques
) {

    // ✅ VALIDATION CONDITIONNELLE (comme ton Zod .superRefine)
    @AssertTrue(message = "La date de fin est requise pour un CDD")
    public boolean isDateFinValid() {
        if (typeContrat == "CDD") {
            return dateFin != null;
        }
        return true;
    }
}