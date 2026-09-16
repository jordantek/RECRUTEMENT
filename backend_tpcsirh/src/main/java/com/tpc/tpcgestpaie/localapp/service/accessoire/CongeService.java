package com.tpc.tpcgestpaie.localapp.service.accessoire;

import com.tpc.tpcgestpaie.localapp.dto.accessoire.*;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.MontantRubriqueDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.SoldeCongeResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.Absence;
import com.tpc.tpcgestpaie.localapp.model.BulletinPaie;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeService;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.administration.AbsenceService;
import com.tpc.tpcgestpaie.localapp.service.paie.MontantRubriqueService;
import com.tpc.tpcgestpaie.localapp.service.paie.SoldeCongeCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.YearMonth;

@Service
public class CongeService {

    private final BulletinPaieRepository bulletinPaieRepository;
    private final ContratEmployeService contratEmployeService;
    private final AbsenceService absenceService;
    private final SoldeCongeCalculator soldeCongeCalculator;
    private final EmployeService employeService;
    private final ContratEmployeRepository   contratEmployeRepository;
    private final MontantRubriqueService montantRubriqueService;

    public CongeService(BulletinPaieRepository bulletinPaieRepository,
                        ContratEmployeService contratEmployeService,
                        AbsenceService absenceService,
                        SoldeCongeCalculator soldeCongeCalculator, EmployeService employeService, ContratEmployeRepository contratEmployeRepository, MontantRubriqueService montantRubriqueService) {
        this.bulletinPaieRepository = bulletinPaieRepository;
        this.contratEmployeService = contratEmployeService;
        this.absenceService = absenceService;
        this.soldeCongeCalculator = soldeCongeCalculator;
        this.employeService = employeService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.montantRubriqueService = montantRubriqueService;
    }

    public ResultatCongeDTO calculerAllocationConge(Long idContratEmploye, String typeOperation, String moisStr) {

        Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getById(idContratEmploye);
        if (contratOpt.isEmpty()) {
            throw new IllegalArgumentException("Contrat employé non trouvé");
        }

        ContractEmployeDTO contrat = contratOpt.get();
        Long employeId = contrat.getEmployeId();
        Long companyId = contrat.getCompanyId();

        // Conversion du mois
        YearMonth moisTraitement;
        try {
            moisTraitement = YearMonth.parse(moisStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format du mois invalide. Format attendu : yyyy-MM");
        }

        // Génération des 12 mois précédents
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        List<String> moisBulletins = new ArrayList<>();

        for (int i = 12; i >= 1; i--) {
            YearMonth mois = moisTraitement.minusMonths(i);
            moisBulletins.add(mois.format(formatter));
        }

        // Récupération bulletins
        List<BulletinPaie> bulletins =
                bulletinPaieRepository.findByEmployeIdAndMoisInOrderByMoisAsc(employeId, moisBulletins);

        double totalTemps = 0;
        BigDecimal totalSalaire = BigDecimal.ZERO;

        List<BulletinResumeDTO> bulletinsDTO = new ArrayList<>();

        for (BulletinPaie b : bulletins) {

            double tempsTravail = b.getTempsTravail();
            BigDecimal salaireBrut = b.getSalaireBrut() != null
                    ? b.getSalaireBrut()
                    : BigDecimal.ZERO;

            totalTemps += tempsTravail;
            totalSalaire = totalSalaire.add(salaireBrut);

            bulletinsDTO.add(
                    new BulletinResumeDTO(
                            b.getMois(),
                            tempsTravail,
                            salaireBrut
                    )
            );
        }

    /*
     ============================================
     PRIORITE 1 : VERIFIER SALAIRE MOYEN SAISI
     ============================================
     */

        List<MontantRubriqueDTO> salaireMoyenSaisie =
                montantRubriqueService.findSalaireMoyenJournalierByCompanyIdAndMoisRubrique(companyId, moisStr);

        Optional<MontantRubriqueDTO> salaireMoyenSaisiOpt =
                salaireMoyenSaisie.stream()
                        .filter(sm ->
                                sm.getContratEmployeId() != null
                                        && sm.getContratEmployeId().equals(idContratEmploye)
                        )
                        .findFirst();

    /*
     ============================================
     PRIORITE 2 : CALCUL AUTOMATIQUE SI NON SAISI
     ============================================
     */

        BigDecimal salaireProvisoire = salaireMoyenSaisiOpt
                .map(MontantRubriqueDTO::getMontantRubrique)
                .filter(m -> m != null && m.compareTo(BigDecimal.ZERO) > 0)
                .orElse(
                        totalTemps > 0
                                ? totalSalaire.divide(
                                BigDecimal.valueOf(totalTemps),
                                2,
                                RoundingMode.HALF_UP
                        )
                                : BigDecimal.ZERO
                );

    /*
     ============================================
     NORMALISATION PAIE
     ============================================
     */

        BigDecimal salaireNormal = salaireProvisoire.compareTo(BigDecimal.ZERO) > 0
                ? salaireProvisoire.multiply(BigDecimal.valueOf(30))
                .divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

    /*
     ============================================
     CALCUL NOMBRE DE JOURS
     ============================================
     */

        long joursPrisPourCalcul = 0;

        if ("INDEMNITE_CONGE".equalsIgnoreCase(typeOperation)) {

            SoldeCongeResponseDTO solde =
                    soldeCongeCalculator.calculerSoldeConge(idContratEmploye);

            joursPrisPourCalcul = solde != null
                    ? solde.getSoldeConge()
                    : 0;
        }

        else if ("ALLOCATION".equalsIgnoreCase(typeOperation)) {

            LocalDate debutMois = moisTraitement.atDay(1);
            LocalDate finMois = moisTraitement.atEndOfMonth();

            List<Absence> absences =
                    absenceService.getAbsencesDeductibles(
                            employeId,
                            debutMois,
                            finMois
                    );

            for (Absence absence : absences) {

                LocalDate debut = absence.getDateDebut().isBefore(debutMois)
                        ? debutMois
                        : absence.getDateDebut();

                LocalDate fin = absence.getDateFin().isAfter(finMois)
                        ? finMois
                        : absence.getDateFin();

                joursPrisPourCalcul +=
                        java.time.temporal.ChronoUnit.DAYS.between(debut, fin) + 1;
            }
        }

    /*
     ============================================
     CALCUL MONTANT FINAL
     ============================================
     */

        BigDecimal montant =
                salaireNormal.multiply(BigDecimal.valueOf(joursPrisPourCalcul));

    /*
     ============================================
     INFOS EMPLOYE
     ============================================
     */

        Employe employe =
                employeService.findById(employeId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Employé non trouvé"));

        EmployeInfoDTO employeInfos = new EmployeInfoDTO();

        employeInfos.setNom(employe.getNom());
        employeInfos.setPrenom(employe.getPrenom());
        employeInfos.setMatricule(employe.getMatricule());

    /*
     ============================================
     RETOUR RESULTAT
     ============================================
     */

        return new ResultatCongeDTO(
                totalTemps,
                totalSalaire,
                salaireProvisoire,
                salaireNormal,
                joursPrisPourCalcul,
                montant,
                typeOperation,
                bulletinsDTO,
                employeInfos,
                moisStr
        );
    }

//    public ResultatCongeDTO calculerAllocationConge(Long idContratEmploye, String typeOperation, String moisStr) {
//        Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getById(idContratEmploye);
//        if (contratOpt.isEmpty()) {
//            throw new IllegalArgumentException("Contrat employé non trouvé");
//        }
//
//        Long employeId = contratOpt.get().getEmployeId();
//
//        // Conversion du mois (ex: "2025-08") en YearMonth
//        YearMonth moisTraitement;
//        try {
//            moisTraitement = YearMonth.parse(moisStr); // Format attendu : "yyyy-MM"
//        } catch (DateTimeParseException e) {
//            throw new IllegalArgumentException("Format du mois invalide. Format attendu : yyyy-MM");
//        }
//
//        // Dates de recherche bulletins (12 mois avant le mois de traitement)
//        LocalDate premierJourMois = moisTraitement.atDay(1);
//        LocalDate dateDebut = premierJourMois.minusMonths(12);
//        LocalDate dateFin = premierJourMois.minusDays(1);
//
//        // Recherche des bulletins
//        // 1. Générer la liste des 12 mois précédents au format "yyyy-MM"
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
//        List<String> moisBulletins = new ArrayList<>();
//        for (int i = 12; i >= 1; i--) {
//            YearMonth mois = moisTraitement.minusMonths(i);
//            moisBulletins.add(mois.format(formatter));
//        }
//
//// 2. Récupérer les bulletins avec le champ `mois` (pas `dateCalculSalaire`)
//        List<BulletinPaie> bulletins = bulletinPaieRepository
//                .findByEmployeIdAndMoisInOrderByMoisAsc(employeId, moisBulletins);
//
//        double totalTemps = 0;
//        BigDecimal totalSalaire = BigDecimal.ZERO;
//
//        // Liste pour stocker les bulletins simplifiés
//        List<BulletinResumeDTO> bulletinsDTO = new ArrayList<>();
//
//        for (BulletinPaie b : bulletins) {
//            totalTemps += b.getTempsTravail();
//            BigDecimal salaireBrut = b.getSalaireBrut() != null ? b.getSalaireBrut() : BigDecimal.ZERO;
//            totalSalaire = totalSalaire.add(salaireBrut);
//
//            // Utilisation du champ "mois" directement
//            String moisBulletin = b.getMois();
//            bulletinsDTO.add(new BulletinResumeDTO(moisBulletin, b.getTempsTravail(), salaireBrut));
//        }
//
//        BigDecimal salaireProvisoire = (totalTemps > 0)
//                ? totalSalaire.divide(BigDecimal.valueOf(totalTemps), 2, RoundingMode.HALF_UP)
//                : BigDecimal.ZERO;
//
//        BigDecimal salaireNormal = (salaireProvisoire.compareTo(BigDecimal.ZERO) > 0)
//                ? salaireProvisoire.multiply(BigDecimal.valueOf(30)).divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP)
//                : BigDecimal.ZERO;
//
//        long joursPrisPourCalcul = 0;
//
//        if ("INDEMNITE_CONGE".equalsIgnoreCase(typeOperation)) {
//            SoldeCongeResponseDTO solde = soldeCongeCalculator.calculerSoldeConge(idContratEmploye);
//            joursPrisPourCalcul = solde != null ? solde.getSoldeConge() : 0;
//        } else if ("ALLOCATION".equalsIgnoreCase(typeOperation)) {
//            LocalDate debutMois = moisTraitement.atDay(1);
//            LocalDate finMois = moisTraitement.atEndOfMonth();
//
//            List<Absence> absences = absenceService.getAbsencesDeductibles(employeId, debutMois, finMois);
//            for (Absence absence : absences) {
//                LocalDate debut = absence.getDateDebut().isBefore(debutMois) ? debutMois : absence.getDateDebut();
//                LocalDate fin = absence.getDateFin().isAfter(finMois) ? finMois : absence.getDateFin();
//                joursPrisPourCalcul += java.time.temporal.ChronoUnit.DAYS.between(debut, fin) + 1;
//            }
//        }
//
//        BigDecimal montant = salaireNormal.multiply(BigDecimal.valueOf(joursPrisPourCalcul));
//
//        Employe employe = employeService.findById(employeId)
//                .orElseThrow(() -> new IllegalArgumentException("Employé non trouvé"));
//
//        // Récupération infos employé (depuis contrat, à adapter si besoin)
//        EmployeInfoDTO employeInfos = new EmployeInfoDTO();
//        employeInfos.setNom(employe.getNom());
//        employeInfos.setPrenom(employe.getPrenom());
//        employeInfos.setMatricule(employe.getMatricule());
//
//        // Retour DTO enrichi (adapter constructeur selon ta classe)
//        return new ResultatCongeDTO(
//                totalTemps,
//                totalSalaire,
//                salaireProvisoire,
//                salaireNormal,
//                joursPrisPourCalcul,
//                montant,
//                typeOperation,
//                bulletinsDTO,
//                employeInfos,
//                moisStr
//        );
//    }

    public HistoriqueMontantsDTO calculerHistoriqueMontants(Long idContratEmploye, String moisStr, String typeLicenciement) {
        Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getById(idContratEmploye);
        if (contratOpt.isEmpty()) {
            throw new IllegalArgumentException("Contrat employé non trouvé");
        }

        Long employeId = contratOpt.get().getEmployeId();
        Long companyId = contratOpt.get().getCompanyId();

        YearMonth moisTraitement;
        try {
            moisTraitement = YearMonth.parse(moisStr); // Format attendu : "yyyy-MM"
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format du mois invalide. Format attendu : yyyy-MM");
        }

        // 12 derniers mois avant le mois donné
        LocalDate premierJourMois = moisTraitement.atDay(1);
        LocalDate dateDebut = premierJourMois.minusMonths(12);
        LocalDate dateFin = premierJourMois.minusDays(1);

//        List<BulletinPaie> bulletins = bulletinPaieRepository
//                .findByEmployeIdAndDateCalculSalaireBetweenOrderByDateCalculSalaireAsc(employeId, dateDebut, dateFin);
//
//

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        List<String> moisBulletins = new ArrayList<>();
        for (int i = 12; i >= 1; i--) {
            YearMonth mois = moisTraitement.minusMonths(i);
            moisBulletins.add(mois.format(formatter));
        }

// 2. Récupérer les bulletins avec le champ `mois` (pas `dateCalculSalaire`)
        List<BulletinPaie> bulletins = bulletinPaieRepository
                .findByEmployeIdAndMoisInOrderByMoisAsc(employeId, moisBulletins);

        List<MontantMensuelDTO> montants = new ArrayList<>();
        BigDecimal montantTotal = BigDecimal.ZERO;

        for (BulletinPaie b : bulletins) {
            BigDecimal montant = b.getSalaireBrut() != null ? b.getSalaireBrut() : BigDecimal.ZERO;
            String mois = b.getMois();

            montants.add(new MontantMensuelDTO(mois, montant));
            montantTotal = montantTotal.add(montant);
        }

        BigDecimal montantMoyen = montants.size() > 0
                ? montantTotal.divide(BigDecimal.valueOf(montants.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // ⏱️ Ancienneté
        double anciennete = calculerAncienneteEnAnnees(employeId, companyId);

        // 🔢 Calcul du salaire moyen en fonction de l'ancienneté
        BigDecimal indemniteLicenciement = calculerSalaireMoyenSelonAnciennete(montantMoyen, anciennete, typeLicenciement);

        Employe employe = employeService.findById(employeId)
                .orElseThrow(() -> new IllegalArgumentException("Employé non trouvé"));

        EmployeInfoDTO employeInfos = new EmployeInfoDTO();
        employeInfos.setNom(employe.getNom());
        employeInfos.setPrenom(employe.getPrenom());
        employeInfos.setMatricule(employe.getMatricule());

        HistoriqueMontantsDTO dto = new HistoriqueMontantsDTO(
                montants,
                montantTotal,
                montantMoyen,
                employeInfos,
                typeLicenciement,
                moisStr
        );

        dto.setAnciennete(anciennete); // ajouter ce champ dans le DTO
        dto.setIndemniteSelonAnciennete(indemniteLicenciement); // ajouter ce champ dans le DTO

        return dto;
    }


    public BigDecimal calculerSalaireMoyenSelonAnciennete(BigDecimal montantMoyen, double anciennete, String typeLicenciement) {
        double tranche1 = 0;
        double tranche2 = 0;
        double tranche3 = 0;

        // Fractionnement en tranches
        if (anciennete > 10) {
            tranche1 = 5;
            tranche2 = 5;
            tranche3 = anciennete - 10;
        } else if (anciennete > 5) {
            tranche1 = 5;
            tranche2 = anciennete - 5;
        } else {
            tranche1 = anciennete;
        }

        // Définir les taux selon le type de licenciement
        double taux1, taux2, taux3;
        if ("COLLECTIF".equalsIgnoreCase(typeLicenciement)) {
            taux1 = 0.35;
            taux2 = 0.40;
            taux3 = 0.45;
        } else { // INDIVIDUEL
            taux1 = 0.30;
            taux2 = 0.35;
            taux3 = 0.40;
        }

        // Calcul
        BigDecimal total = montantMoyen.multiply(BigDecimal.valueOf(taux1 * tranche1))
                .add(montantMoyen.multiply(BigDecimal.valueOf(taux2 * tranche2)))
                .add(montantMoyen.multiply(BigDecimal.valueOf(taux3 * tranche3)));

        // ✅ Arrondi à l'entier le plus proche
        return total.setScale(0, RoundingMode.HALF_UP);
    }

    public double calculerAncienneteEnAnnees(Long employeId, Long entrepriseId) {
        List<ContratEmploye> contrats = contratEmployeRepository.findByEmployeIdAndCompanyId(employeId, entrepriseId);

        if (contrats.isEmpty()) {
            return 0.0;
        }
        long totalJours = 0;
        LocalDate aujourdHui = LocalDate.now();
        for (ContratEmploye contrat : contrats) {
            LocalDate debut = contrat.getDate_debut();
            LocalDate fin = contrat.getDate_fin();
            if (debut == null) continue;
            // dateFin nulle ou après aujourd'hui => prendre aujourd'hui
            if (fin == null || fin.isAfter(aujourdHui)) {
                fin = aujourdHui;
            }
            long joursContrat = ChronoUnit.DAYS.between(debut, fin) + 1;
            totalJours += joursContrat;
        }
        // Convertir jours en années décimales (en divisant par 365.25 pour tenir compte des années bissextiles)
        double annees = totalJours / 365.25;
        return annees;
    }

//    public HistoriqueMontantsDTO calculerHistoriqueMontants(Long idContratEmploye, String moisStr) {
//        Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getById(idContratEmploye);
//        if (contratOpt.isEmpty()) {
//            throw new IllegalArgumentException("Contrat employé non trouvé");
//        }
//
//        Long employeId = contratOpt.get().getEmployeId();
//
//        YearMonth moisTraitement;
//        try {
//            moisTraitement = YearMonth.parse(moisStr); // Format attendu : "yyyy-MM"
//        } catch (DateTimeParseException e) {
//            throw new IllegalArgumentException("Format du mois invalide. Format attendu : yyyy-MM");
//        }
//
//        // Période à analyser : 12 mois jusqu'au mois précisé
//        LocalDate premierJourMois = moisTraitement.atDay(1);
//        LocalDate dateDebut = premierJourMois.minusMonths(12);
//        LocalDate dateFin = premierJourMois.minusDays(1);
//
//        List<BulletinPaie> bulletins = bulletinPaieRepository
//                .findByEmployeIdAndDateCalculSalaireBetweenOrderByDateCalculSalaireAsc(employeId, dateDebut, dateFin);
//
//        List<MontantMensuelDTO> montants = new ArrayList<>();
//        BigDecimal montantTotal = BigDecimal.ZERO;
//
//        for (BulletinPaie b : bulletins) {
//            BigDecimal montant = b.getSalaireBrut() != null ? b.getSalaireBrut() : BigDecimal.ZERO;
//            String mois = b.getDateCalculSalaire().format(DateTimeFormatter.ofPattern("yyyy-MM"));
//
//            montants.add(new MontantMensuelDTO(mois, montant));
//            montantTotal = montantTotal.add(montant);
//        }
//
//        BigDecimal montantMoyen = montants.size() > 0
//                ? montantTotal.divide(BigDecimal.valueOf(montants.size()), 2, RoundingMode.HALF_UP)
//                : BigDecimal.ZERO;
//
//        Employe employe = employeService.findById(employeId)
//                .orElseThrow(() -> new IllegalArgumentException("Employé non trouvé"));
//
//        EmployeInfoDTO employeInfos = new EmployeInfoDTO();
//        employeInfos.setNom(employe.getNom());
//        employeInfos.setPrenom(employe.getPrenom());
//        employeInfos.setMatricule(employe.getMatricule());
//
//        return new HistoriqueMontantsDTO(
//                montants,
//                montantTotal,
//                montantMoyen,
//                employeInfos
//        );
//    }



}
