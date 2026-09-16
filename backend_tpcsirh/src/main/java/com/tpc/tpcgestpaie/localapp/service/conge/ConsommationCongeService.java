package com.tpc.tpcgestpaie.localapp.service.conge;

import com.tpc.tpcgestpaie.localapp.dto.conge.annulation.DetailRestaurationMoisDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.annulation.ResultatAnnulationDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.consommation.DetailConsommationMoisDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.consommation.ResultatConsommationDTO;
import com.tpc.tpcgestpaie.localapp.enums.StatutDemandeAbsence;
import com.tpc.tpcgestpaie.localapp.enums.TypeRepartition;
import com.tpc.tpcgestpaie.localapp.exception.conge.SoldeInsuffisantException;
import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.conge.ProvisionConge;
import com.tpc.tpcgestpaie.localapp.model.conge.RepartitionConsommation;
import com.tpc.tpcgestpaie.localapp.repository.absence.DemandeAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.conge.CongeProvisionCongeRepository;
import com.tpc.tpcgestpaie.localapp.repository.conge.RepartitionConsommationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsommationCongeService {

    private final CongeProvisionCongeRepository provisionRepository;
    private final RepartitionConsommationRepository repartitionRepository;
    private final DemandeAbsenceRepository demandeRepository;

    /**
     * CONSOMMATION : Déduit les provisions lors de la confirmation de départ
     * Algorithme FIFO avec fractionnement sur plusieurs mois si nécessaire
     *
     * @param employeId ID de l'employé
     * @param joursADeduire Nombre de jours à déduire
     * @param demandeId ID de la demande de congé
     * @return Résultat détaillé de la consommation
     */

    /**
     * 🎯 CONSOMMATION FIFO : Priorité absolue à la provision initiale
     */
    @Transactional
    public ResultatConsommationDTO consommerProvisions(Long employeId,
                                                       BigDecimal joursADeduire,
                                                       Long demandeId) {

        DemandeAbsence demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée: " + demandeId));

        if (demande.isSoldeDejaDeduit()) {
            throw new RuntimeException("Cette demande a déjà consommé des provisions");
        }

        // 🔥 Récupération FIFO avec priorité initiale
        List<ProvisionConge> provisions = provisionRepository
                .findDisponiblesPourConsommationFifo(employeId);

        if (provisions.isEmpty()) {
            throw new SoldeInsuffisantException("Aucune provision disponible");
        }

        // Log ordre FIFO
        log.debug("Ordre FIFO pour employe {}:", employeId);
        provisions.forEach(p -> log.debug("  {} ({}): {} jours [type={}]",
                p.getMoisReference(), p.getId(), p.getJoursRestant(), p.getTypeProvision()));

        // Vérification solde total
        BigDecimal soldeDisponible = provisions.stream()
                .map(ProvisionConge::getJoursRestant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (soldeDisponible.compareTo(joursADeduire) < 0) {
            throw new SoldeInsuffisantException(
                    String.format("Solde insuffisant. Disponible: %s, Demandé: %s",
                            soldeDisponible, joursADeduire));
        }

        // Consommation
        List<DetailConsommationMoisDTO> details = new ArrayList<>();
        BigDecimal resteAConsommer = joursADeduire;
        BigDecimal montantTotal = BigDecimal.ZERO;

        boolean initialeEpuisee = false;
        boolean initialePartielle = false;
        BigDecimal joursRestantsInitiale = BigDecimal.ZERO;
        int nbProvisionsNormales = 0;

        for (ProvisionConge provision : provisions) {
            if (resteAConsommer.compareTo(BigDecimal.ZERO) <= 0) break;

            boolean estInitiale = provision.estProvisionInitiale();
            BigDecimal disponible = provision.getJoursRestant();
            BigDecimal pris = disponible.min(resteAConsommer);

            // Calcul valeur
            BigDecimal valeurJour = calculerValeurJour(provision);
            BigDecimal montant = valeurJour.multiply(pris).setScale(2, RoundingMode.HALF_UP);

            // Mise à jour provision
            provision.consommer(pris, montant);
            provisionRepository.save(provision);

            // Répartition
            RepartitionConsommation rep = RepartitionConsommation.builder()
                    .demandeAbsence(demande)
                    .provisionConge(provision)
                    .joursConsommes(pris)
                    .montantConsomme(montant)
                    .valeurJourCalculee(valeurJour)
                    .typeRepartition(TypeRepartition.NORMALE)
                    .build();
            repartitionRepository.save(rep);

            // Tracking
            if (estInitiale) {
                joursRestantsInitiale = provision.getJoursRestant();
                if (joursRestantsInitiale.compareTo(BigDecimal.ZERO) == 0) {
                    initialeEpuisee = true;
                } else {
                    initialePartielle = true;
                }
            } else {
                nbProvisionsNormales++;
            }

            // DTO détail
            details.add(DetailConsommationMoisDTO.builder()
                    .provisionId(provision.getId())
                    .moisReference(provision.getMoisReference())
                    .moisLibelle(formaterMois(provision, estInitiale))
                    .joursAcquisMois(provision.getJoursAcquis())
                    .joursPris(pris)
                    .joursRestantsApres(provision.getJoursRestant())
                    .montantPris(montant)
                    .valeurJour(valeurJour)
                    .typeProvision(provision.getTypeProvision().name())
                    .estProvisionInitiale(estInitiale)
                    .build());

            resteAConsommer = resteAConsommer.subtract(pris);
            montantTotal = montantTotal.add(montant);
        }

        // Mise à jour demande
        demande.marquerSoldeDeduit(montantTotal);
        demandeRepository.save(demande);

        // Message contextuel
        String message = buildMessage(details, initialeEpuisee, initialePartielle, joursRestantsInitiale);

        return ResultatConsommationDTO.builder()
                .demandeId(demandeId)
                .joursTotal(joursADeduire)
                .montantTotal(montantTotal)
                .nombreMoisTouches(details.size())
                .nombreProvisionsNormalesTouches(nbProvisionsNormales)
                .detailsParMois(details)
                .provisionInitialeEpuisee(initialeEpuisee)
                .provisionInitialePartiellementConsommee(initialePartielle)
                .joursRestantsDansProvisionInitiale(joursRestantsInitiale)
                .message(message)
                .build();
    }

    private BigDecimal calculerValeurJour(ProvisionConge p) {
        if (p.estProvisionInitiale() && p.getProvisionMensuelle().compareTo(BigDecimal.ZERO) > 0) {
            return p.getProvisionMensuelle()
                    .divide(p.getJoursAcquis(), 4, RoundingMode.HALF_UP);
        }
        if (p.getJoursAcquis().compareTo(BigDecimal.ZERO) > 0) {
            return p.getProvisionMensuelle()
                    .divide(p.getJoursAcquis(), 4, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private String formaterMois(ProvisionConge p, boolean estInitiale) {
        java.time.YearMonth ym = java.time.YearMonth.parse(p.getMoisReference());
        String base = ym.getMonth().getDisplayName(
                java.time.format.TextStyle.FULL, java.util.Locale.FRANCE) + " " + ym.getYear();
        return estInitiale ? base + " [SOLDE INITIAL]" : base;
    }

    private String buildMessage(List<DetailConsommationMoisDTO> details,
                                boolean initialeEpuisee,
                                boolean initialePartielle,
                                BigDecimal restantInitiale) {

        StringBuilder msg = new StringBuilder();
        msg.append(String.format("Congé confirmé: %s jours, %s €\n",
                details.stream().map(DetailConsommationMoisDTO::getJoursPris)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                details.stream().map(DetailConsommationMoisDTO::getMontantPris)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)));

        if (initialeEpuisee) {
            msg.append("✅ Solde initial historique entièrement épuisé.\n");
            msg.append("Les prochains congés utiliseront les mois récents.");
        } else if (initialePartielle) {
            msg.append(String.format("📌 Solde initial: %s jours restants.\n", restantInitiale));
            msg.append("Vous utilisez toujours vos jours historiques.");
        }

        return msg.toString();
    }
//    @Transactional
//    public ResultatConsommationDTO consommerProvisions(Long employeId,
//                                                       BigDecimal joursADeduire,
//                                                       Long demandeId) {
//
//            DemandeAbsence demande = demandeRepository.findById(demandeId)
//                .orElseThrow(() -> new RuntimeException("Demande non trouvée: " + demandeId));
//
//        // Vérification: pas déjà consommée
//        if (demande.isSoldeDejaDeduit()) {
//            throw new RuntimeException("Cette demande a déjà consommé des provisions");
//        }
//
//        // Récupération des provisions disponibles (FIFO)
//        List<ProvisionConge> provisions = provisionRepository
//                .findDisponiblesPourConsommation(employeId);
//
//        if (provisions.isEmpty()) {
//            throw new SoldeInsuffisantException("Aucune provision disponible");
//        }
//
//        // Calcul du solde total disponible
//        BigDecimal soldeDisponible = provisions.stream()
//                .map(ProvisionConge::getJoursRestant)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        if (soldeDisponible.compareTo(joursADeduire) < 0) {
//            throw new SoldeInsuffisantException(
//                    String.format("Solde insuffisant. Disponible: %s, Demandé: %s",
//                            soldeDisponible, joursADeduire));
//        }
//
//        // Consommation FIFO avec fractionnement
//        List<DetailConsommationMoisDTO> details = new ArrayList<>();
//        BigDecimal resteAConsommer = joursADeduire;
//        BigDecimal montantTotal = BigDecimal.ZERO;
//
//        for (ProvisionConge provision : provisions) {
//            if (resteAConsommer.compareTo(BigDecimal.ZERO) <= 0) break;
//
//            BigDecimal disponible = provision.getJoursRestant();
//            BigDecimal pris = disponible.min(resteAConsommer);
//
//            // Calcul de la valeur journalière de CE mois
//            BigDecimal valeurJour = provision.getProvisionMensuelle()
//                    .divide(provision.getJoursAcquis(), 4, RoundingMode.HALF_UP);
//            BigDecimal montantMois = valeurJour.multiply(pris)
//                    .setScale(2, RoundingMode.HALF_UP);
//
//            // Mise à jour de la provision (méthode métier)
//            provision.consommer(pris, montantMois);
//            provisionRepository.save(provision);
//
//            // Création de la répartition (traçabilité)
//            RepartitionConsommation rep = new RepartitionConsommation();
//            rep.setDemandeAbsence(demande);
//            rep.setProvisionConge(provision);
//            rep.setJoursConsommes(pris);
//            rep.setMontantConsomme(montantMois);
//            rep.setValeurJourCalculee(valeurJour);
//            repartitionRepository.save(rep);
//
//            // Détail pour le retour
//            details.add(DetailConsommationMoisDTO.builder()
//                    .provisionId(provision.getId())
//                    .moisReference(provision.getMoisReference())
//                    .moisLibelle(formaterMois(provision.getMoisReference()))
//                    .joursAcquisMois(provision.getJoursAcquis())
//                    .joursPris(pris)
//                    .joursRestantsApres(provision.getJoursRestant())
//                    .montantPris(montantMois)
//                    .valeurJour(valeurJour)
//                    .tauxJournalier(valeurJour)
//                    .statutMois(provision.getStatut().name())
//                    .build());
//
//            resteAConsommer = resteAConsommer.subtract(pris);
//            montantTotal = montantTotal.add(montantMois);
//
//            log.debug("Consommation sur {}: {} jours, {} €",
//                    provision.getMoisReference(), pris, montantMois);
//        }
//
//        // Mise à jour de la demande
//        demande.marquerSoldeDeduit(montantTotal);
//        demandeRepository.save(demande);
//
//        log.info("Consommation terminée: {} jours, {} € sur {} mois",
//                joursADeduire, montantTotal, details.size());
//
//        return ResultatConsommationDTO.builder()
//                .demandeId(demandeId)
//                .joursTotal(joursADeduire)
//                .montantTotal(montantTotal)
//                .nombreMoisTouches(details.size())
//                .detailsParMois(details)
//                .message(buildMessageConfirmation(details, montantTotal))
//                .build();
//    }

    /**
     * ANNULATION : Restaure les provisions lors d'une annulation de congé
     * Opération inverse de la consommation
     *
     * @param demandeId ID de la demande à annuler
     * @param motif Motif de l'annulation
     * @param utilisateurId ID de l'utilisateur qui annule
     * @return Résultat de la restauration
     */
    @Transactional
    public ResultatAnnulationDTO annulerConsommation(Long demandeId,
                                                     String motif,
                                                     Long utilisateurId) {

        log.info("Début annulation consommation: demande={}, motif={}",
                demandeId, motif);

        DemandeAbsence demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée: " + demandeId));

        // Vérification: a bien été consommée
        if (!demande.isSoldeDejaDeduit()) {
            throw new RuntimeException("Cette demande n'a pas consommé de provisions");
        }

        // Récupération des répartitions actives
        List<RepartitionConsommation> repartitions = repartitionRepository
                .findActivesByDemandeOrderByMois(demandeId);

        if (repartitions.isEmpty()) {
            throw new RuntimeException("Aucune répartition trouvée pour cette demande");
        }

        // Restauration mois par mois
        List<DetailRestaurationMoisDTO> details = new ArrayList<>();
        BigDecimal totalJoursRestaures = BigDecimal.ZERO;
        BigDecimal totalMontantRestaure = BigDecimal.ZERO;

        for (RepartitionConsommation rep : repartitions) {
            ProvisionConge provision = rep.getProvisionConge();

            BigDecimal joursRestaures = rep.getJoursConsommes();
            BigDecimal montantRestaure = rep.getMontantConsomme();

            // Restauration de la provision (méthode métier)
            provision.restaurer(joursRestaures, montantRestaure);
            provisionRepository.save(provision);

            // Annulation de la répartition (soft delete avec historique)
            rep.annuler(motif, demandeId);
            repartitionRepository.save(rep);

            // Détail pour le retour
            details.add(DetailRestaurationMoisDTO.builder()
                    .provisionId(provision.getId())
                    .moisReference(provision.getMoisReference())
                    .moisLibelle(formaterMois(provision.getMoisReference()))
                    .joursRestaures(joursRestaures)
                    .montantRestaure(montantRestaure)
                    .valeurJourHistorique(rep.getValeurJourCalculee())
                    .nouveauSoldeJours(provision.getJoursRestant())
                    .nouveauSoldeMontant(provision.getSoldeFinancier())
                    .nouveauStatut(provision.getStatut().name())
                    .build());

            totalJoursRestaures = totalJoursRestaures.add(joursRestaures);
            totalMontantRestaure = totalMontantRestaure.add(montantRestaure);

            log.debug("Restauration sur {}: {} jours, {} €",
                    provision.getMoisReference(), joursRestaures, montantRestaure);
        }

        // Mise à jour de la demande
        demande.annulerDeductionSolde();
        demande.setStatut(StatutDemandeAbsence.ANNULE); // ou autre statut selon ton enum
        demandeRepository.save(demande);

        log.info("Annulation terminée: {} jours, {} € restaurés sur {} mois",
                totalJoursRestaures, totalMontantRestaure, details.size());

        return ResultatAnnulationDTO.builder()
                .demandeId(demandeId)
                .joursTotalRestaures(totalJoursRestaures)
                .montantTotalRestaure(totalMontantRestaure)
                .nombreMoisTouches(details.size())
                .detailsParMois(details)
                .motifAnnulation(motif)
                .dateAnnulation(java.time.LocalDateTime.now())
                .annulePar(utilisateurId)
                .message(buildMessageAnnulation(details, totalMontantRestaure))
                .build();
    }

    // ====== MÉTHODES UTILITAIRES ======

    private String formaterMois(String moisReference) {
        java.time.YearMonth ym = java.time.YearMonth.parse(moisReference);
        return ym.getMonth().getDisplayName(
                java.time.format.TextStyle.FULL,
                java.util.Locale.FRANCE) + " " + ym.getYear();
    }

    private String buildMessageConfirmation(List<DetailConsommationMoisDTO> details,
                                            BigDecimal montantTotal) {
        StringBuilder msg = new StringBuilder();
        msg.append(String.format("Congé confirmé. Montant total: %s €\n", montantTotal));
        msg.append("Détail par mois:\n");

        for (DetailConsommationMoisDTO d : details) {
            msg.append(String.format("- %s: %s jours (%s €)\n",
                    d.getMoisLibelle(), d.getJoursPris(), d.getMontantPris()));
        }
        return msg.toString();
    }

    private String buildMessageAnnulation(List<DetailRestaurationMoisDTO> details,
                                          BigDecimal montantTotal) {
        StringBuilder msg = new StringBuilder();
        msg.append(String.format("Annulation effectuée. Montant restauré: %s €\n", montantTotal));
        msg.append("Détail par mois:\n");

        for (DetailRestaurationMoisDTO d : details) {
            msg.append(String.format("- %s: %s jours restaurés\n",
                    d.getMoisLibelle(), d.getJoursRestaures()));
        }
        return msg.toString();
    }
}