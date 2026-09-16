package com.tpc.tpcgestpaie.localapp.service.dashboard;


import com.tpc.tpcgestpaie.localapp.dto.dashboard.*;
import com.tpc.tpcgestpaie.localapp.model.BulletinPaie;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.AbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.AccidentTravailRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import com.tpc.tpcgestpaie.localapp.util.Status;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ContratEmployeRepository contratEmployeRepository;
    private final BulletinPaieRepository bulletinPaieRepository;
    private final AbsenceRepository absenceRepository;
    private final AccidentTravailRepository accidentTravailRepository;
    private final CompanyRepository companyRepository;

    public DashboardService(ContratEmployeRepository contratEmployeRepository, BulletinPaieRepository bulletinPaieRepository, AbsenceRepository absenceRepository, AccidentTravailRepository accidentTravailRepository, CompanyRepository companyRepository) {
        this.contratEmployeRepository = contratEmployeRepository;
        this.bulletinPaieRepository = bulletinPaieRepository;
        this.absenceRepository = absenceRepository;
        this.accidentTravailRepository = accidentTravailRepository;
        this.companyRepository = companyRepository;
    }
    public DashboardEffectifOverviewDTO getOverview(Long entrepriseId) {
        Long total = contratEmployeRepository.countEmployesActifs(entrepriseId);

        // ---- GENRE ----
        Map<String, Long> genre = contratEmployeRepository.countByGenre(entrepriseId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        // On complète avec les valeurs manquantes
        genre.putIfAbsent("MASCULIN", 0L);
        genre.putIfAbsent("FEMININ", 0L);

        // ---- CATEGORIE ----
        Map<String, Long> categorie = contratEmployeRepository.countByCategorie(entrepriseId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

// On complète les catégories manquantes
        categorie.putIfAbsent("TRAVAILLEUR PAYE A L'HEURE", 0L);
        categorie.putIfAbsent("EMPLOYE, MANOEUVRE ET OUVRIER", 0L);
        categorie.putIfAbsent("AGENT DE MAITRISE, CADRE ET ASSIMILE", 0L);

// ---- TRANCHE AGE ----
        Map<String, Long> tranches = contratEmployeRepository.countByTrancheAge(entrepriseId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

// On complète avec les tranches manquantes
        tranches.putIfAbsent("<30", 0L);
        tranches.putIfAbsent("30-50", 0L);
        tranches.putIfAbsent(">50", 0L);


        // ---- DTO ----
        DashboardEffectifOverviewDTO dto = new DashboardEffectifOverviewDTO();
        dto.setTotalActifs(total);
        dto.setRepartitionGenre(genre);
        dto.setRepartitionCategorie(categorie);
        dto.setRepartitionTrancheAge(tranches);

        return dto;
    }

    public DashboardMasseSalarialeDTO getMasseSalarialeAnnuelle(Long entrepriseId) {
        int anneeEnCours = Year.now().getValue();

        // Récupérer tous les bulletins validés de l'année en cours pour l'entreprise
        List<BulletinPaie> bulletins = bulletinPaieRepository
                .findByCompanyIdAndYearAndStatut(entrepriseId, anneeEnCours, Status.VALIDATED);

        // Somme des salaires bruts
        BigDecimal masse = bulletins.stream()
                .map(BulletinPaie::getSalaireBrut)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DashboardMasseSalarialeDTO dto = new DashboardMasseSalarialeDTO();
        dto.setEntrepriseId(entrepriseId);
        dto.setMasseSalariale(masse);
        return dto;
    }

    @Transactional
    public SalaireMoyenSexeDTO getSalaireMoyenParSexe(Long entrepriseId) {
        int anneeEnCours = Year.now().getValue();

        // Récupérer tous les bulletins validés de l'année pour l'entreprise
        List<BulletinPaie> bulletins = bulletinPaieRepository
                .findByCompanyIdAndYearAndStatut(entrepriseId, anneeEnCours, Status.VALIDATED);

        // Calcul du salaire moyen par sexe
        BigDecimal salaireHomme = bulletins.stream()
                .filter(b -> "MASCULIN".equalsIgnoreCase(b.getEmploye().getSexe()))
                .map(BulletinPaie::getSalaireBrut)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long nbHomme = bulletins.stream()
                .filter(b -> "MASCULIN".equalsIgnoreCase(b.getEmploye().getSexe()))
                .count();

        BigDecimal salaireFemme = bulletins.stream()
                .filter(b -> "FEMININ".equalsIgnoreCase(b.getEmploye().getSexe()))
                .map(BulletinPaie::getSalaireBrut)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long nbFemme = bulletins.stream()
                .filter(b -> "FEMININ".equalsIgnoreCase(b.getEmploye().getSexe()))
                .count();

        BigDecimal moyenneHomme = nbHomme > 0 ? salaireHomme.divide(BigDecimal.valueOf(nbHomme), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        BigDecimal moyenneFemme = nbFemme > 0 ? salaireFemme.divide(BigDecimal.valueOf(nbFemme), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        return new SalaireMoyenSexeDTO(moyenneHomme, moyenneFemme);
    }

    @Transactional
    public RepartitionDepartementDTO getRepartitionParDepartement(Long entrepriseId) {
        // Récupérer tous les contrats actifs de l'entreprise
        List<ContratEmploye> contrats = contratEmployeRepository.findByCompanyIdAndActifTrue(entrepriseId);

        // Regrouper par département via le contrat -> employé -> département


        Map<String, Long> repartition = contrats.stream()
                .filter(c -> c.getDepartement() != null)
                .collect(Collectors.groupingBy(
                        contrat -> contrat.getDepartement().getLibelle(),
                        Collectors.counting()
                ));


        return new RepartitionDepartementDTO(repartition);
    }

    @Transactional
    public RepartitionTypeContratDTO getRepartitionParTypeContrat(Long entrepriseId) {
        // Récupérer tous les contrats actifs de l'entreprise
        List<ContratEmploye> contrats = contratEmployeRepository.findByCompanyIdAndActifTrue(entrepriseId);

        // Regrouper par type de contrat
        Map<String, Long> repartition = contrats.stream()
                .collect(Collectors.groupingBy(
                        ContratEmploye::getType_contrat,
                        Collectors.counting()
                ));

// On s’assure que CDI et CDD existent toujours
        repartition.putIfAbsent("CDI", 0L);
        repartition.putIfAbsent("CDD", 0L);

        return new RepartitionTypeContratDTO(repartition);
    }

    @Transactional
    public RepartitionSituationFamilleDTO getRepartitionSituationFamille(Long entrepriseId) {
        // Récupérer tous les contrats actifs de l'entreprise
        List<ContratEmploye> contrats = contratEmployeRepository.findByCompanyIdAndActifTrue(entrepriseId);

        // Regrouper par situation de famille via l'employé
        Map<String, Long> repartition = contrats.stream()
                .filter(c -> c.getEmploye().getSituationMatrimoniale() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getEmploye().getSituationMatrimoniale(),
                        Collectors.counting()
                ));

// On complète les situations manquantes
        repartition.putIfAbsent("CELIBATAIRE_SANS_ENFANT", 0L);
        repartition.putIfAbsent("CELIBATAIRE_AVEC_ENFANT", 0L);
        repartition.putIfAbsent("MARIE", 0L);
        repartition.putIfAbsent("DIVORCE", 0L);


        return new RepartitionSituationFamilleDTO(repartition);
    }

    @Transactional
    public RepartitionAncienneteDTO getRepartitionParAnciennete(Long entrepriseId) {
        List<ContratEmploye> contrats = contratEmployeRepository.findByCompanyIdAndActifTrue(entrepriseId);
        Map<String, Long> repartition = contrats.stream()
                .map(ContratEmploye::getDate_debut) // date de début du contrat
                .map(dateDebut -> {
                    int anneeAnciennete = Period.between(dateDebut, LocalDate.now()).getYears();
                    if (anneeAnciennete <= 5) return "0-5 ans";
                    else if (anneeAnciennete <= 10) return "6-10 ans";
                    else return ">10 ans";
                })
                .collect(Collectors.groupingBy(tranche -> tranche, Collectors.counting()));

// On complète les tranches manquantes
        repartition.putIfAbsent("0-5 ans", 0L);
        repartition.putIfAbsent("6-10 ans", 0L);
        repartition.putIfAbsent(">10 ans", 0L);

        return new RepartitionAncienneteDTO(repartition);    }

    public Map<String, Long> getAbsencesEnCoursParType(Long companyId) {
        // 🔹 1. Liste des types d’absence connus (ta référence)
        List<String> allTypes = Arrays.asList(
                "CONGE",
                "CONVENANCE PERSONNELLE",
                "MALADIE",
                "MARIAGE",
                "SABBATIQUE",
                "FORMATION",
                "DECES",
                "PATERNITE",
                "MATERNITE",
                "NAISSANCE AU FOYER",
                "CONGE ADMINISTRATIF"
        );

        // 🔹 2. Récupération des valeurs en BDD
        Map<String, Long> result = absenceRepository.countAbsencesEnCoursParType(companyId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        // 🔹 3. Compléter avec 0 si manquant
        Map<String, Long> fullResult = new LinkedHashMap<>();
        for (String type : allTypes) {
            fullResult.put(type, result.getOrDefault(type, 0L));
        }

        return fullResult;
    }

    public Map<String, Long> getAbsencesEtConges(Long companyId) {
        // Liste fixe des deux catégories
        List<String> categories = Arrays.asList("CONGE", "ABSENCE");

        // Récupération des données depuis le repository
        Map<String, Long> result = absenceRepository.countAbsencesEtConges(companyId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        // Compléter avec 0 si une catégorie est absente
        Map<String, Long> fullResult = new LinkedHashMap<>();
        for (String cat : categories) {
            fullResult.put(cat, result.getOrDefault(cat, 0L));
        }

        return fullResult;
    }

    public Long getAccidentsTravail(Long companyId) {
        return accidentTravailRepository.countAccidentsTravail(companyId);
    }


    public AbsencesDashboardDTO getDashboardAbsences(Long companyId) {
        Map<String, Long> absencesEnCours = getAbsencesEnCoursParType(companyId);
        Map<String, Long> absencesVsConges = getAbsencesEtConges(companyId);
        Long accidentsTravail = getAccidentsTravail(companyId);

        return new AbsencesDashboardDTO(absencesEnCours, absencesVsConges, accidentsTravail);
    }

    @Transactional
    public PaieDashboardDTO getDashboardPaie(Long companyId) {
        Map<String, PaieParStatutDTO> result = new LinkedHashMap<>();

        // ✅ Statuts à traiter
        List<String> statuts = List.of(Status.VALIDATED, Status.PENDING);

        for (String statut : statuts) {
            // 🔹 Récupération des totaux (net + primes/bonus/retenues)
            List<Object[]> sumsList = bulletinPaieRepository.sumNetEtPrimesRetenuesByCompanyAndStatut(companyId, statut);

            // 🔹 Prendre la première ligne si elle existe
            Object[] sums = sumsList.isEmpty() ? new Object[6] : sumsList.get(0);

            BigDecimal net = sums[0] != null ? (BigDecimal) sums[0] : BigDecimal.ZERO;
            BigDecimal totalPrimes = sums[1] != null ? (BigDecimal) sums[1] : BigDecimal.ZERO;
            BigDecimal totalRetenues = sums[2] != null ? (BigDecimal) sums[2] : BigDecimal.ZERO;
            BigDecimal totalCnss = sums[3] != null ? (BigDecimal) sums[3] : BigDecimal.ZERO;
            BigDecimal totalIpts = sums[4] != null ? (BigDecimal) sums[4] : BigDecimal.ZERO;
            BigDecimal totalAib = sums[5] != null ? (BigDecimal) sums[5] : BigDecimal.ZERO;

            // 🔹 Créer le DTO pour ce statut
            PaieParStatutDTO dto = new PaieParStatutDTO();
            dto.setNet(net);
            dto.setTotalPrimes(totalPrimes);
            dto.setTotalRetenues(totalRetenues);
            dto.setTotalCnss(totalCnss);
            dto.setTotalIpts(totalIpts);
            dto.setTotalAib(totalAib);

            result.put(statut, dto);
        }

        // 🔹 DTO global
        PaieDashboardDTO dashboardDTO = new PaieDashboardDTO();
        dashboardDTO.setParStatut(result);

        return dashboardDTO;
    }

    @Transactional
    public KpiDashboardDTO getDashboardKpis(Long companyId) {

        KpiDashboardDTO dto = new KpiDashboardDTO();

        // 🔹 1. Employés actifs
        List<ContratEmploye> actifs = contratEmployeRepository.findEmployesActifs(companyId);
        int totalActifs = actifs.size();

        // 🔹 2. Turnover : départs / total
        long depart = contratEmployeRepository.countDepartUnique(companyId);
        dto.setTauxTurnover(totalActifs + depart > 0 ? (depart * 100.0 / (totalActifs + depart)) : 0.0);

        // 🔹 3. Absentéisme : jours d’absence / jours théoriques
        double joursAbsence = absenceRepository.sumDureeAbsencesActifs(companyId);
        long joursTravailTotal = totalActifs * 30 * 12; // ex: 30 jours/mois, 12 mois
        dto.setTauxAbsentisme(joursTravailTotal > 0 ? (joursAbsence * 100.0 / joursTravailTotal) : 0.0);

        // 🔹 4. Coût moyen par employé
        BigDecimal masseSalariale = bulletinPaieRepository.sumNetPayeByCompany(companyId);
        dto.setCoutMoyenEmploye(totalActifs > 0 ? masseSalariale.divide(BigDecimal.valueOf(totalActifs), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        // 🔹 5. Ratio masse salariale / CA
        BigDecimal chiffreAffaires = companyRepository.findChiffreAffairesById(companyId);

        if (chiffreAffaires != null && chiffreAffaires.compareTo(BigDecimal.ZERO) > 0) {
            dto.setRatioMasseSalarialeCa(masseSalariale.divide(chiffreAffaires, 2, RoundingMode.HALF_UP));
        } else {
            dto.setRatioMasseSalarialeCa(BigDecimal.ZERO);
        }

        return dto;
    }
}
