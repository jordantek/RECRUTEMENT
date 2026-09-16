package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.dto.contrat.ArretContratDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratArreteDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratEmployeGlobalDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeContratActifDTO;
import com.tpc.tpcgestpaie.localapp.helper.ContratEmployeHelper;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import com.tpc.tpcgestpaie.localapp.util.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/paie/contrats-employes")
public class ContratEmployeController {

    private final ContratEmployeService contratService;
    private final ContratEmployeHelper contratHelper;
    private final UserService userService;
    private final DepartementService departementService;
    private final CompanyService companyService;
    private final ContratEmployeService contratEmployeService;
    private final RequestHelper requestHelper;
    private final AuditLogService auditService;
    private  final EmployeService employeService;
    private final NotificationService notificationService;
    private  final CompanyRepository companyRepository;
    private  final  EmployeDiplomeService employeDiplomeService;
    private final ContratEmployeRubriqueService contratEmployeRubriqueService;

    public ContratEmployeController(
            ContratEmployeService contratService,
            ContratEmployeHelper contratHelper,
            UserService userService, DepartementService departementService, CompanyService companyService, ContratEmployeService contratEmployeService,
            RequestHelper requestHelper,
            AuditLogService auditService, EmployeService employeService,
            NotificationService notificationService, CompanyRepository companyRepository, EmployeDiplomeService employeDiplomeService, ContratEmployeRubriqueService contratEmployeRubriqueService
    ) {
        this.contratService = contratService;
        this.contratHelper = contratHelper;
        this.userService = userService;
        this.departementService = departementService;
        this.companyService = companyService;
        this.contratEmployeService = contratEmployeService;
        this.requestHelper = requestHelper;
        this.auditService = auditService;
        this.employeService = employeService;
        this.notificationService = notificationService;
        this.companyRepository = companyRepository;
        this.employeDiplomeService = employeDiplomeService;
        this.contratEmployeRubriqueService = contratEmployeRubriqueService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getAllPaginated(pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Liste paginée des contrats récupérée avec succès",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des contrats", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/par-entreprise/{companyId}/paginated")
    public ResponseEntity<?> getAllContratEmployeParEntreprisePaginated(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "") String search // 👈 AJOUT
    ) {
        try {
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);

            // 👇 Passer "search" au service
            Page<ContractEmployeDTO> contratsPage =
                    contratService.getAllContratEmployeParEntreprisePaginated(companyId, search, pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats paginés récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }


    @GetMapping("/en-cours-validite/paginated")
    public ResponseEntity<?> getAllContratEmployeEnCoursDeValiditePaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getAllContratEmployeEnCoursDeValiditePaginated(pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats en cours de validité paginés récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    @GetMapping("/en-cours-validite/entreprise/{companyId}/paginated")
    public ResponseEntity<?> getAllContratEmployeEnCoursDeValiditeParEntreprisePaginated(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getAllContratEmployeEnCoursDeValiditeParEntreprisePaginated(companyId, pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats en cours de validité pour l'entreprise paginés récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    @GetMapping("/non-arrete/paginated")
    public ResponseEntity<?> getAllContratEmployeNonArretePaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getAllContratEmployeNonArretePaginated(pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats non arrêtés paginés récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    @GetMapping("/actifs/{companyId}/paginated")
    public ResponseEntity<?> getEmployesActifsParEntreprisePaginated(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<EmployeContratActifDTO> dtosPage = contratService.getEmployesAvecContratActifParEntreprisePaginated(companyId, pageable);

            PaginatedResponse<List<EmployeContratActifDTO>> response = new PaginatedResponse<>(
                    true,
                    "Liste paginée des employés avec contrat actif récupérée avec succès",
                    dtosPage.getContent(),
                    dtosPage.getNumber(),
                    dtosPage.getSize(),
                    dtosPage.getTotalElements(),
                    dtosPage.getTotalPages(),
                    dtosPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des employés", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-employe/{employeId}/paginated")
    public ResponseEntity<ApiResponse<PaginatedResponse<List<ContractEmployeDTO>>>> getContratsByEmployePaginated(
            @PathVariable Long employeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getContratsByEmployeIdPaginated(employeId, pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Liste paginée des contrats récupérée avec succès",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des contrats paginée récupérée avec succès", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des contrats", null));
        }
    }

       @GetMapping("/non-arrete/entreprise/{companyId}/departement/{departementId}/paginated")
    public ResponseEntity<?> getAllContratEmployeNonArreteParEntrepriseEtParDepartementPaginated(
            @PathVariable Long companyId,
            @PathVariable Long departementId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }
            Optional<Departement> departementOpt = departementService.findById(departementId);
            if (!departementOpt.isPresent()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Département non trouvé", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getAllContratEmployeNonArreteParEntrepriseEtParDepartementPaginated(companyId, departementId, pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats non arrêtés par entreprise et département paginés récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }


//    @GetMapping
//    public ResponseEntity<?> getAll() {
//        try {
//            List<ContractEmployeDTO> contrats = contratService.getAll();
//            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des contrats récupérée avec succès", contrats));
//        } catch (Exception e) {
//            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des contrats", null), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<ContractEmployeDTO> contrat = contratService.getById(id);
            if (contrat.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Contrat trouvé", contrat.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Contrat non trouvé", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération du contrat", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ContractEmployeDTO contratDto) {
        try {
            User currentUser = userService.getCurrentUser();

            // Validation via helper sur le DTO
            List<ErrorResponse> errors = contratHelper.getInvalidFieldMessages(contratDto.toEntity());

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            // Ajout des métadonnées
            contratDto.setStatusContrat("CONTRAT EN COURS");
            contratDto.setCreatedAt(LocalDateTime.now());
            contratDto.setAddedById(currentUser.getId());

            // Sauvegarde via le service
            ContractEmployeDTO savedContrat = contratService.save(contratDto);

            // Audit & Notification (à décommenter si déjà en place)
        /*
        auditService.log(
                "Ajout d’un contrat",
                "contrats_employes",
                currentUser.getId(),
                "Ajout d’un contrat employé par " + currentUser.getFullName(),
                currentUser,
                requestHelper.getClientIp(),
                requestHelper.getUserAgent()
        );

        notificationService.createNotification(
                currentUser,
                "Nouveau contrat",
                "Un contrat employé a été ajouté avec succès 📄"
        );
        */

            return new ResponseEntity<>(new ApiResponse<>(true, "Contrat créé avec succès", savedContrat), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création du contrat", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ContractEmployeDTO updatedDto) {
        try {
            Optional<ContractEmployeDTO> existingDtoOpt = contratService.getById(id);

            if (existingDtoOpt.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Contrat non trouvé", null), HttpStatus.NOT_FOUND);
            }

            User currentUser = userService.getCurrentUser();

            // Validation du DTO
            List<ErrorResponse> errors = contratHelper.getInvalidFieldMessages(updatedDto.toEntity());
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            // Récupérer l'entité existante depuis le DTO
            ContractEmployeDTO contratToUpdate = existingDtoOpt.get();

            // Mise à jour des champs (tu peux étendre si nécessaire)
            contratToUpdate.setTypeContrat(updatedDto.getTypeContrat());
            contratToUpdate.setDateDebut(updatedDto.getDateDebut());
            contratToUpdate.setDateFin(updatedDto.getDateFin());
            contratToUpdate.setStatusContrat(updatedDto.getStatusContrat());
            contratToUpdate.setPosteId(updatedDto.getPosteId());
            contratToUpdate.setUpdatedAt(LocalDateTime.now());

            // Ajout éventuel de métadonnées
            contratToUpdate.setAddedById(currentUser.getId());

            // Sauvegarde
            ContractEmployeDTO savedDto = contratService.save(contratToUpdate);

            // Audit & Notification
            auditService.log(
                    "Modification d’un contrat",
                    "contrats_employes",
                    currentUser.getId(),
                    "Modification d’un contrat employé par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Contrat modifié",
                    "Le contrat employé a été modifié avec succès ✏️"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Contrat mis à jour avec succès", savedDto));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour du contrat", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Liste par entreprise
    @GetMapping("/par-entreprise/{companyId}")
    public ResponseEntity<?> getAllContratEmployeParEntreprise(@PathVariable Long companyId) {
        try {
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            List<ContractEmployeDTO> contrats = contratService.getAllContratEmployeParEntreprise(companyId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrats récupérés", contrats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }
    // Contrats en cours de validité (pas de paramètre, pas besoin de validation)
    @GetMapping("/en-cours-validite")
    public ResponseEntity<?> getAllContratEmployeEnCoursDeValidite() {
        try {
            List<ContractEmployeDTO> contrats = contratService.getAllContratEmployeEnCoursDeValidite();
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrats en cours de validité récupérés", contrats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    // Contrats en cours de validité par entreprise
    @GetMapping("/en-cours-validite/entreprise/{companyId}")
    public ResponseEntity<?> getAllContratEmployeEnCoursDeValiditeParEntreprise(@PathVariable Long companyId) {
        try {
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            List<ContractEmployeDTO> contrats = contratService.getAllContratEmployeEnCoursDeValiditeParEntreprise(companyId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrats en cours de validité pour l'entreprise récupérés", contrats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    // Contrats non arrêtés (pas de paramètre)

    @GetMapping("/non-arrete")
    public ResponseEntity<?> getAllContratEmployeNonArrete() {
        try {
            List<ContractEmployeDTO> contrats = contratService.getAllContratEmployeNonArrete();
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrats non arrêtés récupérés", contrats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    // Contrats non arrêtés par entreprise
    @GetMapping("/non-arrete/entreprise/{companyId}")
    public ResponseEntity<?> getAllContratEmployeNonArreteParEntreprise(@PathVariable Long companyId) {
        try {
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            List<ContractEmployeDTO> contrats = contratService.getAllContratEmployeNonArreteParEntreprise(companyId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrats non arrêtés de l'entreprise récupérés", contrats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    // Contrats non arrêtés par entreprise et département
    @GetMapping("/non-arrete/entreprise/{companyId}/departement/{departementId}")
    public ResponseEntity<?> getAllContratEmployeNonArreteParEntrepriseEtParDepartement(
            @PathVariable Long companyId,
            @PathVariable Long departementId) {
        try {
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }
            Optional<Departement> departementOpt = departementService.findById(departementId);
            if (!departementOpt.isPresent()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Département non trouvé", null));
            }

            List<ContractEmployeDTO> contrats = contratService.getAllContratEmployeNonArreteParEntrepriseEtParDepartement(companyId, departementId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrats non arrêtés par entreprise et département récupérés", contrats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    // Contrats non arrêtés par période
    @GetMapping("/non-arrete/periode")
    public ResponseEntity<?> getAllContratEmployeNonArreteParPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        try {
            if (dateDebut == null || dateFin == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Les dates de début et fin sont requises", null));
            }
            if (dateDebut.isAfter(dateFin)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "La date de début doit être avant la date de fin", null));
            }

            List<ContractEmployeDTO> contrats = contratService.getAllContratEmployeNonArreteParPeriode(dateDebut, dateFin);
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrats non arrêtés sur la période récupérés", contrats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }
    // Contrats non arrêtés par période pour fin période d'essai
    @GetMapping("/non-arrete/periode/fin-periode-essai")
    public ResponseEntity<?> getAllContratEmployeNonArreteParPeriodeFinPeriodeEssai(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        try {
            if (dateDebut == null || dateFin == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Les dates de début et fin sont requises", null));
            }
            if (dateDebut.isAfter(dateFin)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "La date de début doit être avant la date de fin", null));
            }

            List<ContractEmployeDTO> contrats = contratService.getAllContratEmployeNonArreteParPeriodePourFinPeriodeEssai(dateDebut, dateFin);
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrats non arrêtés avec fin période d'essai récupérés", contrats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    // Count contrats arrêtés par période et entreprise
    @GetMapping("/count/arrete/entreprise/{companyId}")
    public ResponseEntity<?> getCountAllContratEmployeArreteParPeriodeEtParEntreprise(
            @PathVariable Long companyId,
            @RequestParam LocalDateTime dateDebut,
            @RequestParam LocalDateTime dateFin) {
        try {
            // Vérification existence de l'entreprise
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            // Vérification cohérence des dates (null ne peut pas arriver si params requis)
            if (dateDebut.isAfter(dateFin)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "La date de début doit être avant la date de fin", null));
            }

            Company company = companyService.getById(companyId);
            Long count = contratService.getCountAllContratEmployeArreteParPeriodeEtParEntreprise(company, dateDebut, dateFin);

            return ResponseEntity.ok(new ApiResponse<>(true, "Nombre de contrats arrêtés récupéré", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    @GetMapping("/non-arrete/entreprise/{companyId}/departement/{departementId}/employe/{employeId}")
    public ResponseEntity<?> getContratEmployeNonArreteParEntrepriseParDepartementEtParEmploye(
            @PathVariable Long companyId,
            @PathVariable Long departementId,
            @PathVariable Long employeId) {

        Optional<Company> companyOpt = Optional.ofNullable(companyService.getById(companyId));
        if (companyOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Company not found with id: " + companyId, null));
        }

        Optional<Departement> departementOpt = departementService.findById(departementId);
        if (departementOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Departement not found with id: " + departementId, null));
        }

        Optional<Employe> employeOpt = employeService.findById(employeId);
        if (employeOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Employe not found with id: " + employeId, null));
        }

        List<ContractEmployeDTO> result = contratEmployeService
                .getContratEmployeNonArreteParEntrepriseParDepartementEtParEmploye(
                        companyOpt.get(),
                        departementOpt.get(),
                        employeOpt.get()
                );

        return ResponseEntity.ok(new ApiResponse<>(true, "Contrats non arrêtés filtrés", result));
    }

    @GetMapping("/non-arrete/employe/{employeId}")
    public ResponseEntity<?> getContratEmployeNonArreteParEmploye(@PathVariable Long employeId) {
        Optional<Employe> employeOpt = employeService.findById(employeId);
        if (employeOpt.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(false, "Employé non trouvé avec id : " + employeId, null));
        }

        Optional<ContractEmployeDTO> resultOpt = contratEmployeService.getContratEmployeNonArreteParEmploye(employeOpt.get());

        if (resultOpt.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Aucun contrat non arrêté trouvé pour cet employé", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Contrat non arrêté de l'employé", resultOpt.get()));
    }

//    @GetMapping("/non-arrete/employe/{employeId}")
//    public ResponseEntity<?> getContratEmployeNonArreteParEmploye(@PathVariable Long employeId) {
//        Optional<Employe> employeOpt = employeService.findById(employeId);
//        if (employeOpt.isEmpty()) {
//            return ResponseEntity.status(404)
//                    .body(new ApiResponse<>(false, "Employé non trouvé avec id : " + employeId, null));
//        }
//        Optional<ContractEmployeDTO> result = contratEmployeService.getContratEmployeNonArreteParEmploye(employeOpt.get());
//        return ResponseEntity.ok(new ApiResponse<>(true, "Contrats non arrêtés de l'employé", result));
//    }

    @GetMapping("/count/contrats-en-cours/{id}")
    public ResponseEntity<?> getNombreContratsEnCours(@PathVariable Long id) {
        try {
            Optional<Company> optionalCompany = Optional.ofNullable(companyService.getById(id));
            if (optionalCompany.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }
            Long count = contratEmployeService.getNombreContratEmployeEnCoursDeValiditeParEntreprise(optionalCompany.get());
            return ResponseEntity.ok(new ApiResponse<>(true, "Nombre de contrats en cours", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors du comptage des contrats", null));
        }
    }

    @GetMapping("/appercu/{employeId}")
    public ResponseEntity<?> viewContratEmployeGlobalByEmploye(@PathVariable Long employeId) {
        Employe employe = new Employe();    
        employe.setId(employeId);
        // Récupérer les contrats de l’employé (même arrêtés)
        List<ContractEmployeDTO> contrats = contratEmployeService.getContratEmployeParEmploye(employe);

        if (contrats.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Aucun contrat trouvé pour cet employé", null));
        }
        return ResponseEntity.ok(contrats);
    }

//    @GetMapping("/appercu/{idContrat}")
//    public ResponseEntity<?> viewContratEmployeGlobalByContratEmploye(@PathVariable Long idContrat) {
//        // Récupérer le contrat employé par son ID
//        Optional<ContratEmploye> contratOpt = contratEmployeService.findById(idContrat);
//
//        if (contratOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ApiResponse<>(false, "Contrat non trouvé avec l'ID " + idContrat, null));
//        }
//
//        ContratEmploye contrat = contratOpt.get();
//
//        // 🔥 Mapper en DTO (si tu as déjà un mapper)
//        ContractEmployeDTO dto = ContractEmployeMapper.toDTO(contrat);
//
//        return ResponseEntity.ok(new ApiResponse<>(true, "Contrat trouvé", dto));
//    }
//

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<ContractEmployeDTO> existing = contratService.getById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Contrat non trouvé", null), HttpStatus.NOT_FOUND);
            }

            User currentUser = userService.getCurrentUser();
            ContractEmployeDTO contrat = existing.get();
            //contrat.setDeleted_at(LocalDateTime.now());

            contratService.save(contrat);

            auditService.log(
                    "Suppression d’un contrat",
                    "contrats_employes",
                    currentUser.getId(),
                    "Suppression d’un contrat employé par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Contrat supprimé",
                    "Le contrat employé a été supprimé avec succès 🗑️"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Contrat supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression du contrat", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/actifs/{companyId}")
    public ResponseEntity<?> getEmployesActifsParEntreprise(@PathVariable Long companyId) {
        try {
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }

            List<EmployeContratActifDTO> dtos = contratService.getEmployesAvecContratActifParEntreprise(companyId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des employés avec contrat actif récupérée avec succès", dtos));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des employés", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/arreter")
    public ResponseEntity<?> arreterContrat(@RequestBody ArretContratDTO dto) {
        try {
            if (dto.getContratId() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "L'ID du contrat est requis", null));
            }
            if (dto.getMotifId() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "L'ID du motif est requis", null));
            }

            // Récupération du contrat
            Optional<ContratEmploye> optionalContrat = contratEmployeService.findById(dto.getContratId());
            if (optionalContrat.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Contrat introuvable avec l'ID fourni", null));
            }

            ContratEmploye contrat = optionalContrat.get();

            // Vérification si le contrat est déjà arrêté
            if (contrat.isArretContrat()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Le contrat est déjà arrêté", null));
            }

            // Appel du service pour arrêter le contrat
            contrat = contratEmployeService.arreterContrat(dto.getContratId(), dto.getMotifId());

            ContratArreteDTO response = new ContratArreteDTO(
                    contrat.getId(),
                    contrat.getMotif_arret_contrat(),
                    contrat.getDate_arret_contrat(),
                    contrat.getStatus_contrat()
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Contrat arrêté avec succès", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur : " + e.getMessage(), null));
        }
    }

    // 🔹 Préparer un renouvellement
    @GetMapping("/{id}/renouveler")
    public ResponseEntity<ApiResponse<ContratEmployeGlobalDTO>> preparerRenouvellement(@PathVariable Long id) {
        try {
            ContratEmployeGlobalDTO globalDTO = contratEmployeService.preparerRenouvellement(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrat pré-rempli pour renouvellement", globalDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // 🔹 Valider le renouvellement
    @PostMapping("/renouveler")
    public ResponseEntity<ApiResponse<ContratEmploye>> validerRenouvellement(@RequestBody ContratEmployeGlobalDTO globalDTO) {
        try {
            ContratEmploye newContrat = contratEmployeService.validerRenouvellement(globalDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Nouveau contrat créé avec succès", newContrat));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }


    @GetMapping("/by-employe/{employeId}")
    public ResponseEntity<ApiResponse<List<ContractEmployeDTO>>> getContratsByEmploye(@PathVariable Long employeId) {
        try {
            List<ContractEmployeDTO> contrats = contratEmployeService.getContratsByEmployeId(employeId);
            if (contrats.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Aucun contrat trouvé pour cet employé", contrats));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des contrats récupérée avec succès", contrats));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des contrats", null));
        }
    }



    // ==================== CONTRATS ARRÊTÉS ====================

    @GetMapping("/arretes/paginated")
    public ResponseEntity<?> getAllContratsArretesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getAllContratsArretesPaginated(pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats arrêtés paginés récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    @GetMapping("/arretes/entreprise/{companyId}/paginated")
    public ResponseEntity<?> getContratsArretesParEntreprisePaginated(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getContratsArretesParEntreprisePaginated(companyId, pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats arrêtés pour l'entreprise récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

// ==================== CONTRATS EXPIRÉS ====================

    @GetMapping("/expires/paginated")
    public ResponseEntity<?> getAllContratsExpiresPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getAllContratsExpiresPaginated(pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats expirés paginés récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    @GetMapping("/expires/entreprise/{companyId}/paginated")
    public ResponseEntity<?> getContratsExpiresParEntreprisePaginated(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getContratsExpiresParEntreprisePaginated(companyId, pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats expirés pour l'entreprise récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

// ==================== CONTRATS ARRÊTÉS OU EXPIRÉS ====================

    @GetMapping("/arretes-ou-expires/paginated")
    public ResponseEntity<?> getContratsArretesOuExpiresPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getContratsArretesOuExpiresPaginated(pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats arrêtés ou expirés paginés récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    @GetMapping("/arretes-ou-expires/entreprise/{companyId}/paginated")
    public ResponseEntity<?> getContratsArretesOuExpiresParEntreprisePaginated(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            if (companyId == null || !companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractEmployeDTO> contratsPage = contratService.getContratsArretesOuExpiresParEntreprisePaginated(companyId, pageable);

            PaginatedResponse<List<ContractEmployeDTO>> response = new PaginatedResponse<>(
                    true,
                    "Contrats arrêtés ou expirés pour l'entreprise récupérés",
                    contratsPage.getContent(),
                    contratsPage.getNumber(),
                    contratsPage.getSize(),
                    contratsPage.getTotalElements(),
                    contratsPage.getTotalPages(),
                    contratsPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

}
