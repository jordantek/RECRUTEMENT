package com.tpc.tpcgestpaie.localapp.service.conge;

import com.tpc.tpcgestpaie.localapp.dto.conge.solde.*;
import com.tpc.tpcgestpaie.localapp.enums.StatutDemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.conge.ProvisionConge;
import com.tpc.tpcgestpaie.localapp.model.conge.RepartitionConsommation;
import com.tpc.tpcgestpaie.localapp.repository.absence.DemandeAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.conge.CongeProvisionCongeRepository;
import com.tpc.tpcgestpaie.localapp.repository.conge.RepartitionConsommationRepository;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SoldeCongeService {

    private final CongeProvisionCongeRepository provisionRepository;
    private final RepartitionConsommationRepository repartitionRepository;
    private final EmployeService employeService;
    private final DemandeAbsenceRepository demandeRepository;

    // ============================================
    // MÉTHODES PUBLIQUES PRINCIPALES
    // ============================================

    /**
     * Solde actuel (aujourd'hui)
     */
    public SoldeCongeDTO getSoldeActuel(Long employeId) {
        return getSoldeAdate(employeId, LocalDate.now());
    }

    /**
     * Solde à une date donnée (rétrospectif)
     * Permet de savoir combien de jours étaient disponibles à une date précise
     */
    @Transactional(readOnly = true)
    public SoldeCongeDTO getSoldeAdate(Long employeId, LocalDate dateCible) {

        // Vérification employé existe
        Employe employe = employeService.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé: " + employeId));

        String moisCible = YearMonth.from(dateCible).toString();

        // Provisions générées jusqu'à cette date
        List<ProvisionConge> provisions = provisionRepository
                .findByEmployeIdAndMoisReferenceLessThanEqual(employeId, moisCible);

        // Consommations effectuées avant ou le jour de la date cible
        LocalDateTime finJournee = dateCible.atTime(23, 59, 59);
        List<RepartitionConsommation> consommations = repartitionRepository
                .findActivesByEmployeJusquADate(employeId, finJournee);

        // Calcul des totaux
        BigDecimal totalAcquis = calculerTotalAcquis(provisions);
        BigDecimal totalConsomme = calculerTotalConsomme(consommations);
        BigDecimal soldeJours = totalAcquis.subtract(totalConsomme);

        // Calcul de la valeur financière du solde (méthode FIFO)
        BigDecimal valeurSolde = calculerValeurSoldeFIFO(provisions, consommations);

        // Calcul des totaux financiers
        BigDecimal totalProvisions = calculerTotalProvisions(provisions);
        BigDecimal totalMontantConsomme = calculerTotalMontantConsomme(consommations);

        // Construction du détail par mois
        List<DetailSoldeMoisDTO> details = construireDetailsParMois(provisions, consommations);

        // Calcul des alertes
        Boolean alerteSoldeFaible = soldeJours.compareTo(new BigDecimal("5")) < 0 && soldeJours.compareTo(BigDecimal.ZERO) > 0;
        Boolean alerteSoldeEpuise = soldeJours.compareTo(BigDecimal.ZERO) == 0;

        return SoldeCongeDTO.builder()
                .employeId(employeId)
                .nomEmploye(employe.getNom() + " " + employe.getPrenom())
                .matricule(employe.getMatricule())
                .dateCalcul(dateCible)
                .totalJoursAcquis(totalAcquis)
                .totalJoursConsommes(totalConsomme)
                .soldeJoursDisponibles(soldeJours.max(BigDecimal.ZERO))
                .valeurEstimeeSolde(valeurSolde)
                .montantTotalProvisionne(totalProvisions)
                .montantTotalConsomme(totalMontantConsomme)
//                .detailsParMois(details)
                .nombreMoisDisponibles(compterMoisDisponibles(details))
                .nombreMoisClotures(compterMoisClotures(details))
                .prochainMoisFifo(trouverProchainMoisFIFO(details))
                .joursDisponiblesProchainMois(trouverJoursProchainMoisFIFO(details))
                .alerteSoldeFaible(alerteSoldeFaible)
                .alerteSoldeEpuise(alerteSoldeEpuise)
                .messageAlerte(construireMessageAlerte(alerteSoldeFaible, alerteSoldeEpuise, soldeJours))
                .build();
    }

    /**
     * Historique complet de l'employé avec évolution mensuelle
     */
    @Transactional(readOnly = true)
    public HistoriqueEmployeDTO getHistoriqueComplet(Long employeId) {

        Employe employe = employeService.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé: " + employeId));

        // Toutes les provisions (chronologique)
        List<ProvisionConge> provisions = provisionRepository
                .findByEmployeIdOrderByMoisReferenceAsc(employeId);

        // Toutes les consommations actives
        List<RepartitionConsommation> consommations = repartitionRepository
                .findActivesByEmployeJusquADate(employeId, LocalDateTime.now());

        // Toutes les demandes de l'employé
        List<DemandeAbsence> demandes = demandeRepository
                .findByEmployeDemandeurIdOrderByCreatedAtDesc(employeId);

        // Calcul des statistiques globales
        BigDecimal totalAcquisHistorique = calculerTotalAcquis(provisions);
        BigDecimal totalConsomme = calculerTotalConsomme(consommations);
        BigDecimal totalAnnule = calculerTotalAnnule(employeId);
        BigDecimal soldeActuel = totalAcquisHistorique.subtract(totalConsomme);

        // Construction de l'évolution mensuelle avec cumuls
        List<MoisHistoriqueDTO> evolutionMensuelle = construireEvolutionMensuelle(provisions, consommations);

        // 5 dernières demandes
        List<DemandeResumeDTO> dernieresDemandes = demandes.stream()
                .limit(5)
                .map(this::mapperDemandeResume)
                .collect(Collectors.toList());

        // Alertes
        List<String> alertes = construireAlertes(employeId, demandes, soldeActuel);

        return HistoriqueEmployeDTO.builder()
                .employeId(employeId)
                .nomEmploye(employe.getNom() + " " + employe.getPrenom())
                .matricule(employe.getMatricule())
//                .dateEmbauche(employe.getDateEmbauche())
//                .ancienneteAnnees(calculerAnciennete(employe.getDateEmbauche()))
                .soldeJoursActuel(soldeActuel)
                .soldeMontantActuel(calculerValeurSoldeFIFO(provisions, consommations))
                .totalJoursAcquisHistorique(totalAcquisHistorique)
                .totalJoursConsommesHistorique(totalConsomme)
                .totalJoursAnnules(totalAnnule)
                .nombreTotalDemandes(demandes.size())
                .nombreDemandesEnCours(compterDemandesParStatut(demandes, StatutDemandeAbsence.EN_COURS))
                .nombreDemandesTerminees(compterDemandesParStatut(demandes, StatutDemandeAbsence.TERMINEE))
                .nombreDemandesAnnulees(compterDemandesParStatut(demandes, StatutDemandeAbsence.ANNULE))
                .evolutionMensuelle(evolutionMensuelle)
                .dernieresDemandes(dernieresDemandes)
                .alertes(alertes)
                .build();
    }

    /**
     * Rapport périodique pour RH (entre deux dates)
     */
    @Transactional(readOnly = true)
    public RapportPeriodiqueDTO getRapportPeriode(Long employeId, LocalDate debut, LocalDate fin) {
        log.info("Génération rapport période {} - {} pour employe {}", debut, fin, employeId);

        Employe employe = employeService.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé: " + employeId));

        // Consommations sur la période
        LocalDateTime debutDt = debut.atStartOfDay();
        LocalDateTime finDt = fin.atTime(23, 59, 59);

        List<RepartitionConsommation> consommationsPeriode = repartitionRepository
                .findByEmployeAndPeriode(employeId, debutDt, finDt);

        // Demandes sur la période
        List<DemandeAbsence> demandesPeriode = demandeRepository
                .findByEmployeAndPeriode(employeId, debut, fin);

        BigDecimal totalJoursPris = consommationsPeriode.stream()
                .map(RepartitionConsommation::getJoursConsommes)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMontant = consommationsPeriode.stream()
                .map(RepartitionConsommation::getMontantConsomme)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return RapportPeriodiqueDTO.builder()
                .employeId(employeId)
                .nomEmploye(employe.getNom() + " " + employe.getPrenom())
                .periodeDebut(debut)
                .periodeFin(fin)
                .nombreJoursTravailler(demandesPeriode.size())
                .totalJoursCongePris(totalJoursPris)
                .coutTotalConges(totalMontant)
                .detailsMois(construireDetailsRapportPeriode(consommationsPeriode))
                .build();
    }

    // ============================================
    // MÉTHODES DE CALCUL PRIVÉES
    // ============================================

    private BigDecimal calculerTotalAcquis(List<ProvisionConge> provisions) {
        return provisions.stream()
                .map(ProvisionConge::getJoursAcquis)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculerTotalConsomme(List<RepartitionConsommation> consommations) {
        return consommations.stream()
                .map(RepartitionConsommation::getJoursConsommes)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculerTotalProvisions(List<ProvisionConge> provisions) {
        return provisions.stream()
                .map(ProvisionConge::getProvisionMensuelle)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculerTotalMontantConsomme(List<RepartitionConsommation> consommations) {
        return consommations.stream()
                .map(RepartitionConsommation::getMontantConsomme)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculerTotalAnnule(Long employeId) {
        // Récupérer toutes les répartitions annulées de l'employé
        List<RepartitionConsommation> annulations = repartitionRepository
                .findAnnulationsByEmploye(employeId);

        return annulations.stream()
                .map(RepartitionConsommation::getJoursConsommes)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcule la valeur du solde restant selon méthode FIFO
     * Les jours restants sont valorisés aux taux des mois respectifs
     */
    private BigDecimal calculerValeurSoldeFIFO(List<ProvisionConge> provisions,
                                               List<RepartitionConsommation> consommations) {

        // Grouper consommations par mois
        Map<String, BigDecimal> consoParMois = consommations.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getProvisionConge().getMoisReference(),
                        Collectors.reducing(BigDecimal.ZERO,
                                RepartitionConsommation::getJoursConsommes,
                                BigDecimal::add)
                ));

        BigDecimal valeurTotale = BigDecimal.ZERO;

        for (ProvisionConge prov : provisions) {
            BigDecimal dejaConsomme = consoParMois.getOrDefault(prov.getMoisReference(), BigDecimal.ZERO);
            BigDecimal restant = prov.getJoursAcquis().subtract(dejaConsomme);

            if (restant.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal valeurJour = prov.getProvisionMensuelle()
                        .divide(prov.getJoursAcquis(), 4, RoundingMode.HALF_UP);
                valeurTotale = valeurTotale.add(valeurJour.multiply(restant));
            }
        }

        return valeurTotale.setScale(2, RoundingMode.HALF_UP);
    }

    // ============================================
    // CONSTRUCTION DES DTOs
    // ============================================

    private List<DetailSoldeMoisDTO> construireDetailsParMois(
            List<ProvisionConge> provisions,
            List<RepartitionConsommation> consommations) {

        Map<String, List<RepartitionConsommation>> consoParMois = consommations.stream()
                .collect(Collectors.groupingBy(c -> c.getProvisionConge().getMoisReference()));

        return provisions.stream()
                .map(p -> {
                    List<RepartitionConsommation> consoMois = consoParMois
                            .getOrDefault(p.getMoisReference(), List.of());

                    BigDecimal joursConso = consoMois.stream()
                            .map(RepartitionConsommation::getJoursConsommes)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal montantConso = consoMois.stream()
                            .map(RepartitionConsommation::getMontantConsomme)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return DetailSoldeMoisDTO.builder()
                            .provisionId(p.getId())
                            .moisReference(p.getMoisReference())
                            .moisLibelle(formaterMois(p.getMoisReference()))
                            .joursAcquis(p.getJoursAcquis())
                            .salaireBrutMois(p.getSalaireBrutMois())
                            .joursTravaillesMois(p.getJoursTravaillesMois())
                            .provisionInitiale(p.getProvisionMensuelle())
                            .valeurJour(p.getProvisionMensuelle()
                                    .divide(p.getJoursAcquis(), 2, RoundingMode.HALF_UP))
                            .joursConsommes(joursConso)
                            .montantConsomme(montantConso)
                            .joursRestants(p.getJoursAcquis().subtract(joursConso))
                            .soldeFinancier(p.getProvisionMensuelle().subtract(montantConso))
                            .statut(calculerStatutVirtuel(p, joursConso))
                            .pourcentageConsomme(
                                    joursConso.multiply(new BigDecimal("100"))
                                            .divide(p.getJoursAcquis(), 1, RoundingMode.HALF_UP))
                            .dateDerniereConsommation(consoMois.isEmpty() ? null :
                                    consoMois.get(consoMois.size() - 1).getDateConsommation()
                                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .nombreDemandesConcernees((int) consoMois.stream()
                                    .map(c -> c.getDemandeAbsence().getId())
                                    .distinct()
                                    .count())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<MoisHistoriqueDTO> construireEvolutionMensuelle(
            List<ProvisionConge> provisions,
            List<RepartitionConsommation> consommations) {

        Map<String, List<RepartitionConsommation>> consoParMois = consommations.stream()
                .collect(Collectors.groupingBy(c -> c.getProvisionConge().getMoisReference()));

        BigDecimal cumulJours = BigDecimal.ZERO;
        BigDecimal cumulFinancier = BigDecimal.ZERO;
        List<MoisHistoriqueDTO> resultat = new ArrayList<>();

        for (ProvisionConge p : provisions) {
            List<RepartitionConsommation> consoMois = consoParMois
                    .getOrDefault(p.getMoisReference(), List.of());

            BigDecimal joursConso = consoMois.stream()
                    .map(RepartitionConsommation::getJoursConsommes)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal montantConso = consoMois.stream()
                    .map(RepartitionConsommation::getMontantConsomme)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Mise à jour des cumuls
            cumulJours = cumulJours.add(p.getJoursAcquis()).subtract(joursConso);
            cumulFinancier = cumulFinancier.add(p.getProvisionMensuelle()).subtract(montantConso);

            List<ConsommationResumeDTO> detailsConso = consoMois.stream()
                    .map(c -> ConsommationResumeDTO.builder()
                            .demandeId(c.getDemandeAbsence().getId())
                            .jours(c.getJoursConsommes())
                            .montant(c.getMontantConsomme())
                            .dateConsommation(c.getDateConsommation())
                            .build())
                    .collect(Collectors.toList());

            resultat.add(MoisHistoriqueDTO.builder()
                    .moisReference(p.getMoisReference())
                    .moisLibelle(formaterMois(p.getMoisReference()))
                    .joursAcquis(p.getJoursAcquis())
                    .provisionInitiale(p.getProvisionMensuelle())
                    .joursConsommes(joursConso)
                    .montantConsomme(montantConso)
                    .joursRestantsMois(p.getJoursAcquis().subtract(joursConso))
                    .soldeFinancierMois(p.getProvisionMensuelle().subtract(montantConso))
                    .statutMois(calculerStatutVirtuel(p, joursConso))
                    .soldeJoursCumule(cumulJours)
                    .soldeFinancierCumule(cumulFinancier)
                    .consommations(detailsConso)
                    .build());
        }

        return resultat;
    }

    private DemandeResumeDTO mapperDemandeResume(DemandeAbsence d) {
        return DemandeResumeDTO.builder()
                .demandeId(d.getId())
                .periode(d.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " +
                        d.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .jours(d.getJoursEffectifsDeduits())
                .montant(d.getMontantAllocation())
                .statut(d.getStatut().name())
                .dateCreation(d.getCreatedAt())
                .build();
    }

    // ============================================
    // MÉTHODES UTILITAIRES
    // ============================================

    private String formaterMois(String moisReference) {
        YearMonth ym = YearMonth.parse(moisReference);
        return ym.getMonth().getDisplayName(
                java.time.format.TextStyle.FULL,
                java.util.Locale.FRANCE) + " " + ym.getYear();
    }

    private String calculerStatutVirtuel(ProvisionConge provision, BigDecimal conso) {
        if (conso.compareTo(BigDecimal.ZERO) == 0) return "ACTIF";
        if (conso.compareTo(provision.getJoursAcquis()) >= 0) return "CLOTURE";
        return "PARTIELLEMENT_CONSOMME";
    }

    private Integer compterMoisDisponibles(List<DetailSoldeMoisDTO> details) {
        return (int) details.stream()
                .filter(d -> d.getJoursRestants().compareTo(BigDecimal.ZERO) > 0)
                .count();
    }

    private Integer compterMoisClotures(List<DetailSoldeMoisDTO> details) {
        return (int) details.stream()
                .filter(d -> "CLOTURE".equals(d.getStatut()))
                .count();
    }

    private String trouverProchainMoisFIFO(List<DetailSoldeMoisDTO> details) {
        return details.stream()
                .filter(d -> d.getJoursRestants().compareTo(BigDecimal.ZERO) > 0)
                .findFirst()
                .map(DetailSoldeMoisDTO::getMoisLibelle)
                .orElse("Aucun");
    }

    private BigDecimal trouverJoursProchainMoisFIFO(List<DetailSoldeMoisDTO> details) {
        return details.stream()
                .filter(d -> d.getJoursRestants().compareTo(BigDecimal.ZERO) > 0)
                .findFirst()
                .map(DetailSoldeMoisDTO::getJoursRestants)
                .orElse(BigDecimal.ZERO);
    }

    private String construireMessageAlerte(Boolean alerteFaible, Boolean alerteEpuise, BigDecimal solde) {
        if (alerteEpuise) return "Solde de congés épuisé";
        if (alerteFaible) return "Solde faible: " + solde + " jours restants";
        return null;
    }

    private Integer calculerAnciennete(LocalDate dateEmbauche) {
        if (dateEmbauche == null) return 0;
        return YearMonth.from(LocalDate.now()).getYear() - dateEmbauche.getYear();
    }

    private Integer compterDemandesParStatut(List<DemandeAbsence> demandes, StatutDemandeAbsence statut) {
        return (int) demandes.stream()
                .filter(d -> d.getStatut() == statut)
                .count();
    }

    private List<String> construireAlertes(Long employeId, List<DemandeAbsence> demandes, BigDecimal solde) {
        List<String> alertes = new ArrayList<>();

        long enCours = demandes.stream()
                .filter(d -> d.getStatut() == StatutDemandeAbsence.EN_COURS)
                .count();

        if (enCours > 0) {
            alertes.add(enCours + " demande(s) en cours - vérifier les retours");
        }

        if (solde.compareTo(new BigDecimal("5")) < 0) {
            alertes.add("Solde insuffisant pour une semaine de congé");
        }

        return alertes;
    }

    private List<DetailRapportMoisDTO> construireDetailsRapportPeriode(
            List<RepartitionConsommation> consommations) {

        Map<String, List<RepartitionConsommation>> parMois = consommations.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getProvisionConge().getMoisReference(),
                        LinkedHashMap::new, // Preserve order
                        Collectors.toList()
                ));

        return parMois.entrySet().stream()
                .map(e -> {
                    BigDecimal totalJours = e.getValue().stream()
                            .map(RepartitionConsommation::getJoursConsommes)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalMontant = e.getValue().stream()
                            .map(RepartitionConsommation::getMontantConsomme)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return DetailRapportMoisDTO.builder()
                            .moisReference(e.getKey())
                            .moisLibelle(formaterMois(e.getKey()))
                            .joursConsommes(totalJours)
                            .montantConsomme(totalMontant)
                            .nombreDemandes((int) e.getValue().stream()
                                    .map(c -> c.getDemandeAbsence().getId())
                                    .distinct()
                                    .count())
                            .build();
                })
                .collect(Collectors.toList());
    }
}