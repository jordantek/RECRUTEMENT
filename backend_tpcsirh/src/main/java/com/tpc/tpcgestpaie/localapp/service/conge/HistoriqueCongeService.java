package com.tpc.tpcgestpaie.localapp.service.conge;

import com.tpc.tpcgestpaie.localapp.dto.conge.historique.*;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.conge.ProvisionConge;
import com.tpc.tpcgestpaie.localapp.model.conge.RepartitionConsommation;
import com.tpc.tpcgestpaie.localapp.repository.absence.DemandeAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.conge.CongeProvisionCongeRepository;
import com.tpc.tpcgestpaie.localapp.repository.conge.RepartitionConsommationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoriqueCongeService {

    private final DemandeAbsenceRepository demandeRepository;
    private final RepartitionConsommationRepository repartitionRepository;
    private final CongeProvisionCongeRepository provisionRepository;

    /**
     * Détail complet d'une demande avec répartition sur les mois
     */
    @Transactional // 🆕 Important pour le lazy loading
    public HistoriqueDemandeDTO getHistoriqueDemande(Long demandeId) {



        DemandeAbsence demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée: " + demandeId));

        // 🆕 Vérifier que la demande a bien des répartitions
        if (demande.getRepartitions() == null || demande.getRepartitions().isEmpty()) {

        }

        // Récupérer les répartitions avec JOIN FETCH pour avoir toutes les données
        List<RepartitionConsommation> toutesRepartitions = repartitionRepository
                .findAllByDemandeOrderByMois(demandeId);

        List<RepartitionDetailDTO> repartitionsActives = toutesRepartitions.stream()
                .filter(r -> !Boolean.TRUE.equals(r.getEstAnnule()))
                .map(this::mapperRepartition)
                .collect(Collectors.toList());

        List<RepartitionAnnuleeDTO> repartitionsAnnulees = toutesRepartitions.stream()
                .filter(r -> Boolean.TRUE.equals(r.getEstAnnule()))
                .map(this::mapperRepartitionAnnulee)
                .collect(Collectors.toList());

        // 🆕 Construction avec null-safety
        Employe employe = demande.getEmployeDemandeur();
        String nomComplet = (employe.getNom() != null ? employe.getNom() : "") +
                " " +
                (employe.getPrenom() != null ? employe.getPrenom() : "");

        return HistoriqueDemandeDTO.builder()
                .demandeId(demande.getId())
                .employeId(employe.getId())
                .nomEmploye(nomComplet.trim())
                .matricule(employe.getMatricule())
                .dateDebut(demande.getDateDebut())
                .dateFin(demande.getDateFin())
                .dateDebutEffective(demande.getDateDebutEffective())
                .dateFinEffective(demande.getDateFinEffective())
                .nombreJoursDemandes(demande.getNombreJours())
                .joursEffectifsDeduits(demande.getJoursEffectifsDeduits())
                .statut(demande.getStatut().name())
                .soldeDeduit(demande.getSoldeDeduit())
                .dateDeduction(demande.getDateDeductionSolde())
                .dateConfirmationDepart(demande.getDateConfirmationDepartEffective())
                .montantTotalAllocation(demande.getMontantAllocation())
                .repartitionsActives(repartitionsActives)
                .historiqueAnnulations(repartitionsAnnulees)
                .build();
    }
    /**
     * Historique d'un mois de provision spécifique (audit)
     */
    public HistoriqueMoisProvisionDTO getHistoriqueMoisProvision(Long provisionId) {

        ProvisionConge provision = provisionRepository.findById(provisionId)
                .orElseThrow(() -> new RuntimeException("Provision non trouvée"));

        List<RepartitionConsommation> consommations = repartitionRepository
                .findByProvisionCongeIdAndEstAnnuleFalse(provisionId);

        List<ConsommationMoisDTO> details = consommations.stream()
                .map(c -> ConsommationMoisDTO.builder()
                        .demandeId(c.getDemandeAbsence().getId())
                        .employeNom(c.getDemandeAbsence().getEmployeDemandeur().getNom())
                        .dateDebutConge(c.getDemandeAbsence().getDateDebut())
                        .joursPris(c.getJoursConsommes())
                        .montantPris(c.getMontantConsomme())
                        .dateConsommation(c.getDateConsommation())
                        .build())
                .collect(Collectors.toList());

        return HistoriqueMoisProvisionDTO.builder()
                .provisionId(provision.getId())
                .moisReference(provision.getMoisReference())
                .moisLibelle(formaterMois(provision.getMoisReference()))
                .joursAcquis(provision.getJoursAcquis())
                .provisionInitiale(provision.getProvisionMensuelle())
                .joursConsommes(provision.getJoursConsommes())
                .montantConsomme(provision.getMontantConsomme())
                .joursRestants(provision.getJoursRestant())
                .soldeFinancier(provision.getSoldeFinancier())
                .statut(provision.getStatut().name())
                .consommations(details)
                .build();
    }

    private RepartitionDetailDTO mapperRepartition(RepartitionConsommation rep) {

        // 🆕 Récupérer la provision avec toutes ses infos
        ProvisionConge provision = rep.getProvisionConge();

        return RepartitionDetailDTO.builder()
                .repartitionId(rep.getId())  // 🆕 ID de la répartition
                .provisionId(provision.getId())  // 🆕 ID de la provision
                .moisConcerne(formaterMois(provision.getMoisReference()))
                .moisReference(provision.getMoisReference())
                .joursConsommes(rep.getJoursConsommes())
                .montantConsomme(rep.getMontantConsomme())
                .tauxJournalier(rep.getValeurJourCalculee())
                .dateConsommation(rep.getDateConsommation())
                .soldeRestantMois(provision.getJoursRestant())  // 🆕 Solde actuel du mois
                .statutMoisApresConso(provision.getStatut().name())  // 🆕 Statut actuel
                .build();
    }

    private RepartitionAnnuleeDTO mapperRepartitionAnnulee(RepartitionConsommation rep) {

        ProvisionConge provision = rep.getProvisionConge();

        return RepartitionAnnuleeDTO.builder()
                .repartitionId(rep.getId())  // 🆕
                .moisConcerne(formaterMois(provision.getMoisReference()))
                .moisReference(provision.getMoisReference())
                .joursAnnules(rep.getJoursConsommes())
                .montantAnnule(rep.getMontantConsomme())
                .valeurJourHistorique(rep.getValeurJourCalculee())
                .dateAnnulation(rep.getDateAnnulation())
                .motifAnnulation(rep.getMotifAnnulation())
                .annulePar("Inconnu")  // TODO: récupérer via rep.getAnnulationParId()
                .annuleParId(rep.getAnnulationParDemandeId())
                .build();
    }

    private String formaterMois(String moisReference) {
        java.time.YearMonth ym = java.time.YearMonth.parse(moisReference);
        return ym.getMonth().getDisplayName(
                java.time.format.TextStyle.FULL,
                java.util.Locale.FRANCE) + " " + ym.getYear();
    }
}