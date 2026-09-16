package com.tpc.tpcgestpaie.localapp.dto.contrat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.dto.ContratEmployeRubriqueResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContratEmployeResponseDTO(
        Long id,

        // Employé
        Long employeId,
        String employeNom,
        String employePrenom,

        // Société
        Long companyId,
        String companyName,

        // Organisation
        Long departementId,
        String departementNom,
        Long posteId,
        String posteNom,
        Long categorieEmployeId,
        String categorieEmployeNom,

        // Infos contrat
        String numeroContrat,
        String typeContrat,
        String dureeContrat,
        String mouvementContrat,
        String statusContrat,

        // Nature / paiement
        Long natureContratId,
        String natureContratNom,
        Long modePaiementId,
        String modePaiementNom,
        Long banqueId,
        String banqueNom,
        String numeroCompte,

        // Dates
        LocalDate dateDebut,
        LocalDate dateFin,
        String duree,
        LocalDate dateEmbauche,

        // Période d’essai
        LocalDate debutEssai,
        LocalDate finEssai,
        String dureeEssai,

        // Missions
        String missions,
        String diplomeRequis,
        String lieuExecution,

        // ⏰ Horaire de travail
        String horaireTravail,

        // 🏢 Nature juridique de l'employeur
        String natureJuridiqueEmployeur,

        // 📝 Situations pour avenants
        String ancienneSituation,
        String nouvelleSituation,

        // 💬 Commentaire libre
        String commentaire,

        // Fin contrat
        Boolean arretContrat,
        LocalDate dateArretContrat,
        String motifArretContrat,

        // Financier
        Double salaireBrut,
        Double salaireBase,
        Double aib,
        Double caution,
        Double transfert,

        // Calcul
        Integer ancienneteEnMois,

        // Audit
        Long addedById,
        String addedByNom,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        //
        List<ContratEmployeRubriqueResponseDTO> rubriques

) {}