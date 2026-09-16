package com.tpc.tpcgestpaie.localapp.controller.arh.employe;

import com.tpc.tpcgestpaie.localapp.dto.EmployeUserInfoDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.CreateEmployeRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeResponseDTO;
import com.tpc.tpcgestpaie.localapp.helper.EmployeHelper;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.helper.UserHelper;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.RoleRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import jakarta.transaction.Transactional;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employes")
public class EmployeController {

    private final EmployeService employeService;
    private final EmployeHelper employeHelper;
    private final UserRepository userRepository;
    private final UserHelper userHelper;
    private final RequestHelper requestHelper;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public EmployeController(EmployeService employeService, EmployeHelper employeHelper, UserRepository userRepository, UserHelper userHelper, RequestHelper requestHelper, AuditLogService auditService, NotificationService notificationService, RoleRepository roleRepository, UserService userService) {
        this.employeService = employeService;
        this.employeHelper = employeHelper;
        this.userRepository = userRepository;
        this.userHelper = userHelper;
        this.requestHelper = requestHelper;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    // === ENDPOINTS PAGINÉS ===

    // Liste de tous les employés avec pagination
    @Transactional
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Employe> employePage = employeService.findAll(pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", employePage.getContent());
            responseData.put("currentPage", employePage.getNumber());
            responseData.put("totalItems", employePage.getTotalElements());
            responseData.put("totalPages", employePage.getTotalPages());
            responseData.put("size", employePage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des employés récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des employés: " + e.getMessage(), null));
        }
    }

    // Employés par entreprise avec pagination
    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<ApiResponse<?>> getEmployeesByCompany(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Employe> employees = employeService.findEmployeesByCompany(companyId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", employees.getContent());
            responseData.put("currentPage", employees.getNumber());
            responseData.put("totalItems", employees.getTotalElements());
            responseData.put("totalPages", employees.getTotalPages());
            responseData.put("size", employees.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des employés de l'entreprise récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des employés de l'entreprise: " + e.getMessage(), null));
        }
    }

    // Employés par entreprise avec pagination pour numérisation
    @Transactional
    @GetMapping("/par_entreprise/{companyId}")
    public ResponseEntity<ApiResponse<?>> getEmployeesByCompanyNumerisation(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Employe> employees = employeService.findAllEmployeesByCompany(companyId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", employees.getContent());
            responseData.put("currentPage", employees.getNumber());
            responseData.put("totalItems", employees.getTotalElements());
            responseData.put("totalPages", employees.getTotalPages());
            responseData.put("size", employees.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des employés de l'entreprise récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des employés de l'entreprise: " + e.getMessage(), null));
        }
    }

    // Employés par entreprise avec DTO pour éviter Lazy Loading
    @GetMapping("/entreprise/{companyId}")
    public ResponseEntity<ApiResponse<?>> getEmployesByCompanyNumerisation(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search) {

        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);

            Page<EmployeDTO> employesPage =
                    employeService.getEmployesByCompanyNumerisation(companyId, search, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", employesPage.getContent());
            responseData.put("currentPage", employesPage.getNumber());
            responseData.put("totalItems", employesPage.getTotalElements());
            responseData.put("totalPages", employesPage.getTotalPages());
            responseData.put("size", employesPage.getSize());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Employés récupérés avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des employés: " + e.getMessage(), null));
        }
    }


    // Liste des employés récents avec pagination
    @GetMapping("/recents")
    public ResponseEntity<ApiResponse<?>> getAllRecents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
            Page<Employe> employePage = employeService.getAllEmployesRecents(pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", employePage.getContent());
            responseData.put("currentPage", employePage.getNumber());
            responseData.put("totalItems", employePage.getTotalElements());
            responseData.put("totalPages", employePage.getTotalPages());
            responseData.put("size", employePage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des employés récents récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des employés récents: " + e.getMessage(), null));
        }
    }

    // Employés sans contrat avec pagination
    @GetMapping("/sans-contrat")
    public ResponseEntity<ApiResponse<?>> getEmployesSansContrat(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Employe> employes = employeService.getEmployesSansContrat(pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", employes.getContent());
            responseData.put("currentPage", employes.getNumber());
            responseData.put("totalItems", employes.getTotalElements());
            responseData.put("totalPages", employes.getTotalPages());
            responseData.put("size", employes.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des employés sans contrat récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des employés sans contrat: " + e.getMessage(), null));
        }
    }

    // Récupérer un employé par ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeResponseDTO>> getById(@PathVariable Long id) {
        try {
            EmployeResponseDTO employe = employeService.findByIdAsDTO(id);

            if (employe == null) {
                throw new RuntimeException("Employé introuvable");
            }

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Informations de l'employé récupérées avec succès", employe)
            );

        } catch (RuntimeException e) {
            // Ici on renvoie 404 si l'employé n'est pas trouvé
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            // Erreur serveur pour les autres exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération de l'employé: " + e.getMessage(), null));
        }
    }

    // Créer un nouvel employé
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@RequestBody Employe employe) {
        try {
            User currentUser = userService.getCurrentUser();
            List<ErrorResponse> errors = employeHelper.getInvalidFieldMessages(employe);

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            Employe savedEmploye = employeService.save(employe);

            if (savedEmploye.getId() != null) {
                String ipAddress = requestHelper.getClientIp();
                String userAgent = requestHelper.getUserAgent();
                auditService.log(
                        "Un nouveau employé ajouté",
                        "employes",
                        currentUser.getId(),
                        "Ajout d'un nouveau employé par " + currentUser.getFullName() + " avec le role",
                        currentUser,
                        ipAddress,
                        userAgent
                );
            }

            notificationService.createNotification(
                    currentUser,
                    "👤 Nouveau employé ajouté",
                    "✅ L'utilisateur a été ajouté avec succès. 🎉\n"
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Employé créé avec succès", savedEmploye));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la création de l'employé: " + e.getMessage(), null));
        }
    }

    // Mettre à jour un employé existant
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @RequestBody CreateEmployeRequestDTO updatedEmploye) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<Employe> existing = employeService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé non trouvé avec l'ID: " + id, null));
            }
            Employe saved = employeService.update(id,updatedEmploye,currentUser);

            auditService.log(
                    "Mise à jour d'un employé",
                    "employes",
                    currentUser.getId(),
                    "Mise à jour d'un employé par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Mise à jour effectuée avec succès",
                    "La demande de mise à jour de l'employé a bien été effectuée avec succès"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Employé mis à jour avec succès", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la mise à jour de l'employé: " + e.getMessage(), null));
        }
    }

    // Supprimer un employé
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        try {
            Optional<Employe> existing = employeService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé non trouvé avec l'ID: " + id, null));
            }

            employeService.deleteById(id);

            return ResponseEntity.ok(new ApiResponse<>(true, "Employé supprimé avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la suppression de l'employé: " + e.getMessage(), null));
        }
    }

    // Récupérer les informations d'un employé par user ID
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<?>> getEmployeByUserId(@PathVariable Long userId) {
        try {
            EmployeUserInfoDTO dto = userService.getEmployeInfoByUserId(userId);

            if (dto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Utilisateur ou employé non trouvé pour l'ID: " + userId, null));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Informations de l'employé récupérées avec succès", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération de l'employé: " + e.getMessage(), null));
        }
    }
}