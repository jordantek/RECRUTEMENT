package com.tpc.tpcgestpaie.localapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.tpc.tpcgestpaie.localapp.CalculUtils.AncienneteCalculator;
import com.tpc.tpcgestpaie.localapp.CalculUtils.ITSCalculator;
import com.tpc.tpcgestpaie.localapp.CalculUtils.SalaryCalculator;
import com.tpc.tpcgestpaie.localapp.CalculUtils.SalaryCalculatorResult;
import com.tpc.tpcgestpaie.localapp.dto.ParametrePaieDTO;
import com.tpc.tpcgestpaie.localapp.dto.TraitementSalaire.ApercuSalaireDTO;
import com.tpc.tpcgestpaie.localapp.dto.TraitementSalaireLogDTO;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.AllocationCongeDetailDTO;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.HistoriqueMontantsDTO;
import com.tpc.tpcgestpaie.localapp.dto.bulletin.BulletinPaieGenerateDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.*;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.model.conge.ProvisionConge;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.TraitementSalaireLogRepository;
import com.tpc.tpcgestpaie.localapp.repository.conge.CongeProvisionCongeRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import com.tpc.tpcgestpaie.localapp.service.anciennete.AncienneteSettingService;
import com.tpc.tpcgestpaie.localapp.service.bulletun.GenerateBulletin;
import com.tpc.tpcgestpaie.localapp.service.paie.*;
import com.tpc.tpcgestpaie.localapp.service.paie.bulletin.BulletinPaieService;
import com.tpc.tpcgestpaie.localapp.util.DateUtils;
import com.tpc.tpcgestpaie.localapp.util.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TraitementSalaireService {

    private final CompanyService companyService;
    private final ContratEmployeService contratEmployeService;
    private final RubriqueService rubriqueService;
    private final ContratEmployeRubriqueService contratEmployeRubriqueService;
    private final HeureSupplementaireService heureSupplementaireService;
    private final MontantRubriqueService montantRubriqueService;
    private final TempsDeTravailService tempsDeTravailService;
    private final BulletinPaieService bulletinPaieService;
    private final List<ApercuSalaireDTO> apercuSalaireDTOList = new ArrayList<>();
    private final EnfantEmployeService enfantEmployeService;
    private final TraitementSalaireLogRepository traitementSalaireLogRepository;
    private final MensualiteService mensualiteService;
    private final PrelevementMensualiteService prelevementMensualiteService;
    private final AcompteService acompteService;
    private final AvanceService avanceService;
    private final AllocationCongeService allocationCongeService;
    private final BulletinPaieRepository bulletinPaieRepository;
    private final ParametrePaieService parametrePaieService;
    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;
    private final AncienneteSettingService ancienneteSettingService;
    private final AncienneteCalculator ancienneteCalculator;
    private final CongeProvisionCongeRepository provisionCongeRepository;
    private Map<String, Double> detailsSalaire;
    private Map<String, Double> detailsPrimes;
    //private final MensualiteService mensualiteService;

    public TraitementSalaireService(CompanyService companyService, ContratEmployeService contratEmployeService, RubriqueService rubriqueService, ContratEmployeRubriqueService contratEmployeRubriqueService, HeureSupplementaireService heureSupplementaireService, MontantRubriqueService montantRubriqueService, TempsDeTravailService tempsDeTravailService, BulletinPaieService bulletinPaieService, EnfantEmployeService enfantEmployeService, TraitementSalaireLogRepository traitementSalaireLogRepository, MensualiteService mensualiteService, PrelevementMensualiteService prelevementMensualiteService, AcompteService acompteService, AvanceService avanceService, AllocationCongeService allocationCongeService, BulletinPaieRepository bulletinPaieRepository, ParametrePaieService parametrePaieService, EmployeRepository employeRepository, CompanyRepository companyRepository, AncienneteSettingService ancienneteSettingService, AncienneteCalculator ancienneteCalculator, CongeProvisionCongeRepository provisionCongeRepository) {
        this.companyService = companyService;
        this.contratEmployeService = contratEmployeService;
        this.rubriqueService = rubriqueService;
        this.contratEmployeRubriqueService = contratEmployeRubriqueService;
        this.heureSupplementaireService = heureSupplementaireService;
        this.montantRubriqueService = montantRubriqueService;
        this.tempsDeTravailService = tempsDeTravailService;
        this.bulletinPaieService = bulletinPaieService;
        this.enfantEmployeService = enfantEmployeService;
        this.traitementSalaireLogRepository = traitementSalaireLogRepository;
        this.mensualiteService = mensualiteService;
        this.prelevementMensualiteService = prelevementMensualiteService;
        this.acompteService = acompteService;
        this.avanceService = avanceService;
        this.allocationCongeService = allocationCongeService;
        this.bulletinPaieRepository = bulletinPaieRepository;
        this.parametrePaieService = parametrePaieService;
        this.employeRepository = employeRepository;
        this.companyRepository = companyRepository;
        this.ancienneteSettingService = ancienneteSettingService;
        this.ancienneteCalculator = ancienneteCalculator;
        this.provisionCongeRepository = provisionCongeRepository;
    }

    @Transactional
    public List<ApercuSalaireDTO> apercuAvant(Long companyId, String mois, Long departementId) {
        apercuSalaireDTOList.clear();
        // System.out.println("Aperçu avant calcul des salaires pour l'entreprise " + companyId + " pour le mois de " + mois);
        if (departementId != null) {
            System.out.println("Département spécifié : " + departementId);
        }

        // Récupération de l'entreprise
        Optional<Company> companyOpt = companyService.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new RuntimeException("Aucune entreprise trouvée");
        }

        Company company = companyOpt.get();

        boolean enabled = ancienneteSettingService.isAncienneteEnabledForCompany(companyId);

        // Récupération des contrats valides
       // List<ContractEmployeDTO> contrats = contratEmployeService.getAllContratEmployeEnCoursDeValiditeParEntreprise(company.getId());

        YearMonth yearMonth = YearMonth.parse(mois); // 2026-01
        LocalDate localDate = yearMonth.atDay(1);
        // Récupération des contrats valides
        List<ContractEmployeDTO> contrats = contratEmployeService.getAllContratEmployeEnCoursDeValiditeParEntrepriseMois(company.getId(),localDate);

        // Filtrer par département si nécessaire
        if (departementId != null) {
            contrats = contrats.stream()
                    .filter(c -> c.getDepartement() != null && departementId.equals(c.getDepartement().getId()))
                    .toList();
        }

        if (contrats.isEmpty()) {
            throw new RuntimeException("Aucun contrat valide trouvée");
        }

        // Liste des éléments du contrat (ex. rubriques fixes comme primes)
        List<ContratEmployeRubriqueDTO> elementContract = contratEmployeRubriqueService.getByCompanyId(company.getId());

        // Récupération des primes individuelles
        List<MontantRubriqueDTO> primes = montantRubriqueService.findByCompanyIdAndMoisRubrique(company.getId(), mois);

        // Récupération salaire 13e mois
        List<MontantRubriqueDTO> salaire13eMois = montantRubriqueService.findSalaire13eMoisByCompanyIdAndMoisRubrique(company.getId(), mois);

        // Récupération primesExceptionnelles
        List<MontantRubriqueDTO> primesExceptionnelles = montantRubriqueService.findPrimesExceptionnellesByCompanyIdAndMoisRubrique(company.getId(), mois);

        // Récupération salaire Moyen
        List<MontantRubriqueDTO> salaireMoyenSaisie = montantRubriqueService.findSalaireMoyenByCompanyIdAndMoisRubrique(company.getId(), mois);

        // Récupération des heures supplémentaires
        List<HeureSupplementaireDTO> heureSups = heureSupplementaireService.findByEntrepriseIdAndMois(company.getId(), mois);

        //Récupération des temps de travail
        List<TempsDeTravailDTO> tempsTravail = tempsDeTravailService.getByCompanyAndMois(company.getId(), mois);
        List<AllocationCongeDetailDTO> allocationCongeDetailDTOS = allocationCongeService.getByCompanyAndMoisCalculeSalaire(company.getId(), mois);

        // Initialisation de calcul
        double risqueProfessionnel = company.getRss().doubleValue() / 100.0;
        double companyVps = company.getVps();
        int nbrJourTravailCompany = company.getNbrJourTravail().intValue();

        ParametrePaieDTO parametrePaie = parametrePaieService.getUnique();
        double pretationFamiliale = parametrePaie.getPrestationsfamiliales();
        double pensionEmployeur = parametrePaie.getPensionsEntreprise();
        SalaryCalculator calculator = new SalaryCalculator(companyVps, risqueProfessionnel, pretationFamiliale, pensionEmployeur, nbrJourTravailCompany);

        // Traitement pour chaque contrat
            for (ContractEmployeDTO contrat : contrats) {
            Long employeId = contrat.getEmploye().getId();
            Long contratId = contrat.getId();
            double salaireDeBase = contrat.getSalaire_base();
            Double salaireBrutMoisPasse = bulletinPaieService.getSalaireBrutMoisPrecedent(contratId, mois);

            double salaireBrut = elementContract.stream()
                    .filter(ec -> ec.getContratEmployeId() != null && ec.getContratEmployeId().equals(contratId))
                    .mapToDouble(ec -> ec.getMontant().doubleValue())
                    .sum();

            Map<String, Double> detailsSalaire = elementContract.stream()
                    .filter(ec -> ec.getContratEmployeId() != null && ec.getContratEmployeId().equals(contratId))
                    .collect(Collectors.groupingBy(
                            ec -> ec.getLibelle(),
                            Collectors.summingDouble(ec -> {
                                double montant = ec.getMontant() != null ? ec.getMontant().doubleValue() : 0.0;
                                double montantAjouter = ec.getMontant_ajouter() != null ? ec.getMontant_ajouter().doubleValue() : 0.0;
                                return montant + montantAjouter;
                            })
                    ));

            double totalPrimes = primes.stream()
                    .filter(p -> p.getContratEmployeId() != null
                            && p.getContratEmployeId().equals(contratId)
                            && p.getDeletedAt() == null)
                    .mapToDouble(p -> p.getMontantRubrique().doubleValue())
                    .sum();

                Map<String, Double> detailsPrimes = primes.stream()
                        .filter(p -> p.getContratEmployeId() != null
                                && p.getContratEmployeId().equals(contratId)
                                && p.getDeletedAt() == null)
                        .collect(Collectors.groupingBy(
                                p -> p.getRubriqueName(),
                                Collectors.summingDouble(p -> p.getMontantRubrique().doubleValue())
                        ));


                double total13eMois = salaire13eMois.stream()
                    .filter(p -> p.getContratEmployeId() != null && p.getContratEmployeId().equals(contratId) && p.getDeletedAt() == null)
                    .mapToDouble(p -> p.getMontantRubrique().doubleValue())
                    .sum();

            double totalPrimesExp = primesExceptionnelles.stream()
                    .filter(p -> p.getContratEmployeId() != null && p.getContratEmployeId().equals(contratId) && p.getDeletedAt() == null)
                    .mapToDouble(p -> p.getMontantRubrique().doubleValue())
                    .sum();

            double totalHeuresSup = heureSups.stream()
                    .filter(h -> h.getContratEmployeId() != null && h.getContratEmployeId().equals(contratId))
                    .mapToDouble(h -> h.getMontant().doubleValue())
                    .sum();

            int totalTempsTravail = (int) tempsTravail.stream()
                    .filter(h -> h.getContratEmployeId() != null && h.getContratEmployeId().equals(contratId))
                    .mapToDouble(TempsDeTravailDTO::getNombreJour)
                    .sum();

            double allocation = allocationCongeDetailDTOS.stream()
                    .filter(al -> al.getContratEmployeId() != null && al.getContratEmployeId().equals(contratId))
                    .map(al -> al.getMontantTotal() != null ? al.getMontantTotal() : BigDecimal.ZERO)
                    .max(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO)
                    .doubleValue();

            // Calcul du salaire brut de base
            double salaireBrutBase = elementContract.stream()
                    .filter(ec -> ec.getContratEmployeId() != null && ec.getContratEmployeId().equals(contratId))
                    .mapToDouble(ec -> {
                        double montant = ec.getMontant() != null ? ec.getMontant().doubleValue() : 0.0;
                        double montantAjouter = ec.getMontant_ajouter() != null ? ec.getMontant_ajouter().doubleValue() : 0.0;
                        return montant + montantAjouter;
                    })
                    .sum();

            double salaireBasecontrat = elementContract.stream()
                    .filter(ec -> ec.getContratEmployeId() != null
                            && ec.getContratEmployeId().equals(contratId)
                            && ec.getLibelle() != null
                            && ec.getLibelle().trim().equalsIgnoreCase("SALAIRE DE BASE"))
                    .mapToDouble(ec -> {
                        double montant = ec.getMontant() != null ? ec.getMontant().doubleValue() : 0.0;
                        double montantAjouter = ec.getMontant_ajouter() != null ? ec.getMontant_ajouter().doubleValue() : 0.0;
                        return montant + montantAjouter;
                    })
                    .findFirst()
                    .orElse(0.0);

                double primesAnciennete = 0.0;
                double ancienneteEmploye = 0.0;

                if (enabled) {
                    ancienneteEmploye = ancienneteCalculator.calculerAnciennete(employeId, companyId);

                    // Calcul de la prime d'ancienneté selon les règles
                    if (ancienneteEmploye >= 3 && ancienneteEmploye < 5) {
                        // 3% après 3 ans de présence
                        primesAnciennete = salaireBasecontrat * 0.03;

                    } else if (ancienneteEmploye >= 5 && ancienneteEmploye < 7) {
                        // 5% après 5 ans de présence
                        primesAnciennete = salaireBasecontrat * 0.05;

                    } else if (ancienneteEmploye >= 7 && ancienneteEmploye <= 20) {
                        // 5% de base (acquis à 5 ans) + 1% par année supplémentaire de la 7ème à la 20ème année
                        double pourcentageBase = 0.05;
                        double anneesSupplementaires = ancienneteEmploye - 5; // Années au-delà de la 5ème
                        double pourcentageSupplementaire = anneesSupplementaires * 0.01;

                        double pourcentageTotal = pourcentageBase + pourcentageSupplementaire;
                        primesAnciennete = salaireBasecontrat * pourcentageTotal;

                    } else if (ancienneteEmploye > 20) {
                        // Maximum atteint à 20 ans : 5% + 15% (de la 7ème à la 20ème) = 20%
                        primesAnciennete = salaireBasecontrat * 0.20;
                    }
                    // Si ancienneteEmploye < 3, primesAnciennete reste à 0.0
                }

                double taxeTelevisuel = 3000;
                double taxeRadiophonique = 1000;

            SalaryCalculatorResult result;

            // Vérifier s'il y a un 13e mois ou des primes exceptionnelles
            if (total13eMois > 0 || totalPrimesExp > 0 && totalTempsTravail != 0) {
                ITSCalculator itsCalculator = new ITSCalculator();
                double salaireMoyen;

                // ✅ NOUVELLE LOGIQUE : Vérifier si un salaire moyen a été saisi pour ce contrat
                Optional<MontantRubriqueDTO> salaireMoyenSaisiOpt = salaireMoyenSaisie.stream()
                        .filter(sm -> sm.getContratEmployeId() != null && sm.getContratEmployeId().equals(contratId))
                        .findFirst();

                if (salaireMoyenSaisiOpt.isPresent()) {
                    // Utiliser le salaire moyen saisi manuellement
                    salaireMoyen = salaireMoyenSaisiOpt.get().getMontantRubrique().doubleValue();
                } else {
                    // Calculer le salaire moyen sur 12 mois
                    HistoriqueMontantsDTO historiqueMontantsDTO = montantRubriqueService.calculerSalaireMoyens12Mois(contratId, mois);
                    salaireMoyen = historiqueMontantsDTO.getMontantMoyen().doubleValue();
                }
                double salaireBrutDuMois = salaireBrutBase + totalPrimes + primesAnciennete;

                int itsDuMois = itsCalculator.itsOfMonth(salaireMoyen, salaireBrutDuMois, total13eMois + totalPrimesExp, Short.parseShort("3"));

                result = calculator.salaryCalculateWithHeuresSupAndWorkingDays13eMois(
                        salaireBrutBase,
                        totalPrimes+ primesAnciennete + totalPrimesExp + total13eMois,
                        totalHeuresSup,
                        totalTempsTravail != 0 ? totalTempsTravail : 30,
                        allocation,
                        itsDuMois
                );

                System.out.println("ITS: " +result.getIts());

            } else {
                result = calculator.salaryCalculateWithHeuresSupAndWorkingDays(
                        salaireBrutBase,
                        totalPrimes + primesAnciennete + totalPrimesExp + total13eMois,
                        totalHeuresSup,
                        totalTempsTravail != 0 ? totalTempsTravail : 30,
                        allocation
                );
            }

            // Construction de l'aperçu du salaire
            ApercuSalaireDTO apercuSalaireDTO = new ApercuSalaireDTO();
            apercuSalaireDTO.setContratEmployeId(contratId);
            apercuSalaireDTO.setNomPrenomEmploye(contrat.getEmploye().getNom() + " " + contrat.getEmploye().getPrenom());
            apercuSalaireDTO.setBank(contrat.getBanque().getName());
            apercuSalaireDTO.setNumeroCompte(contrat.getNumeroCompte());
            apercuSalaireDTO.setMois(mois);
            apercuSalaireDTO.setDepartement(contrat.getDepartement().getLibelle());
            apercuSalaireDTO.setSalaireBaseContrat(salaireBasecontrat);
            apercuSalaireDTO.setHeureSup(totalHeuresSup);
            apercuSalaireDTO.setSalaireBrut(result.getSalaireBrut());
            apercuSalaireDTO.setAutrePrime(totalPrimes);
            apercuSalaireDTO.setDetailsSalaire(detailsSalaire);
            apercuSalaireDTO.setDetailsPrimes(detailsPrimes);

            apercuSalaireDTO.setPrimesExceptionnelles(totalPrimesExp);
            apercuSalaireDTO.setTreiziemeMois(total13eMois);
            apercuSalaireDTO.setSalaireBrutMoisPasse(salaireBrutMoisPasse);
            apercuSalaireDTO.setAllocationConge(allocation);
            apercuSalaireDTO.setPrimeAnciennete(primesAnciennete);

            // Récupérez le mois (String) depuis votre DTO
            String moisAnnee = mois;
            String getMois = moisAnnee.substring(5); // "01", "03", "06", etc.

            if (getMois.equals("03")) {
                // Appliquer taxe radiophonique et ajuster le net
                double nouveauNet = result.getSalaireNet() - taxeRadiophonique;
                apercuSalaireDTO.setTaxeRadiophonique(taxeRadiophonique);
                apercuSalaireDTO.setNetAPayer(nouveauNet);

            } else if (getMois.equals("06")) {
                // Appliquer taxe télévisuelle et ajuster le net
                double nouveauNet = result.getSalaireNet() - taxeTelevisuel;
                apercuSalaireDTO.setTaxeTelevisuel(taxeTelevisuel);
                apercuSalaireDTO.setNetAPayer(nouveauNet);
            }else {
                apercuSalaireDTO.setNetAPayer(result.getSalaireNet());
            }

            apercuSalaireDTO.setMontantIpts(result.getIts());
            apercuSalaireDTO.setMontantCnss(result.getCnssSalariale());
            apercuSalaireDTO.setMontantTTRetenue(result.getChargesSalariales());
            apercuSalaireDTO.setMontantVps(result.getVpsPatronales());
            apercuSalaireDTO.setMontantCnssEmployeur(result.getCnssPatronales());
            apercuSalaireDTO.setTotalChargePatronale(result.getChargesPatronales());
            apercuSalaireDTO.setSalaireNet(result.getSalaireNet());
            apercuSalaireDTO.setMensualite(0);
            apercuSalaireDTO.setTotalRetenue(0);
            apercuSalaireDTO.setTempsTravail(totalTempsTravail == 0 ? 30 : totalTempsTravail);
            apercuSalaireDTOList.add(apercuSalaireDTO);
        }
        return apercuSalaireDTOList;
    }

    @Transactional
    public void calculerSalaires(Long companyId, String mois, Long departementId) throws IOException, WriterException {
        if (departementId != null) {
        }
        YearMonth yearMonth = YearMonth.parse(mois);
        // Récupération de l'entreprise
        Optional<Company> companyOpt = companyService.findById(companyId);
        if (companyOpt.isEmpty()) {
            return;
        }
        Company company = companyOpt.get();

        //verifie si anncienneté appliqué
        boolean enabled = ancienneteSettingService.isAncienneteEnabledForCompany(companyId);

        // Gestion du log existant
        if (traitementSalaireLogRepository.existsByCompanyIdAndMois(company.getId(), mois)) {
            traitementSalaireLogRepository.deleteByCompanyIdAndMois(company.getId(), mois);
        }
        // Création du log
        TraitementSalaireLog log = new TraitementSalaireLog();
        log.setStatut(Status.IN_PROCESS);
        log.setCompanyId(company.getId());
        log.setCompanyName(company.getName());
        log.setDateTraitement(LocalDateTime.now());
        log.setMois(mois);
        log = traitementSalaireLogRepository.save(log);

        // Récupération des contrats valides
       // List<ContractEmployeDTO> contrats = contratEmployeService.getAllContratEmployeEnCoursDeValiditeParEntreprise(company.getId());

        YearMonth yearMonth1 = YearMonth.parse(mois); // 2026-01
        LocalDate localDate = yearMonth1.atDay(1);
        // Récupération des contrats valides
        List<ContractEmployeDTO> contrats = contratEmployeService.getAllContratEmployeEnCoursDeValiditeParEntrepriseMois(company.getId(),localDate);

        // Filtrer par département si nécessaire
        if (departementId != null) {
            contrats = contrats.stream()
                    .filter(c -> c.getDepartement() != null && departementId.equals(c.getDepartement().getId()))
                    .toList();
        }
        if (contrats.isEmpty()) {
            return;
        }
        // Récupération de toutes les données nécessaires
        List<BulletinPaieDTO> bulletinPaieDTOS = bulletinPaieService.getByMoisAndCompany(DateUtils.getMoisPrecedent(mois), companyId);
        List<ContratEmployeRubriqueDTO> elementContract = contratEmployeRubriqueService.getByCompanyId(company.getId());
        List<MontantRubriqueDTO> primes = montantRubriqueService.findByCompanyIdAndMoisRubrique(company.getId(), mois);
        List<MontantRubriqueDTO> salaire13eMois = montantRubriqueService.findSalaire13eMoisByCompanyIdAndMoisRubrique(company.getId(), mois);
        List<MontantRubriqueDTO> primesExceptionnelles = montantRubriqueService.findPrimesExceptionnellesByCompanyIdAndMoisRubrique(company.getId(), mois);
        List<HeureSupplementaireDTO> heureSups = heureSupplementaireService.findByEntrepriseIdAndMois(company.getId(), mois);
        List<TempsDeTravailDTO> tempsTravail = tempsDeTravailService.getByCompanyAndMois(company.getId(), mois);
        List<MensualiteDTO> mensualiteDTOS = mensualiteService.loadMensualiteIfNotSoldee(company.getId(), mois);
        List<AcompteDTO> acompteDTOS = acompteService.findByCompanyAndMois(company.getId(), mois);
        List<AvanceDTO> avanceDTOS = avanceService.findByCompanyIdAndMois(company.getId(), yearMonth);
        List<AllocationCongeDetailDTO> allocationCongeDetailDTOS = allocationCongeService.getByCompanyAndMoisCalculeSalaire(company.getId(), mois);
        // Récupération salaire Moyen
        List<MontantRubriqueDTO> salaireMoyenSaisie = montantRubriqueService.findSalaireMoyenByCompanyIdAndMoisRubrique(company.getId(), mois);

        // Initialisation de calcul
        double risqueProfessionnel = company.getRss().doubleValue() / 100.0;
        double companyVps = company.getVps();
        int nbrJourTravailCompany = company.getNbrJourTravail().intValue();

        ParametrePaieDTO parametrePaie = parametrePaieService.getUnique();
        double pretationFamiliale = parametrePaie.getPrestationsfamiliales();
        double pensionEmployeur = parametrePaie.getPensionsEntreprise();
        SalaryCalculator calculator = new SalaryCalculator(companyVps, risqueProfessionnel, pretationFamiliale, pensionEmployeur, nbrJourTravailCompany);

        // Traitement pour chaque contrat
        for (ContractEmployeDTO contrat : contrats) {
            Long employeId = contrat.getEmploye().getId();
            Long contratId = contrat.getId();
            double salaireDeBase = contrat.getSalaire_brut();

            Map<String, Double> detailsSalaire = elementContract.stream()
                    .filter(ec -> ec.getContratEmployeId() != null && ec.getContratEmployeId().equals(contratId))
                    .collect(Collectors.groupingBy(
                            ec -> ec.getLibelle(),
                            Collectors.summingDouble(ec -> {
                                double montant = ec.getMontant() != null ? ec.getMontant().doubleValue() : 0.0;
                                double montantAjouter = ec.getMontant_ajouter() != null ? ec.getMontant_ajouter().doubleValue() : 0.0;
                                return montant + montantAjouter;
                            })
                    ));

            Double salaireBrutMoisPasse = bulletinPaieService.getSalaireBrutMoisPrecedent(contratId, mois);
            // Calcul du nombre d'enfants
            int ttEnfant = enfantEmployeService.countByEmployeId(employeId);

            // Calcul du salaire brut de base
            double salaireBrutBase = elementContract.stream()
                    .filter(ec -> ec.getContratEmployeId() != null && ec.getContratEmployeId().equals(contratId))
                    .mapToDouble(ec -> {
                        double montant = ec.getMontant() != null ? ec.getMontant().doubleValue() : 0.0;
                        double montantAjouter = ec.getMontant_ajouter() != null ? ec.getMontant_ajouter().doubleValue() : 0.0;
                        return montant + montantAjouter;
                    })
                    .sum();

            // Salaire brut précédent
            double salaireBrutPrecedent = bulletinPaieDTOS.stream()
                    .filter(bp -> bp.getContratEmployeId() != null && bp.getContratEmployeId().equals(contratId))
                    .map(bp -> bp.getSalaireBrut() != null ? bp.getSalaireBrut().doubleValue() : 0.0)
                    .findFirst()
                    .orElse(0.0);

            // Calcul des totaux pour ce contrat
            double totalPrimes = primes.stream()
                    .filter(p -> p.getContratEmployeId() != null && p.getContratEmployeId().equals(contratId) && p.getDeletedAt() == null)
                    .mapToDouble(p -> p.getMontantRubrique().doubleValue())
                    .sum();

            double total13eMois = salaire13eMois.stream()
                    .filter(p -> p.getContratEmployeId() != null && p.getContratEmployeId().equals(contratId) && p.getDeletedAt() == null)
                    .mapToDouble(p -> p.getMontantRubrique().doubleValue())
                    .sum();

            double totalPrimesExp = primesExceptionnelles.stream()
                    .filter(p -> p.getContratEmployeId() != null && p.getContratEmployeId().equals(contratId) && p.getDeletedAt() == null)
                    .mapToDouble(p -> p.getMontantRubrique().doubleValue())
                    .sum();

            double totalHeuresSup = heureSups.stream()
                    .filter(h -> h.getContratEmployeId() != null && h.getContratEmployeId().equals(contratId))
                    .mapToDouble(h -> h.getMontant().doubleValue())
                    .sum();

            int totalTempsTravail = (int) tempsTravail.stream()
                    .filter(h -> h.getContratEmployeId() != null && h.getContratEmployeId().equals(contratId))
                    .mapToDouble(TempsDeTravailDTO::getNombreJour)
                    .sum();

            double totalMensualite = mensualiteDTOS.stream()
                    .filter(h -> h.getContratEmployeId() != null && h.getContratEmployeId().equals(contratId))
                    .mapToDouble(h -> h.getMontantMensuel() != null ? h.getMontantMensuel().doubleValue() : 0.0)
                    .sum();

            MensualiteDTO mensualite = mensualiteDTOS.stream()
                    .filter(h -> h.getContratEmployeId() != null && h.getContratEmployeId().equals(contratId))
                    .max(Comparator.comparing(h -> h.getMontantMensuel() != null ? h.getMontantMensuel() : BigDecimal.ZERO))
                    .orElse(null);

            double totalAcompte = acompteDTOS.stream()
                    .filter(ac -> ac.getContratEmployeId() != null && ac.getContratEmployeId().equals(contratId))
                    .map(ac -> ac.getMontant() != null ? ac.getMontant() : BigDecimal.ZERO)
                    .max(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO)
                    .doubleValue();

            double totalAvance = avanceDTOS.stream()
                    .filter(av -> av.getContratEmployeId() != null && av.getContratEmployeId().equals(contratId))
                    .map(av -> av.getMontantMensuel() != null ? av.getMontantMensuel() : BigDecimal.ZERO)
                    .max(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO)
                    .doubleValue();

            double allocation = allocationCongeDetailDTOS.stream()
                    .filter(al -> al.getContratEmployeId() != null && al.getContratEmployeId().equals(contratId))
                    .map(al -> al.getMontantTotal() != null ? al.getMontantTotal() : BigDecimal.ZERO)
                    .max(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO)
                    .doubleValue();
            double salaireBasecontrat = elementContract.stream()
                    .filter(ec -> ec.getContratEmployeId() != null
                            && ec.getContratEmployeId().equals(contratId)
                            && ec.getLibelle() != null
                            && ec.getLibelle().trim().equalsIgnoreCase("SALAIRE DE BASE"))
                    .mapToDouble(ec -> {
                        double montant = ec.getMontant() != null ? ec.getMontant().doubleValue() : 0.0;
                        double montantAjouter = ec.getMontant_ajouter() != null ? ec.getMontant_ajouter().doubleValue() : 0.0;
                        return montant + montantAjouter;
                    })
                    .findFirst()
                    .orElse(0.0);

            double primesAnciennete = 0.0;
            double ancienneteEmploye = 0.0;

            if (enabled) {
                ancienneteEmploye = ancienneteCalculator.calculerAnciennete(employeId, companyId);
                // Calcul de la prime d'ancienneté selon les règles
                if (ancienneteEmploye >= 3 && ancienneteEmploye < 5) {
                    // 3% après 3 ans de présence
                    primesAnciennete = salaireBasecontrat * 0.03;
                } else if (ancienneteEmploye >= 5 && ancienneteEmploye < 7) {
                    // 5% après 5 ans de présence
                    primesAnciennete = salaireBasecontrat * 0.05;
                } else if (ancienneteEmploye >= 7 && ancienneteEmploye <= 20) {
                    // 5% de base (acquis à 5 ans) + 1% par année supplémentaire de la 7ème à la 20ème année
                    double pourcentageBase = 0.05;
                    double anneesSupplementaires = ancienneteEmploye - 5; // Années au-delà de la 5ème
                    double pourcentageSupplementaire = anneesSupplementaires * 0.01;
                    double pourcentageTotal = pourcentageBase + pourcentageSupplementaire;
                    primesAnciennete = salaireBasecontrat * pourcentageTotal;

                } else if (ancienneteEmploye > 20) {
                    // Maximum atteint à 20 ans : 5% + 15% (de la 7ème à la 20ème) = 20%
                    primesAnciennete = salaireBasecontrat * 0.20;
                }
                // Si ancienneteEmploye < 3, primesAnciennete reste à 0.0
            }

            double taxeTelevisuel = 3000;
            double taxeRadiophonique = 1000;

            // Calcul du salaire
            SalaryCalculatorResult result;
            // Vérifier s'il y a un 13e mois ou des primes exceptionnelles
            if (total13eMois > 0 || totalPrimesExp > 0 && totalTempsTravail != 0) {
                ITSCalculator itsCalculator = new ITSCalculator();
                double salaireMoyen;
                // ✅ NOUVELLE LOGIQUE : Vérifier si un salaire moyen a été saisi pour ce contrat
                Optional<MontantRubriqueDTO> salaireMoyenSaisiOpt = salaireMoyenSaisie.stream()
                        .filter(sm -> sm.getContratEmployeId() != null && sm.getContratEmployeId().equals(contratId))
                        .findFirst();

                if (salaireMoyenSaisiOpt.isPresent()) {
                    // Utiliser le salaire moyen saisi manuellement
                    salaireMoyen = salaireMoyenSaisiOpt.get().getMontantRubrique().doubleValue();
                } else {
                    // Calculer le salaire moyen sur 12 mois
                    HistoriqueMontantsDTO historiqueMontantsDTO = montantRubriqueService.calculerSalaireMoyens12Mois(contratId, mois);
                    salaireMoyen = historiqueMontantsDTO.getMontantMoyen().doubleValue();
                }

                double salaireBrutDuMois = salaireBrutBase + totalPrimes + primesAnciennete;
                int itsDuMois = itsCalculator.itsOfMonth(salaireMoyen, salaireBrutDuMois, total13eMois + totalPrimesExp, Short.parseShort("3"));
                result = calculator.salaryCalculateWithHeuresSupAndWorkingDays13eMois(
                        salaireBrutBase,
                        totalPrimes+ primesAnciennete + totalPrimesExp + total13eMois,
                        totalHeuresSup,
                        totalTempsTravail != 0 ? totalTempsTravail : 30,
                        allocation,
                        itsDuMois
                );

                System.out.println("ITS: " +result.getIts());

            } else {
                result = calculator.salaryCalculateWithHeuresSupAndWorkingDays(
                        salaireBrutBase,
                        totalPrimes + primesAnciennete + totalPrimesExp + total13eMois,
                        totalHeuresSup,
                        totalTempsTravail != 0 ? totalTempsTravail : 30,
                        allocation
                );
            }

            double totalRetenues = totalMensualite + totalAvance + totalAcompte;
            double netApayer = result.getSalaireNet() - totalRetenues;

            // Gestion du prélèvement mensualité
            if (mensualite != null) {
                PrelevementMensualite prelevement_ = prelevementMensualiteService.findByMoisPrelevementAndContratEmployeIdAndCompanyIdAndInstitutionId(mois, companyId, mensualite.getInstitutionId(), contratId);
                PrelevementMensualite prelevement = new PrelevementMensualite();
                if (prelevement_ != null) {
                    prelevement = prelevement_;
                }
                prelevement.setCompany(company);
                prelevement.setContratEmploye(contrat.toEntity());
                prelevement.setEmploye(contrat.getEmploye());
                Mensualite mensualiteRef = new Mensualite();
                mensualiteRef.setId(mensualite.getId());
                prelevement.setMensualite(mensualiteRef);
                prelevement.setInstitution(mensualite.getInstitution());
                prelevement.setMontant(BigDecimal.valueOf(totalMensualite));
                prelevement.setMoisPrelevement(mois);
                prelevementMensualiteService.save(prelevement);
            }

            // Création et sauvegarde du bulletin de paie
            BulletinPaieDTO bulletinPaie = new BulletinPaieDTO();
            bulletinPaie.setContratEmployeId(contratId);
            bulletinPaie.setEmployeId(employeId);
            bulletinPaie.setCompanyId(company.getId());
            bulletinPaie.setNombreEnfant(ttEnfant);
            bulletinPaie.setMois(mois);
            bulletinPaie.setMontantCnss(BigDecimal.valueOf(result.getCnssSalariale()));
            bulletinPaie.setMontantCnssEmployeur(BigDecimal.valueOf(result.getCnssPatronales()));
            bulletinPaie.setMontantIpts(BigDecimal.valueOf(result.getIts()));
            bulletinPaie.setMontantVps(BigDecimal.valueOf(result.getVpsPatronales()));
            bulletinPaie.setMontantAib(BigDecimal.valueOf(0.0));
            bulletinPaie.setSoldeConge(0.0);
            bulletinPaie.setCongePris(0.0);
            bulletinPaie.setNetAPayer(BigDecimal.valueOf(netApayer));
            bulletinPaie.setAutreAvantage(BigDecimal.valueOf(totalPrimes));
            bulletinPaie.setAutreRetenue(BigDecimal.valueOf(0.0));
            bulletinPaie.setSalaireBrutArrondi(BigDecimal.valueOf(result.getSalaireBrut()));
            bulletinPaie.setSalaireBrut(BigDecimal.valueOf(result.getSalaireBrut()));


            bulletinPaie.setPrimesExceptionnelles(totalPrimesExp);
            bulletinPaie.setTreiziemeMois(total13eMois);
            bulletinPaie.setSalaireBrutMoisPasse(salaireBrutMoisPasse);
            bulletinPaie.setAllocationConge(allocation);
            bulletinPaie.setPrimeAnciennete(primesAnciennete);
            bulletinPaie.setDepartement(contrat.getDepartement().getLibelle());
            bulletinPaie.setTempsTravail(totalTempsTravail != 0 ? totalTempsTravail : 30);

           //Taxes Radiophonique et Télévisuel
            String moisAnnee = mois;
            String getMois = moisAnnee.substring(5); // "01", "03", "06", etc.

            if (getMois.equals("03")) {
                bulletinPaie.setTaxeRadiophonique(taxeRadiophonique);
                // Convertir en BigDecimal pour une précision exacte
                BigDecimal salaireNetDecimal = BigDecimal.valueOf(result.getSalaireNet());
                BigDecimal taxeDecimal = new BigDecimal(String.valueOf(taxeRadiophonique));
                BigDecimal nouveauSalaireNet = salaireNetDecimal.subtract(taxeDecimal);
                bulletinPaie.setSalaireNet(nouveauSalaireNet);
            } else if (getMois.equals("06")) {
                bulletinPaie.setTaxeTelevisuel(taxeTelevisuel);
                BigDecimal salaireNetDecimal = BigDecimal.valueOf(result.getSalaireNet());
                BigDecimal taxeDecimal = new BigDecimal(String.valueOf(taxeTelevisuel));
                BigDecimal nouveauSalaireNet = salaireNetDecimal.subtract(taxeDecimal);
                bulletinPaie.setSalaireNet(nouveauSalaireNet);
            }else {
                bulletinPaie.setSalaireNet(BigDecimal.valueOf(result.getSalaireNet()));
            }

            bulletinPaie.setTempsTravail(result.getJoursTravailles());
            bulletinPaie.setTotalChargePatronale(BigDecimal.valueOf(result.getChargesPatronales()));
            bulletinPaie.setTotalRetenue(BigDecimal.valueOf(result.getChargesSalariales()));
            bulletinPaie.setDateCalculSalaire(LocalDate.now());
            bulletinPaie.setNumeroCompteEmploye(contrat.getNumeroCompte());
            bulletinPaie.setBanqueId(contrat.getBanqueId());
            bulletinPaie.setSignataire("Signature");
            bulletinPaie.setSalaireBrutMoisPasse(salaireBrutPrecedent);
            bulletinPaie.setStatut(Status.PENDING);

            // Préparation des données pour la description JSON
            Map<String, Object> primesData = new LinkedHashMap<>();
            List<MontantRubriqueDTO> listePrimes = primes.stream()
                    .filter(p -> p.getContratEmployeId() != null && p.getContratEmployeId().equals(contratId))
                    .toList();

            for (MontantRubriqueDTO prime : listePrimes) {
                primesData.put(prime.getRubriqueName(), prime.getMontantRubrique());
            }

            Map<String, Object> descriptionData = new LinkedHashMap<>();

            // Identification de l'employé
            descriptionData.put("employeId", employeId);
            descriptionData.put("nomPrenomEmploye", contrat.getEmploye().getNom() + " " + contrat.getEmploye().getPrenom());
            descriptionData.put("matriculeEmploye", contrat.getEmploye().getMatricule());

            descriptionData.put("tempsTravail", totalTempsTravail != 0 ? totalTempsTravail : 30);
            descriptionData.put("departement", contrat.getDepartement().getLibelle());
            if (getMois.equals("03")) {
                descriptionData.put("taxeRadiophonique", taxeRadiophonique);
            } else if (getMois.equals("06")) {
                descriptionData.put("TaxeTelevisieul", taxeTelevisuel);
            }
            // Informations professionnelles
            descriptionData.put("fonction", contrat.getPoste().getLibelle());
            descriptionData.put("numeroCnss", employeId);
            descriptionData.put("dateDebut", contrat.getDateDebut() != null ? contrat.getDateDebut().toString() : "");
            descriptionData.put("dateFin", contrat.getDateFin() != null ? contrat.getDateFin().toString() : "");
            descriptionData.put("typeContrat", contrat.getTypeContrat());
            descriptionData.put("natureContrat", contrat.getNatureContrat().getLibelle());
            descriptionData.put("modePaiement", contrat.getModeDePaiement().getLibelle());


            Company entreprise = companyRepository.findById(contrat.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

            descriptionData.put("signataire", entreprise.getSignatoryName());
            descriptionData.put("numeroCnssEmployeur", entreprise.getNss());
            descriptionData.put("adresse", entreprise.getAddress());
            descriptionData.put("telephone", entreprise.getPhone());
            descriptionData.put("rccm", entreprise.getRccm());
            descriptionData.put("mail", entreprise.getEmail());
            descriptionData.put("site", entreprise.getWebSite());

            // QR Code
            String qrContent = "Bulletin employé: " + contrat.getEmploye().getNom() + " " + contrat.getEmploye().getPrenom() +
                    " | Mois: " + bulletinPaie.getMois() +
                    " | Net à payer: " + netApayer;
            String qrCodeBase64 = GenerateBulletin.generateQRCodeBase64(qrContent, 120, 120);
            descriptionData.put("qrCodeImg", qrCodeBase64);

            // Logo de l'entreprise
            String logoPath = entreprise.getLogo();
            String logoDataUri = null;
            if (logoPath != null && !logoPath.isEmpty()) {
                Path fullLogoPath = Paths.get(System.getProperty("user.dir") + logoPath);
                if (Files.exists(fullLogoPath)) {
                    byte[] imageBytes = Files.readAllBytes(fullLogoPath);
                    String base64Logo = Base64.getEncoder().encodeToString(imageBytes);
                    logoDataUri = "data:image/png;base64," + base64Logo;
                }
            }
            descriptionData.put("logoEntreprise", logoDataUri);

            // Informations complémentaires
            descriptionData.put("contratEmployeId", contratId);
            descriptionData.put("numeroCompte", contrat.getNumeroCompte());
            descriptionData.put("numeroCompteEmploye", contrat.getNumeroCompte());
            descriptionData.put("banqueId", contrat.getBanqueId());
            descriptionData.put("companyId", company.getId());
            descriptionData.put("mois", mois);
            descriptionData.put("dateCalculSalaire", LocalDate.now().toString());
            descriptionData.put("nombreEnfant", ttEnfant);
            descriptionData.put("soldeConge", 0.0);
            descriptionData.put("congePris", 0.0);
            descriptionData.put("heuresSup", totalHeuresSup);
            descriptionData.put("salaireBase", salaireBrutBase);
            descriptionData.put("primes", primesData);
            descriptionData.put("primesExceptionnelles", totalPrimesExp);
            descriptionData.put("primes13eMois", total13eMois);
            descriptionData.put("primesAnciennete", primesAnciennete);
            descriptionData.put("autreAvantage", totalPrimes);

            descriptionData.put("detailsSalaire", detailsSalaire);

            descriptionData.put("salaireButPrecedant", salaireBrutPrecedent);
            descriptionData.put("salaireBrut", result.getSalaireBrut());

            descriptionData.put("salaireBrutArrondi", result.getSalaireBrut());
            descriptionData.put("montantCnss", result.getCnssSalariale());
            descriptionData.put("montantIpts", result.getIts());
            descriptionData.put("totalChargeSalarialRetenue", result.getChargesSalariales());
            descriptionData.put("totalRetenueNet", totalRetenues);
            descriptionData.put("totalRetenue", result.getChargesSalariales());

            descriptionData.put("mensualite", totalMensualite);
            descriptionData.put("avance", totalAvance);
            descriptionData.put("acompte", totalAcompte);
            descriptionData.put("montantCnssEmployeur", result.getCnssPatronales());
            descriptionData.put("montantVps", result.getVpsPatronales());
            descriptionData.put("montantAib", 0.0);
            descriptionData.put("totalChargePatronale", result.getChargesPatronales());
            descriptionData.put("salaireNet", result.getSalaireNet());
            descriptionData.put("netAPayer", netApayer);
            // Conversion en JSON
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonDescription = mapper.writeValueAsString(descriptionData);
                bulletinPaie.setDescription(jsonDescription);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                bulletinPaie.setDescription("Erreur de génération JSON");
            }

            bulletinPaieService.save(bulletinPaie);
        }

        // Finalisation du log
        log.setStatut(Status.PENDING);
        log.setDateTraitement(LocalDateTime.now());
        traitementSalaireLogRepository.save(log);
    }

    @Transactional
    public List<BulletinPaieDTO> apercuApres(Long companyId, String mois, Long departementId) {

        System.out.println("Aperçu après calcul des salaires pour l'entreprise " + companyId + " pour le mois de " + mois);
        if (departementId != null) {
            System.out.println("Département spécifié : " + departementId);
        }

        // Récupération de l'entreprise
        Optional<Company> companyOpt = companyService.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new RuntimeException("Aucune entreprise trouvée");
        }

        Company company = companyOpt.get();
        List<BulletinPaieDTO> bpDTO;
        bpDTO = bulletinPaieService.getByMoisAndCompany(mois, company.getId());
        //return bulletinPaieService.getByMoisAndCompany(mois,company.getId());
        return bpDTO;
    }

    public List<TraitementSalaireLogDTO> getLogByCompanyId(Long companyId) {
        return traitementSalaireLogRepository.findByCompanyIdOrderByDateTraitementDesc(companyId).stream().map(TraitementSalaireLogDTO::toDTO).collect(Collectors.toList());
    }

//    @Transactional
//    public TraitementSalaireLog vadationTraitement(Long logId, Long validateurId, Long companyId, String mois) {
//        System.out.println("Validation traitement salaire " + companyId + " pour le mois de " + mois);
//
//        // Récupération de l'entreprise
//        Optional<Company> companyOpt = companyService.findById(companyId);
//        if (companyOpt.isEmpty()) {
//            throw new RuntimeException("Aucune entreprise trouvée");
//        }
//        Company company = companyOpt.get();
//
//        List<BulletinPaieDTO> bulletinPaieList = bulletinPaieService.getByMoisAndCompanyAndStatus(mois, companyId, Status.PENDING);
//        if (bulletinPaieList.isEmpty()) {
//            throw new RuntimeException("Aucun traitement trouver");
//        }
//
//        for (BulletinPaieDTO bp : bulletinPaieList) {
//            bp.setContratEmployeId(bp.getContratEmployeId());
//            bp.setStatut(Status.VALIDATED);
//            bulletinPaieService.save(bp);
//        }
//
//        this.validerTraitement(logId, validateurId);
//        return null;
//    }
//
//

    @Transactional
    public TraitementSalaireLog vadationTraitement(Long logId, Long validateurId, Long companyId, String mois) {

        System.out.println("Validation traitement salaire " + companyId + " pour le mois de " + mois);

        // Récupération de l'entreprise
        Optional<Company> companyOpt = companyService.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new RuntimeException("Aucune entreprise trouvée");
        }
        Company company = companyOpt.get();

        List<BulletinPaieDTO> bulletinPaieList = bulletinPaieService.getByMoisAndCompanyAndStatus(mois, companyId, Status.PENDING);
        if (bulletinPaieList.isEmpty()) {
            throw new RuntimeException("Aucun traitement trouver");
        }

        // 🆕 GÉNÉRATION DES PROVISIONS POUR CHAQUE BULLETIN
        for (BulletinPaieDTO bp : bulletinPaieList) {

            // 1. Mise à jour du statut du bulletin
            bp.setContratEmployeId(bp.getContratEmployeId());
            bp.setStatut(Status.VALIDATED);
            bulletinPaieService.save(bp);

            // 2. 🆕 GÉNÉRATION/MISE À JOUR DE LA PROVISION DE CONGÉS
            try {
                genererProvisionFromBulletin(bp, mois, company);

            } catch (Exception e) {
                // Log l'erreur mais ne bloque pas la validation de la paie
                System.err.println("Erreur génération provision pour employe " +
                        bp.getEmployeId() + ": " + e.getMessage());
                // Ou throw si tu veux bloquer: throw new RuntimeException("Provision non générée", e);
            }
        }

        this.validerTraitement(logId, validateurId);
        return null;
    }

    /**
     * Génère ou met à jour la provision de congés à partir d'un bulletin de paie
     */
    private void genererProvisionFromBulletin(BulletinPaieDTO bp, String mois, Company company) {

        // Récupérer l'employé depuis le bulletin
        Long employeId = bp.getEmployeId();

        // Vérifier si provision existe déjà
        Optional<ProvisionConge> existante = provisionCongeRepository
                .findByEmployeIdAndMoisReference(employeId, mois);

        // Données de la paie
        BigDecimal salaireBrut = bp.getSalaireBrut(); // ou bp.getTotalBrut() selon ton DTO

        double tempsTravail = bp.getTempsTravail();  // Toujours une valeur, jamais null
        BigDecimal joursTravailles = BigDecimal.valueOf(
                tempsTravail > 0 ? tempsTravail : company.getNbrJourTravail()
        );

        // Calcul jours acquis (2.0 ou 2.5 selon config entreprise)
        BigDecimal joursAcquis = calculerJoursAcquis(company, joursTravailles);

        // Calcul provision
        BigDecimal provisionMensuelle = salaireBrut
                .divide(joursTravailles, 4, RoundingMode.HALF_UP)
                .multiply(joursAcquis)
                .setScale(2, RoundingMode.HALF_UP);

        if (existante.isPresent()) {
            // Mise à jour si pas encore consommée
            ProvisionConge provision = existante.get();

            if (provision.getJoursConsommes().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Provision déjà consommée pour " + employeId + "/" + mois + " - ignorée");
                return;
            }

            provision.setSalaireBrutMois(salaireBrut);
            provision.setJoursTravaillesMois(joursTravailles);
            provision.setJoursAcquis(joursAcquis);
            provision.setProvisionMensuelle(provisionMensuelle);

            provisionCongeRepository.save(provision);
            System.out.println("Provision mise à jour: " + employeId + " / " + mois + " = " + provisionMensuelle);

        } else {
            // Création nouvelle provision
            Employe employe = employeRepository.findById(employeId)
                    .orElseThrow(() -> new RuntimeException("Employé non trouvé: " + employeId));

            ProvisionConge provision = new ProvisionConge();
            provision.setEmploye(employe);
            provision.setCompany(company);
            provision.setMoisReference(mois);
            provision.setJoursAcquis(joursAcquis);
            provision.setSalaireBrutMois(salaireBrut);
            provision.setJoursTravaillesMois(joursTravailles);
            provision.setProvisionMensuelle(provisionMensuelle);

            provisionCongeRepository.save(provision);
            System.out.println("Provision créée: " + employeId + " / " + mois + " = " + provisionMensuelle);
        }
    }

    /**
     * Calcule les jours acquis (2.0 ou 2.5) avec prorata si mois incomplet
     */
    private BigDecimal calculerJoursAcquis(Company company, BigDecimal joursTravailles) {
        Double nbrJourConge = company.getNbrJourConge();  // 2.0 ou 2.5

        if (nbrJourConge == null) {
            throw new RuntimeException("Configuration congés incomplète pour l'entreprise");
        }

        // Tout le monde a 2.5 jours (ou 2.0) par mois, point final
        return BigDecimal.valueOf(nbrJourConge);
    }

    @Transactional
    public TraitementSalaireLog restaurerTraitement(Long logId, Long validateurId, Long companyId, String mois) {
        // Récupération de l'entreprise
        Optional<Company> companyOpt = companyService.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new RuntimeException("Aucune entreprise trouvée");
        }
        Company company = companyOpt.get();

        List<BulletinPaieDTO> bulletinPaieList = bulletinPaieService.getByMoisAndCompanyAndStatus(mois, companyId, Status.VALIDATED);
        if (bulletinPaieList.isEmpty()) {
            throw new RuntimeException("Aucun bulletin trouver");
        }

        for (BulletinPaieDTO bp : bulletinPaieList) {
            System.out.println(bp.getContratEmployeId());
            bp.setContratEmployeId(bp.getContratEmployeId());
            bp.setStatut(Status.PENDING);
            bulletinPaieService.save(bp);
        }

        Optional<TraitementSalaireLog> tsl = traitementSalaireLogRepository.findById(logId);
        if (tsl.isEmpty()) {
            throw new RuntimeException("Aucun traitement trouver");
        }
        TraitementSalaireLog log = tsl.get();
        log.setStatut(Status.PENDING);
        log.setVerificateurId(null);
        log.setDateVerification(null);
        log.setValidateurId(null);
        traitementSalaireLogRepository.save(log);
        return null;
    }

    public TraitementSalaireLog enregistrerTraitementInitial(TraitementSalaireLog log) {
        log.setStatut(Status.PENDING);
        log.setDateTraitement(LocalDateTime.now());
        return traitementSalaireLogRepository.save(log);
    }

    public TraitementSalaireLog verifierTraitement(Long logId, Long verificateurId) {
        TraitementSalaireLog log = findOrThrow(logId);
        log.setStatut(Status.PENDING);
        log.setVerificateurId(verificateurId);
        log.setDateVerification(LocalDateTime.now());
        return traitementSalaireLogRepository.save(log);
    }

    public TraitementSalaireLog validerTraitement(Long logId, Long validateurId) {
        TraitementSalaireLog log = findOrThrow(logId);
        log.setStatut(Status.VALIDATED);
        log.setValidateurId(validateurId);
        log.setDateValidation(LocalDateTime.now());
        return traitementSalaireLogRepository.save(log);
    }

    public TraitementSalaireLog rejeterTraitement(Long logId, Long rejetParId, String motif) {
        TraitementSalaireLog log = findOrThrow(logId);
        log.setStatut(Status.REJECTED);
        log.setRejetParId(rejetParId);
        log.setDateRejet(LocalDateTime.now());
        log.setMotifRejet(motif);
        return traitementSalaireLogRepository.save(log);
    }

    private TraitementSalaireLog findOrThrow(Long id) {
        return traitementSalaireLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Traitement non trouvé"));
    }

    @Transactional
    public BulletinPaieGenerateDTO getBulletin(Long employeId, String mois, Long companyId) {
        Optional<BulletinPaie> optionalBulletin = bulletinPaieRepository
                .findByEmployeIdAndMoisAndCompanyId(employeId, mois, companyId);

        if (optionalBulletin.isEmpty()) {
            return null; // le controller gère le notFound
        }

        BulletinPaie bulletin = optionalBulletin.get();

        // 🔹 Conversion JSON -> DTO
        ObjectMapper mapper = new ObjectMapper();
        BulletinPaieGenerateDTO dto;
        try {
            dto = mapper.readValue(bulletin.getDescription(), BulletinPaieGenerateDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            dto = new BulletinPaieGenerateDTO();
        }

        // 🔹 Récupération banque et entreprise
        Banque banque = bulletin.getDomiciliationBancaireEmploye();
        Company company = bulletin.getCompany();

        // 🔹 Remplissage des nouveaux champs
        dto.setNomBanque(banque != null ? banque.getName() : "");
        dto.setNomEntreprise(company != null ? company.getName() : "");
        dto.setLogoEntreprise(company != null ? company.getLogo() : "");

        return dto;
    }


    @Transactional
    public List<BulletinPaieGenerateDTO> getBulletinsByCompanyAndPeriode(Long companyId,
                                                                         String moisDebut,
                                                                         String moisFin) {
        // 1️⃣ Récupérer tous les bulletins de l'entreprise pour la période
        List<BulletinPaie> bulletins = bulletinPaieRepository
                .findByCompanyIdAndMoisBetweenPourBulletin(companyId, moisDebut, moisFin);

        List<BulletinPaieGenerateDTO> dtos = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (BulletinPaie bulletin : bulletins) {
            BulletinPaieGenerateDTO dto;

            // 2️⃣ Conversion JSON -> DTO depuis description
            try {
                dto = mapper.readValue(bulletin.getDescription(), BulletinPaieGenerateDTO.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                dto = new BulletinPaieGenerateDTO();
            }

            // 3️⃣ Remplir les infos complémentaires
            Banque banque = bulletin.getDomiciliationBancaireEmploye();
            Company company = bulletin.getCompany();

            dto.setNomBanque(banque != null ? banque.getName() : "");
            dto.setNomEntreprise(company != null ? company.getName() : "");
            dto.setLogoEntreprise(company != null ? company.getLogo() : "");
            dto.setMois(bulletin.getMois());
            dto.setPrimeAnciennete(bulletin.getPrimeAnciennete());
            // 🔹 Ajouter à la liste finale
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    public List<BulletinPaieGenerateDTO> getBulletinsByEmployeAndPeriode(
            Long employeId, Long companyId, String moisDebut, String moisFin) {

        List<BulletinPaie> bulletins = bulletinPaieRepository
                .findByEmployeIdAndCompanyIdAndMoisBetween(employeId, companyId, moisDebut, moisFin);

        ObjectMapper mapper = new ObjectMapper();
        List<BulletinPaieGenerateDTO> dtos = new ArrayList<>();

        for (BulletinPaie b : bulletins) {
            try {
                BulletinPaieGenerateDTO dto = mapper.readValue(b.getDescription(), BulletinPaieGenerateDTO.class);
                // Ajouter les champs banque/entreprise
                dto.setNomBanque(b.getDomiciliationBancaireEmploye() != null ?
                        b.getDomiciliationBancaireEmploye().getName() : "");
                dto.setNomEntreprise(b.getCompany() != null ? b.getCompany().getName() : "");
                dto.setLogoEntreprise(b.getCompany() != null ? b.getCompany().getLogo() : "");
                dtos.add(dto);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return dtos;
    }

    @Transactional
    public List<BulletinPaieGenerateDTO> getBulletinsByDepartementAndPeriode(Long departementId, Long companyId,
                                                                             String moisDebut, String moisFin) {
        List<BulletinPaieGenerateDTO> bulletinsDTO = new ArrayList<>();

        List<BulletinPaie> bulletins = bulletinPaieRepository
                .findByDepartementAndPeriode(departementId, companyId, moisDebut, moisFin);

        ObjectMapper mapper = new ObjectMapper();

        for (BulletinPaie bulletin : bulletins) {
            BulletinPaieGenerateDTO dto;
            try {
                dto = mapper.readValue(bulletin.getDescription(), BulletinPaieGenerateDTO.class);
            } catch (JsonProcessingException e) {
                dto = new BulletinPaieGenerateDTO();
            }

            dto.setNomBanque(bulletin.getDomiciliationBancaireEmploye() != null ?
                    bulletin.getDomiciliationBancaireEmploye().getName() : "");
            dto.setNomEntreprise(bulletin.getCompany() != null ?
                    bulletin.getCompany().getName() : "");
            dto.setLogoEntreprise(bulletin.getCompany() != null ?
                    bulletin.getCompany().getLogo() : "");

            bulletinsDTO.add(dto);
        }

        return bulletinsDTO;
    }


    private String imageToBase64(String filePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
