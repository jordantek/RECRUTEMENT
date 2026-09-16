package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.AvanceDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.AvanceRepository;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.service.paie.AvanceService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
    @RequestMapping("/api/paie/avances")
public class AvanceController {

    private final AvanceService avanceService;
    private final UserService userService;
    private final CompanyService companyService;
    private final EmployeService employeService;
    private final ContratEmployeService contratEmployeService;
    private final RubriqueService rubriqueService;
    private final CompanyRepository companyRepository;
    private final AvanceRepository avanceRepository;

    public AvanceController(
            AvanceService avanceService,
            UserService userService,
            CompanyService companyService,
            EmployeService employeService,
            ContratEmployeService contratEmployeService, RubriqueService rubriqueService, CompanyRepository companyRepository, AvanceRepository avanceRepository
    ) {
        this.avanceService = avanceService;
        this.userService = userService;
        this.companyService = companyService;
        this.employeService = employeService;
        this.contratEmployeService = contratEmployeService;
        this.rubriqueService = rubriqueService;
        this.companyRepository = companyRepository;
        this.avanceRepository = avanceRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AvanceDTO>>> getAll() {
        List<AvanceDTO> list = avanceService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des avances récupérée", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AvanceDTO>> getById(@PathVariable Long id) {
        Optional<AvanceDTO> opt = avanceService.getById(id);
        return opt.map(dto -> ResponseEntity.ok(new ApiResponse<>(true, "Avance trouvée", dto)))
                .orElseGet(() -> new ResponseEntity<>(new ApiResponse<>(false, "Avance non trouvée", null), HttpStatus.NOT_FOUND));
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AvanceDTO dto) {
        try {
            User currentUser = userService.getCurrentUser();
            List<ErrorResponse> errors = new ArrayList<>();

            // Champs obligatoires
            if (dto.getEmployeId() == null)
                errors.add(new ErrorResponse("employeId", "Le champ 'employeId' est requis."));
            if (dto.getCompanyId() == null)
                errors.add(new ErrorResponse("companyId", "Le champ 'companyId' est requis."));
            if (dto.getMontantTotal() == null)
                errors.add(new ErrorResponse("montant_total", "Le montant total est requis."));
            if (dto.getMontantMensuel() == null)
                errors.add(new ErrorResponse("montant_mensuel", "Le montant mensuel est requis."));
            if (dto.getMoisDemarrage() == null || dto.getMoisDemarrage().isBlank())
                errors.add(new ErrorResponse("mois_demarrage", "Le mois de démarrage est requis."));
            if (dto.getMoisFin() == null || dto.getMoisFin().isBlank())
                errors.add(new ErrorResponse("mois_fin", "Le mois de fin est requis."));

            // Format des mois - UTILISEZ LES MÉTHODES UTILITAIRES DU DTO
            YearMonth moisDebut = dto.getMoisDemarrageAsYearMonth();
            YearMonth moisFin = dto.getMoisFinAsYearMonth();

            // Validation de la conversion
            if (dto.getMoisDemarrage() != null && moisDebut == null) {
                errors.add(new ErrorResponse("mois_demarrage", "Format invalide. Utilisez 'YYYY-MM'."));
            }
            if (dto.getMoisFin() != null && moisFin == null) {
                errors.add(new ErrorResponse("mois_fin", "Format invalide. Utilisez 'YYYY-MM'."));
            }

            // Vérification de l'ordre
            if (moisDebut != null && moisFin != null && moisDebut.isAfter(moisFin)) {
                errors.add(new ErrorResponse("mois_demarrage", "Le mois de démarrage doit être antérieur ou égal au mois de fin."));
            }

            if (!errors.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs invalides", errors));

            // Récupération entités
            Optional<Employe> employeOpt = employeService.findById(dto.getEmployeId());
            Company company = companyService.getById(dto.getCompanyId());

            if (employeOpt.isEmpty())
                errors.add(new ErrorResponse("employeId", "Employé introuvable."));
            if (company == null)
                errors.add(new ErrorResponse("companyId", "Entreprise introuvable."));

            if (!errors.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de récupération", errors));

            Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getContratEmployeNonArreteParEmploye(employeOpt.get());
            if (contratOpt.isEmpty()) {
                errors.add(new ErrorResponse("contrat", "Contrat employé actif introuvable."));
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de récupération", errors));
            }

            ContractEmployeDTO contrat = contratOpt.get();

            // Vérifier date fin contrat
            LocalDate finContrat = contrat.getDateFin();
            if (finContrat != null && moisFin != null && moisFin.atEndOfMonth().isAfter(finContrat)) {
                errors.add(new ErrorResponse("mois_fin", "Le mois de fin dépasse la date de fin du contrat."));
            }

            // Calcul durée & vérification montant
            int duree = 0;
            if (moisDebut != null && moisFin != null) {
                duree = (int) ChronoUnit.MONTHS.between(moisDebut, moisFin) + 1;
                dto.setDureeAvance(duree);
            }

            if (dto.getMontantMensuel() != null && dto.getMontantTotal() != null && duree > 0) {
                BigDecimal attendu = dto.getMontantMensuel().multiply(BigDecimal.valueOf(duree));
                if (dto.getMontantTotal().compareTo(attendu) != 0) {
                    errors.add(new ErrorResponse("montant_total", "Le montant total doit être égal à montant mensuel × durée (" + duree + " mois)."));
                }
            }

            // Vérifier doublon : même employé + même période - APPROCHE SÉCURISÉE
            boolean doublonExists = false;
            if (moisDebut != null && moisFin != null) {
                try {
                    // OPTION 1 : Utilisez l'ID de l'employé au lieu de l'objet Employe
                    doublonExists = avanceRepository.existsByEmployeIdAndMoisDemarrageAndMoisFin(
                            employeOpt.get().getId(), moisDebut, moisFin
                    );
                } catch (Exception e) {
                    // OPTION 2 : Approche manuelle si l'option 1 échoue
                    doublonExists = checkAvanceManuellement(employeOpt.get().getId(), moisDebut, moisFin);
                }

                if (doublonExists) {
                    errors.add(new ErrorResponse("doublon", "Une avance existe déjà pour cet employé sur cette période."));
                }
            }

            if (!errors.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));

            // Construction finale & sauvegarde
            dto.setAddedById(currentUser.getId());

            // Conversion du contrat DTO en entité
            ContratEmploye contratEntity = contratEmployeService.findById(contrat.getId())
                    .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

            AvanceDTO saved = avanceService.save(dto, contratEntity, employeOpt.get(), company, currentUser);
            return new ResponseEntity<>(new ApiResponse<>(true, "Avance créée avec succès", saved), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur lors de la création de l'avance: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Méthode utilitaire pour vérifier manuellement les doublons
    private boolean checkAvanceManuellement(Long employeId, YearMonth moisDebut, YearMonth moisFin) {
        List<Avance> avancesExistantes = avanceRepository.findByEmployeId(employeId);
        return avancesExistantes.stream()
                .anyMatch(avance ->
                        avance.getMoisDemarrage().equals(moisDebut) &&
                                avance.getMoisFin().equals(moisFin)
                );
    }
//    @PostMapping
//    public ResponseEntity<?> create(@RequestBody AvanceDTO dto) {
//        try {
//            User currentUser = userService.getCurrentUser();
//            List<ErrorResponse> errors = new ArrayList<>();
//
//            // Champs obligatoires
//            if (dto.getEmployeId() == null)
//                errors.add(new ErrorResponse("employeId", "Le champ 'employeId' est requis."));
//            if (dto.getCompanyId() == null)
//                errors.add(new ErrorResponse("companyId", "Le champ 'companyId' est requis."));
//            if (dto.getMontantTotal() == null)
//                errors.add(new ErrorResponse("montant_total", "Le montant total est requis."));
//            if (dto.getMontantMensuel() == null)
//                errors.add(new ErrorResponse("montant_mensuel", "Le montant mensuel est requis."));
//            if (dto.getMoisDemarrage() == null || dto.getMoisDemarrage().isBlank())
//                errors.add(new ErrorResponse("mois_demarrage", "Le mois de démarrage est requis."));
//            if (dto.getMoisFin() == null || dto.getMoisFin().isBlank())
//                errors.add(new ErrorResponse("mois_fin", "Le mois de fin est requis."));
//
//            // Format des mois
//            YearMonth moisDebut = null;
//            YearMonth moisFin = null;
//
//            try {
//                if (dto.getMoisDemarrage() != null) {
//                    moisDebut = YearMonth.parse(dto.getMoisDemarrage());
//                }
//            } catch (DateTimeParseException e) {
//                errors.add(new ErrorResponse("mois_demarrage", "Format invalide. Utilisez 'YYYY-MM'."));
//            }
//
//            try {
//                if (dto.getMoisFin() != null) {
//                    moisFin = YearMonth.parse(dto.getMoisFin());
//                }
//            } catch (DateTimeParseException e) {
//                errors.add(new ErrorResponse("mois_fin", "Format invalide. Utilisez 'YYYY-MM'."));
//            }
//
//// Vérification de l'ordre
//            if (moisDebut != null && moisFin != null && moisDebut.isAfter(moisFin)) {
//                errors.add(new ErrorResponse("mois_demarrage", "Le mois de démarrage doit être antérieur ou égal au mois de fin."));
//            }
//
//            if (!errors.isEmpty())
//                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs invalides", errors));
//
//            // Récupération entités
//            Optional<Employe> employeOpt = employeService.findById(dto.getEmployeId());
//            Company company = companyService.getById(dto.getCompanyId());
//
//            if (employeOpt.isEmpty())
//                errors.add(new ErrorResponse("employeId", "Employé introuvable."));
//            if (company == null)
//                errors.add(new ErrorResponse("companyId", "Entreprise introuvable."));
//
//            Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getContratEmployeNonArreteParEmploye(employeOpt.get());
//            if (contratOpt.isEmpty())
//                errors.add(new ErrorResponse("contrat", "Contrat employé actif introuvable."));
//
//            if (!errors.isEmpty())
//                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de récupération", errors));
//
//            ContractEmployeDTO contrat = contratOpt.get();
//
//            // Vérifier date fin contrat
//            LocalDate finContrat = contrat.getDateFin();
//            if (finContrat != null && moisFin.atEndOfMonth().isAfter(finContrat)) {
//                errors.add(new ErrorResponse("mois_fin", "Le mois de fin dépasse la date de fin du contrat."));
//            }
//
//            // Calcul durée & vérification montant
//            int duree = (int) ChronoUnit.MONTHS.between(moisDebut, moisFin) + 1;
//            dto.setDureeAvance(duree);
//
//            if (dto.getMontantMensuel() != null && dto.getMontantTotal() != null) {
//                BigDecimal attendu = dto.getMontantMensuel().multiply(BigDecimal.valueOf(duree));
//                if (dto.getMontantTotal().compareTo(attendu) != 0) {
//                    errors.add(new ErrorResponse("montant_total", "Le montant total doit être égal à montant mensuel × durée (" + duree + " mois)."));
//                }
//            }
//
//            // Vérifier doublon : même employé + même période
//            Optional<Avance> doublon = avanceRepository.findByEmployeAndMoisDemarrageAndMoisFin(
//                    employeOpt.get(), moisDebut, moisFin
//            );
//            if (doublon.isPresent()) {
//                errors.add(new ErrorResponse("doublon", "Une avance existe déjà pour cet employé sur cette période."));
//            }
//
//            if (!errors.isEmpty())
//                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
//
//            // Construction finale & sauvegarde
//            dto.setAddedById(currentUser.getId());
//            AvanceDTO saved = avanceService.save(dto, contrat.toEntity(), employeOpt.get(), company,  currentUser);
//            return new ResponseEntity<>(new ApiResponse<>(true, "Avance créée avec succès", saved), HttpStatus.CREATED);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création de l'avance", null), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (!avanceService.exists(id))
            return new ResponseEntity<>(new ApiResponse<>(false, "Avance non trouvée", null), HttpStatus.NOT_FOUND);

        avanceService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Avance supprimée avec succès", null));
    }

    @GetMapping("/par-employe/{employeId}")
    public ResponseEntity<ApiResponse<List<AvanceDTO>>> getByEmploye(@PathVariable Long employeId) {
        if (!employeService.existsById(employeId))
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Employé introuvable", null));

        List<AvanceDTO> list = avanceService.findByEmployeId(employeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des avances de l'employé", list));
    }

    @GetMapping("/par-entreprise/{companyId}")
    public ResponseEntity<ApiResponse<List<AvanceDTO>>> getByCompany(@PathVariable Long companyId) {
        if (!companyService.existsById(companyId))
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

        List<AvanceDTO> list = avanceService.findByCompanyId(companyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des avances de l'entreprise", list));
    }

    @GetMapping("/entreprise/{companyId}/mois-demarrage")
    public ResponseEntity<?> getAvancesByCompanyAndMoisDemarrageBetween(
            @PathVariable Long companyId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        try {
            // Conversion des paramètres String en YearMonth
            YearMonth startMonth = YearMonth.parse(start);
            YearMonth endMonth = YearMonth.parse(end);

            if (startMonth.isAfter(endMonth)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Le mois de démarrage ne peut pas être après le mois de fin.", null));
            }

            // Vérifier l'existence de l'entreprise
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Entreprise introuvable avec l'ID : " + companyId, null));
            }

            // Appel du service
            List<AvanceDTO> dtos = avanceService.findByCompanyAndMoisDemarrageBetween(companyId, startMonth, endMonth);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Avances récupérées avec succès", dtos)
            );

        } catch (DateTimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Format des mois invalide. Utilisez 'yyyy-MM'.", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des avances", null));
        }
    }


    @GetMapping("/entreprise/{companyId}/fin-periode")
    public ResponseEntity<?> getAvancesByCompanyAndMoisFin(
            @PathVariable Long companyId,
            @RequestParam("moisDebut") String moisDebut,
            @RequestParam("moisFin") String moisFin) {
        try {
            YearMonth start = YearMonth.parse(moisDebut);
            YearMonth end = YearMonth.parse(moisFin);

            if (start.isAfter(end)) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Le mois de début ne peut pas être après le mois de fin", null)
                );
            }

            // Vérification existence de l'entreprise
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Entreprise introuvable", null)
                );
            }

            List<AvanceDTO> dtos = avanceService.findAvancesByCompanyAndMoisFinBetween(companyId, start, end);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Avances récupérées avec succès", dtos)
            );
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Le format des mois est invalide. Format attendu : yyyy-MM", null)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des avances", null));
        }
    }

}
