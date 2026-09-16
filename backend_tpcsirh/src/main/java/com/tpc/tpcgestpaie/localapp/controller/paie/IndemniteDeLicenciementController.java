package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.dto.accessoire.*;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.IndemniteLicenciement;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.stc.IndemniteLicenciementRepository;
import com.tpc.tpcgestpaie.localapp.service.accessoire.CongeService;
import com.tpc.tpcgestpaie.localapp.service.administration.AbsenceService;
import com.tpc.tpcgestpaie.localapp.service.soldeToutCompte.IndemniteLicenciementService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/indemnite-licenciement")
public class IndemniteDeLicenciementController {

    private final CongeService congeService;
    private final ContratEmployeRepository contratEmployeRepository;

    private final AbsenceService absenceService;
    private final CompanyRepository companyRepository;
    private final IndemniteLicenciementService indemniteLicenciementService;
    private final IndemniteLicenciementRepository indemniteLicenciementRepository;

    public IndemniteDeLicenciementController(CongeService congeService, ContratEmployeRepository contratEmployeRepository, AbsenceService absenceService, CompanyRepository companyRepository, IndemniteLicenciementService indemniteLicenciementService, IndemniteLicenciementRepository indemniteLicenciementRepository) {
        this.congeService = congeService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.absenceService = absenceService;
        this.companyRepository = companyRepository;
        this.indemniteLicenciementService = indemniteLicenciementService;
        this.indemniteLicenciementRepository = indemniteLicenciementRepository;
    }

    @PostMapping("/calcul-montant")
    public ResponseEntity<?> getHistoriqueMontants(@RequestBody HistoriqueMontantsRequestDTO request) {
        try {
            List<ErrorResponse> errors = new ArrayList<>();

            // Vérification des IDs
            if (request.getIdContratEmploye() == null || !contratEmployeRepository.existsById(request.getIdContratEmploye())) {
                errors.add(new ErrorResponse("idContratEmploye", "Contrat employé introuvable"));
            }

            if (request.getIdCompany() == null || !companyRepository.existsById(request.getIdCompany())) {
                errors.add(new ErrorResponse("idCompany", "Entreprise introuvable"));
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }
            // Récupération du contrat et de l’employé
            ContratEmploye contratEmploye = contratEmployeRepository.findById(request.getIdContratEmploye())
                    .orElseThrow(() -> new RuntimeException("ContratEmploye non trouvé"));
            Employe employe = contratEmploye.getEmploye();

            // Vérifier que l'employé a bien un contrat avec cette entreprise
            boolean exist = contratEmployeRepository.existsByEmployeIdAndCompanyId(employe.getId(), request.getIdCompany());
            if (!exist) {
                errors.add(new ErrorResponse("contrats", "Aucun contrat entre l'Entreprise et l'employé"));
            }

            // Vérification du mois
            YearMonth moisTraitement = null;
            if (request.getMois() == null || request.getMois().isBlank()) {
                errors.add(new ErrorResponse("mois", "Le mois de traitement est obligatoire (format attendu : yyyy-MM)"));
            } else {
                try {
                    moisTraitement = YearMonth.parse(request.getMois());
                } catch (DateTimeParseException e) {
                    errors.add(new ErrorResponse("mois", "Format du mois invalide. Format attendu : yyyy-MM"));
                }
            }

            if (request.getTypeLicenciement() == null ||
                    !(request.getTypeLicenciement().equals("INDIVIDUEL") ||
                            request.getTypeLicenciement().equals("COLLECTIF"))) {
                errors.add(new ErrorResponse("typeLicenciement", "Le type de licenciement est invalide. Valeurs attendues : INDIVIDUEL ou COLLECTIF."));
            }
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }
            // 🔥 Calcul de l'historique et de l’ancienneté
            HistoriqueMontantsDTO resultat = congeService.calculerHistoriqueMontants(
                    request.getIdContratEmploye(),
                    request.getMois(),
                    request.getTypeLicenciement()
            );
            double anciennete = calculerAncienneteEnAnnees(
                    employe.getId(),
                    request.getIdCompany()
            );
            // On set l’ancienneté dans le DTO
            resultat.setAnciennete(anciennete);
            // 🔥 Calcul de l’indemnité
            BigDecimal montantMoyen = resultat.getMontantMoyen();
            BigDecimal indemnite = congeService.calculerSalaireMoyenSelonAnciennete(montantMoyen,anciennete,request.getTypeLicenciement());
            resultat.setIndemniteSelonAnciennete(indemnite);
            return new ResponseEntity<>(new ApiResponse<>(true, "Indemnité calculée avec succès", resultat), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur interne du serveur", null));
        }
    }

    @PostMapping
    public ResponseEntity<?> createIndemnite(@RequestBody IndemniteLicenciementRequestDTO request) {
        List<ErrorResponse> errors = new ArrayList<>();
        // Vérification des IDs
        if (request.getIdContratEmploye() == null || !contratEmployeRepository.existsById(request.getIdContratEmploye())) {
            errors.add(new ErrorResponse("idContratEmploye", "Contrat employé introuvable"));
        }
        if (request.getIdCompany() == null || !companyRepository.existsById(request.getIdCompany())) {
            errors.add(new ErrorResponse("idCompany", "Entreprise introuvable"));
        }
        if (request.getData() == null) {
            errors.add(new ErrorResponse("data", "Données 'data' obligatoires"));
        } else {
            if (request.getData().getMoisCalculSalaire() == null || request.getData().getMoisCalculSalaire().isBlank()) {
                errors.add(new ErrorResponse("moisCalculSalaire", "Le mois de calcul salaire est obligatoire (format yyyy-MM)"));
            } else {
                try {
                    YearMonth.parse(request.getData().getMoisCalculSalaire());
                } catch (DateTimeParseException e) {
                    errors.add(new ErrorResponse("moisCalculSalaire", "Format du mois invalide. Format attendu : yyyy-MM"));
                }
            }
            if (request.getData().getTypeLicencement() == null ||
                    !(request.getData().getTypeLicencement().equalsIgnoreCase("INDIVIDUEL") ||
                            request.getData().getTypeLicencement().equalsIgnoreCase("COLLECTIF"))) {
                errors.add(new ErrorResponse("typeLicencement", "Le type de licenciement est invalide. Valeurs attendues : INDIVIDUEL ou COLLECTIF"));
            }
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
        }
        // Check doublon : existe-t-il déjà une indemnité pour ce contrat + mois + type ?
        YearMonth mois = YearMonth.parse(request.getData().getMoisCalculSalaire());
        boolean exists = indemniteLicenciementRepository.existsByContratEmployeIdAndMoisCalculSalaireAndTypeLicencement(
                request.getIdContratEmploye(), mois, request.getData().getTypeLicencement().toUpperCase());

        if (exists) {
            errors.add(new ErrorResponse("doublon", "Une indemnité existe déjà pour ce contrat, ce mois et ce type de licenciement"));
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Doublon détecté", errors));
        }
        try {
            IndemniteLicenciement saved = indemniteLicenciementService.saveFromDTO(request);
            IndemniteLicenciementResponseDTO responseDTO = new IndemniteLicenciementResponseDTO(saved);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Indemnité enregistrée avec succès", responseDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur interne du serveur", null));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<IndemniteLicenciementResponseDTO>>> listByEmployeAndCompany(
            @RequestParam(required = false) Long employeId,
            @RequestParam(required = false) Long companyId) {

        try {
            if (employeId == null && companyId == null) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Au moins un paramètre 'employeId' ou 'companyId' doit être fourni.", null));
            }

            List<IndemniteLicenciementResponseDTO> result;

            if (employeId != null && companyId != null) {
                result = indemniteLicenciementService.listByEmployeAndCompany(employeId, companyId);
            } else if (employeId != null) {
                result = indemniteLicenciementService.listByEmploye(employeId);
            } else {
                result = indemniteLicenciementService.listByCompany(companyId);
            }

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste récupérée avec succès", result));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur serveur: " + e.getMessage(), null));
        }
    }
    @PostMapping("/test-salaire-moyen")
    public ResponseEntity<?> testerSalaireMoyen(@RequestBody SalaireMoyenRequestDTO request) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Vérification du type de licenciement
        if (request.getLicenciement() == null ||
                !(request.getLicenciement().equalsIgnoreCase("INDIVIDUEL") ||
                        request.getLicenciement().equalsIgnoreCase("COLLECTIF"))) {
            errors.add(new ErrorResponse("typeLicenciement", "Type de licenciement invalide (INDIVIDUEL ou COLLECTIF attendu)"));
        }

        if (request.getGlobalMensuelMoyen() == null || request.getGlobalMensuelMoyen().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new ErrorResponse("montantMoyen", "Le montant moyen doit être un nombre positif"));
        }

        if (request.getAncienneteTotal() < 0) {
            errors.add(new ErrorResponse("anciennete", "L'ancienneté doit être positive"));
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
        }

        BigDecimal resultat = congeService.calculerSalaireMoyenSelonAnciennete(
                request.getGlobalMensuelMoyen(),
                request.getAncienneteTotal(),
                request.getLicenciement()
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Calcul réussi", resultat));
    }

    @PostMapping("/anciennete")
    public ResponseEntity<?> calculAnciennete(@RequestBody DemandeAncienneteDTO request) {
        try {

            List<ErrorResponse> errors = new ArrayList<>();
            // Vérification de l'identifiant du contrat


            if (request.getIdEmploye() == null || !contratEmployeRepository.existsById(request.getIdEmploye())) {
                errors.add(new ErrorResponse("idEmploye", "Employé introuvable"));
            }

            if (request.getIdCompany() == null || !companyRepository.existsById(request.getIdCompany())) {
                errors.add(new ErrorResponse("idCompany", "Entreprise employé introuvable"));
            }
            // Vérification que l'employé a des contrats dans l'entreprise
            boolean exist = contratEmployeRepository.existsByEmployeIdAndCompanyId(request.getIdEmploye(), request.getIdCompany());
             if (!exist) {
                errors.add(new ErrorResponse("contrats", "Aucun contrat entre l'Entreprise et l'employé"));
            }

        // Récupération de tous les contrats de l'employé dans l'entreprise
        List<ContratEmploye> contrats = contratEmployeRepository.findByEmployeIdAndCompanyId(request.getIdEmploye(), request.getIdCompany());

        long totalJours = 0;
        LocalDate today = LocalDate.now();

        for (ContratEmploye contrat : contrats) {
            LocalDate dateDebut = contrat.getDate_debut();
            LocalDate dateFin = contrat.getDate_fin();

            // Si le contrat est toujours en cours ou sa date de fin est dans le futur
            if (dateFin == null || dateFin.isAfter(today)) {
                dateFin = today;
            }

            totalJours += ChronoUnit.DAYS.between(dateDebut, dateFin);
        }
        // Conversion en années décimales (en tenant compte des années bissextiles)
        double annees = totalJours / 365.25;
        return new ResponseEntity<>(new ApiResponse<>(true, "Ancienneté calculée avec succès", annees), HttpStatus.CREATED);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur interne du serveur", null));
        }
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


}