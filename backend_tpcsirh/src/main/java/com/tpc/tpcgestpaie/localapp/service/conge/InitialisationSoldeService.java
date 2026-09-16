package com.tpc.tpcgestpaie.localapp.service.conge;

import com.tpc.tpcgestpaie.localapp.dto.conge.initialisation.ResultatInitialisationDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.initialisation.SaisieSoldeInitialDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.initialisation.SaisieSoldeInitialDTO.DetailCongePrisDTO;
import com.tpc.tpcgestpaie.localapp.enums.TypeProvision;
import com.tpc.tpcgestpaie.localapp.enums.TypeRepartition;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.conge.*;
import com.tpc.tpcgestpaie.localapp.model.conge.HistoriqueSoldeConge.TypeEvenementSolde;
import com.tpc.tpcgestpaie.localapp.repository.conge.*;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitialisationSoldeService {

    private final CongeProvisionCongeRepository provisionRepository;
    private final SoldeCongeRepository soldesCongeRepository;
    private final HistoriqueSoldeCongeRepository historiqueRepository;
    private final RepartitionConsommationRepository repartitionRepository;
    private final EmployeService employeService;

    /**
     * ⭐ VÉRIFICATION PUBLIQUE : Employé déjà initialisé ?
     */
    public boolean estDejaInitialise(Long employeId) {
        return provisionRepository.countByEmployeId(employeId) > 0
                || soldesCongeRepository.existsByEmployeId(employeId);
    }

    /**
     * ⭐ INITIALISATION AVEC MESSAGES CLAIRS
     */
    @Transactional
    public ResultatInitialisationDTO initialiserSolde(SaisieSoldeInitialDTO dto) {
        log.info("🚀 Initialisation solde employe={} - Solde constaté: {} jours ({}€)",
                dto.getEmployeId(), dto.getJoursRestantTotal(), dto.getMontantRestantEstime());

        // Vérifications
        validerDonnees(dto);

        Employe employe = employeService.findById(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employé non trouvé: " + dto.getEmployeId()));

        // ⭐ VÉRIFICATION DOUBLON AVANT TOUTE ACTION
        String statutVerification = verifierStatutInitialisation(dto.getEmployeId());
        if (statutVerification != null) {
            // Déjà initialisé - retourner message clair sans erreur
            return buildReponseDejaInitialise(employe, statutVerification);
        }

        // Calcul ancienneté pour info
        int nbMois = calculerMoisAnciennete(dto.getDateDebutPremierContrat(), dto.getDateReference());

        // Création provision
        ProvisionConge provision = creerProvisionSynthetique(employe, dto);

        // Répartitions historiques optionnelles
        if (dto.getDetailCongesPris() != null && !dto.getDetailCongesPris().isEmpty()) {
            creerRepartitionsHistoriques(provision, dto);
        }

        // Solde global
        creerSoldeGlobal(employe, dto, nbMois);

        // Historique
        creerHistorique(employe, dto, provision, nbMois);

        log.info("✅ Initialisation terminée: provision {} créée avec {} jours ({}€)",
                provision.getId(), dto.getJoursRestantTotal(), dto.getMontantRestantEstime());

        return ResultatInitialisationDTO.builder()
                .employeId(dto.getEmployeId())
                .nomEmploye(employe.getNom() + " " + employe.getPrenom())
                .dateDebutContrat(dto.getDateDebutPremierContrat())
                .dateReference(dto.getDateReference())
                .nombreMoisAnciennete(nbMois)
                .joursDejaPris(dto.getJoursDejaPrisTotal())
                .joursRestants(dto.getJoursRestantTotal())
                .montantRestantEstime(dto.getMontantRestantEstime())
                .provisionId(provision.getId())
                .typeProvision(provision.getTypeProvision().name())
                .statut("SUCCESS")
                .messageSucces(String.format(
                        "✅ Solde initial enregistré: %s jours (%s€) | Ancienneté: %d mois | Pris: %s jours",
                        dto.getJoursRestantTotal(),
                        dto.getMontantRestantEstime(),
                        nbMois,
                        dto.getJoursDejaPrisTotal()))
                .build();
    }

    /**
     * ⭐ VÉRIFICATION COMPLÈTE DU STATUT
     * Retourne null si OK, sinon message décrivant le problème
     */
    private String verifierStatutInitialisation(Long employeId) {
        // Vérifier provisions
        long nbProvisions = provisionRepository.countByEmployeId(employeId);
        if (nbProvisions > 0) {
            return "PROVISION_EXISTANTE";
        }

        // Vérifier solde global
        if (soldesCongeRepository.existsByEmployeId(employeId)) {
            return "SOLDE_GLOBAL_EXISTANT";
        }

        // Vérifier historique
        if (historiqueRepository.existsByEmployeId(employeId)) {
            return "HISTORIQUE_EXISTANT";
        }

        return null; // OK, pas encore initialisé
    }

    /**
     * ⭐ RÉPONSE QUAND DÉJÀ INITIALISÉ
     */
    private ResultatInitialisationDTO buildReponseDejaInitialise(Employe employe, String raison) {
        String message;
        String statut;

        switch (raison) {
            case "PROVISION_EXISTANTE" -> {
                statut = "DEJA_INITIALISE";
                message = "⚠️ Cet employé a déjà une provision de congés. Initialisation ignorée.";
            }
            case "SOLDE_GLOBAL_EXISTANT" -> {
                statut = "DEJA_INITIALISE";
                message = "⚠️ Cet employé a déjà un solde de congés enregistré. Initialisation ignorée.";
            }
            case "HISTORIQUE_EXISTANT" -> {
                statut = "DEJA_INITIALISE";
                message = "⚠️ Cet employé a déjà un historique de congés. Initialisation ignorée.";
            }
            default -> {
                statut = "DEJA_INITIALISE";
                message = "⚠️ Cet employé est déjà initialisé dans le système.";
            }
        }

        log.warn("Employe {} déjà initialisé (raison: {})", employe.getId(), raison);

        // Récupérer les infos existantes si possible
        var soldeOpt = soldesCongeRepository.findByEmployeId(employe.getId());
        BigDecimal joursRestants = BigDecimal.ZERO;
        BigDecimal montantRestant = BigDecimal.ZERO;

        if (soldeOpt.isPresent()) {
            joursRestants = soldeOpt.get().getSoldeRestantJours();
            montantRestant = soldeOpt.get().getMontantRestant();
        }

        return ResultatInitialisationDTO.builder()
                .employeId(employe.getId())
                .nomEmploye(employe.getNom() + " " + employe.getPrenom())
                .joursRestants(joursRestants)
                .montantRestantEstime(montantRestant)
                .statut(statut)
                .messageSucces(message + " Solde actuel: " + joursRestants + " jours.")
                .build();
    }

    // ========== MÉTHODES EXISTANTES (inchangées) ==========

    private ProvisionConge creerProvisionSynthetique(Employe employe, SaisieSoldeInitialDTO dto) {
        boolean avecHistorique = dto.getDetailCongesPris() != null && !dto.getDetailCongesPris().isEmpty();

        String moisRef = YearMonth.from(dto.getDateReference()).toString();

        ProvisionConge p = ProvisionConge.builder()
                .employe(employe)
                .company(employe.getCompany())
                .moisReference(moisRef)
                .joursAcquis(dto.getJoursRestantTotal())
                .salaireBrutMois(BigDecimal.ZERO)
                .joursTravaillesMois(BigDecimal.ZERO)
                .provisionMensuelle(dto.getMontantRestantEstime())
                .joursConsommes(BigDecimal.ZERO)
                .montantConsomme(BigDecimal.ZERO)
                .typeProvision(avecHistorique ? TypeProvision.SYNTHESE_AVEC_HISTORIQUE : TypeProvision.SYNTHESE_INITIALE)
                .estVerrouille(true)
                .commentaireInitial(buildCommentaire(dto))
                .build();

        p.calculerSoldes();
        return provisionRepository.save(p);
    }

    private void creerRepartitionsHistoriques(ProvisionConge provision, SaisieSoldeInitialDTO dto) {
        BigDecimal valeurJour = BigDecimal.ZERO;
        if (dto.getJoursRestantTotal().compareTo(BigDecimal.ZERO) > 0) {
            valeurJour = dto.getMontantRestantEstime()
                    .divide(dto.getJoursRestantTotal(), 4, RoundingMode.HALF_UP);
        }

        BigDecimal totalPris = BigDecimal.ZERO;

        for (DetailCongePrisDTO cp : dto.getDetailCongesPris()) {
            BigDecimal montant = valeurJour.multiply(cp.getJoursPris())
                    .setScale(2, RoundingMode.HALF_UP);

            RepartitionConsommation rep = RepartitionConsommation.builder()
                    .provisionConge(provision)
                    .demandeAbsence(null)
                    .joursConsommes(cp.getJoursPris())
                    .montantConsomme(montant)
                    .valeurJourCalculee(valeurJour)
                    .typeRepartition(TypeRepartition.HISTORIQUE_MIGRE)
                    .commentaire(String.format("Pris %s au %s", cp.getJoursPris(), cp.getPeriodeConcernee()))
                    .build();

            repartitionRepository.save(rep);
            totalPris = totalPris.add(cp.getJoursPris());
        }

        provision.setJoursConsommes(totalPris);
        provision.calculerSoldes();
        provisionRepository.save(provision);
    }

    private void creerSoldeGlobal(Employe employe, SaisieSoldeInitialDTO dto, int nbMois) {
        SoldesConge solde = SoldesConge.builder()
                .employe(employe)
                .companyId(employe.getCompany().getId())
                .soldeTotalJours(dto.getJoursRestantTotal())
                .soldeConsommeJours(dto.getJoursDejaPrisTotal())
                .soldeRestantJours(dto.getJoursRestantTotal())
                .montantTotalProvisions(dto.getMontantRestantEstime())
                .montantConsomme(BigDecimal.ZERO)
                .montantRestant(dto.getMontantRestantEstime())
                .dateReference(dto.getDateReference())
                .soldeInitialSaisi(dto.getJoursRestantTotal())
                .dateSaisieInitial(LocalDateTime.now())
                .congesDejaPrisJours(dto.getJoursDejaPrisTotal())
                .nbMoisCalcules(nbMois)
                .build();

        soldesCongeRepository.save(solde);
    }

    private void creerHistorique(Employe employe, SaisieSoldeInitialDTO dto,
                                 ProvisionConge provision, int nbMois) {
        HistoriqueSoldeConge hist = HistoriqueSoldeConge.builder()
                .employe(employe)
                .companyId(employe.getCompany().getId())
                .dateEvenement(dto.getDateReference())
                .typeEvenement(TypeEvenementSolde.SAISIE_INITIALE)
                .description(buildCommentaire(dto))
                .joursEntree(dto.getJoursRestantTotal())
                .montantEntree(dto.getMontantRestantEstime())
                .soldeJoursApres(dto.getJoursRestantTotal())
                .soldeMontantApres(dto.getMontantRestantEstime())
                .referenceId(provision.getId())
                .referenceType("PROVISION_SYNTHESE")
                .build();

        historiqueRepository.save(hist);
    }

    private int calculerMoisAnciennete(java.time.LocalDate debut, java.time.LocalDate reference) {
        Period p = Period.between(debut, reference);
        int mois = (p.getYears() * 12) + p.getMonths();
        if (debut.getDayOfMonth() > reference.getDayOfMonth()) {
            mois--;
        }
        return Math.max(0, mois);
    }

    private String buildCommentaire(SaisieSoldeInitialDTO dto) {
        return String.format(
                "INIT | Contrat: %s | Réf: %s | Pris: %s | RESTANT: %s jours (%s€) | %s",
                dto.getDateDebutPremierContrat(),
                dto.getDateReference(),
                dto.getJoursDejaPrisTotal(),
                dto.getJoursRestantTotal(),
                dto.getMontantRestantEstime(),
                dto.getCommentaire() != null ? dto.getCommentaire() : "Saisie manuelle solde constaté"
        );
    }

    private void validerDonnees(SaisieSoldeInitialDTO dto) {
        if (dto.getDateDebutPremierContrat().isAfter(dto.getDateReference())) {
            throw new IllegalArgumentException("Date début contrat doit être avant date référence");
        }
    }
}