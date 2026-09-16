package com.tpc.tpcgestpaie.localapp.controller.admin;

import com.tpc.tpcgestpaie.localapp.dto.*;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.helper.UserHelper;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.Role;
import com.tpc.tpcgestpaie.localapp.model.Status;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.RoleRepository;
import com.tpc.tpcgestpaie.localapp.repository.StatusRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.UserCompanyService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(
        name = "Gestion administrative/Gestion du Personnel (Admin)",
        description = "API d'administration du personnel : création d'utilisateurs, attribution de rôles, gestion des accès et consultation des informations utilisateur-entreprise"
)
@RestController
@RequestMapping("/api/admin/personnel")
public class PersonnelController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserHelper userHelper;
    private final StatusRepository statusRepository;
    private final RequestHelper resquestHelper;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final EmployeRepository employeRepository;
    private final UserCompanyService userCompanyService;

    public PersonnelController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserHelper userHelper,
                               StatusRepository statusRepository, RequestHelper resquestHelper, AuditLogService auditService,
                               NotificationService notificationService, RoleRepository roleRepository, UserService userService,
                               EmployeRepository employeRepository, UserCompanyService userCompanyService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userHelper = userHelper;
        this.statusRepository = statusRepository;
        this.resquestHelper = resquestHelper;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.employeRepository = employeRepository;
        this.userCompanyService = userCompanyService;
    }

    @Operation(
            summary = "Créer un nouveau membre du personnel",
            description = """
                    Enregistre un nouvel utilisateur dans le système avec les informations suivantes :
                    - Nom complet (fullName)
                    - Email (doit être unique)
                    - Mot de passe (minimum 6 caractères)
                    - Rôle (doit exister dans le système)
                    
                    Processus automatique :
                    - Génération d'un username unique
                    - Hachage sécurisé du mot de passe
                    - Attribution du statut "active" par défaut
                    - Enregistrement de l'audit (qui a créé, quand, depuis quelle IP)
                    - Envoi de notifications (à l'utilisateur créé et au créateur)
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Utilisateur enregistré avec succès",
                                      "data": {
                                        "id": 123,
                                        "username": "user4589",
                                        "fullName": "Jean Dupont",
                                        "email": "jean.dupont@example.com",
                                        "roles": ["ROLE_HR"],
                                        "status": "active"
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Erreurs de validation",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Champs manquants",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Champs manquants : fullName, email",
                                              "data": null
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Erreurs de validation",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Erreur de validation",
                                              "data": [
                                                {
                                                  "field": "email",
                                                  "message": "Email invalide ou déjà utilisé"
                                                },
                                                {
                                                  "field": "password",
                                                  "message": "Le mot de passe doit contenir au moins 6 caractères"
                                                },
                                                {
                                                  "field": "role",
                                                  "message": "role invalide"
                                                }
                                              ]
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur lors de l'enregistrement",
                    content = @Content
            )
    })
    @PostMapping("/create")
    public ResponseEntity<?> registerPersonnel(
            @Parameter(
                    description = """
                            Informations du nouveau membre du personnel :
                            - fullName : Nom complet (requis)
                            - email : Adresse email unique (requis)
                            - password : Mot de passe (minimum 6 caractères, requis)
                            - role : Nom du rôle à attribuer (requis)
                            """,
                    required = true,
                    schema = @Schema(
                            type = "object",
                            example = """
                            {
                              "fullName": "Jean Dupont",
                              "email": "jean.dupont@example.com",
                              "password": "SecurePass123",
                              "role": "ROLE_HR"
                            }
                            """
                    )
            )
            @RequestBody Map<String, Object> requestBody) {

        //verification des champs
        List<String> missingFields = resquestHelper.getMissingFields(requestBody, "fullName", "email", "password", "role");
        if (!missingFields.isEmpty()) {
            String errorMessage = "Champs manquants : " + String.join(", ", missingFields);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, errorMessage, null));
        }

        List<ErrorResponse> errors = new ArrayList<>();
        // Validation manuelle
        if (userHelper.isValidFullName((String) requestBody.get("fullName"))) {
            errors.add(new ErrorResponse("fullName", "Le nom complet est requis"));
        }

        if (userHelper.isValidEmail((String) requestBody.get("email"))) {
            errors.add(new ErrorResponse("email", "Email invalide ou déjà utilisé"));
        }

        if (userHelper.isValidPassword((String) requestBody.get("password"))) {
            errors.add(new ErrorResponse("password", "Le mot de passe doit contenir au moins 6 caractères"));
        }

        if (!userHelper.isValidRole((String) requestBody.get("role"))) {
            errors.add(new ErrorResponse("role", "role invalide"));
        }

        // Si erreurs, retourner JSON structuré
        if (!errors.isEmpty()) {
            ApiResponse<List<ErrorResponse>> errorResponse = new ApiResponse<>(
                    false,
                    "Erreur de validation",
                    errors
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
        try {
            //Générer un username unique
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails currentUser_ =  (UserDetails) principal;
            User currentUser = userRepository.findByUsername(currentUser_.getUsername());


            String username = userHelper.generateUsername("user", 4);
            Status defaultStatus = statusRepository.findByName("active");

            //Créer l'utilisateur
            User newUser = new User();
            Role hrRole = roleRepository.findByName(requestBody.get("role").toString()).orElseThrow();
            newUser.setFullName((String) requestBody.get("fullName"));
            newUser.setEmail((String) requestBody.get("email"));
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode((String) requestBody.get("password")));
            newUser.getRoles().add(hrRole);
            newUser.setStatus(defaultStatus);
            newUser.setAddedBy(currentUser);
            // Enregistrer l'utilisateur
            User savedUser = userRepository.save(newUser);

            // Vérifier si l'enregistrement a réussi
            if (savedUser.getId() != null) {
                String ipAddress = resquestHelper.getClientIp(); // Cette méthode récupère l'IP de l'utilisateur
                String userAgent = resquestHelper.getUserAgent(); // Cette méthode récupère le User-Agent de l'utilisateur
                auditService.log(
                        "Un nouveau utilisateur ajouter",
                        "users",
                        currentUser.getId(),
                        "Ajout d'un nouveau utilisateur par" + currentUser.getFullName()+"avec le role"+newUser.getRoles().toString(),
                        currentUser ,
                        ipAddress,
                        userAgent
                );
            }
            //Créer une notification
            notificationService.createNotification(savedUser,"🎉Bienvenue sur TALENT  GEST PAIE", "Vous pouvez maintenant vous connecter avec le nom d'utilisateur "+newUser.getUsername());
            notificationService.createNotification(
                    currentUser,
                    "👤 Nouvel personnel ajouter",
                    "✅ L'utilisateur "+savedUser.getFullName() + "a été ajouté avec succès. 🎉\n"
            );

            //Créer un DTO à retourner
            UserDTO userDTO = new UserDTO(newUser);

            ApiResponse<UserDTO> successResponse = new ApiResponse<>(
                    true,
                    "Utilisateur enregistré avec succès",
                    userDTO
            );
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            // En cas d'erreur
            ApiResponse<Object> errorResponse = new ApiResponse<>(
                    false,
                    "Une erreur est survenue lors de l'enregistrement de l'utilisateur : " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(
            summary = "Lister tous les membres du personnel",
            description = "Récupère la liste complète de tous les utilisateurs enregistrés dans le système avec leurs informations détaillées (nom, email, rôles, statut, etc.)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Liste non vide",
                                            value = """
                                            {
                                              "success": true,
                                              "message": "Liste des utilisateurs récupérée avec succès.",
                                              "data": [
                                                {
                                                  "id": 1,
                                                  "username": "user1234",
                                                  "fullName": "Jean Dupont",
                                                  "email": "jean@example.com",
                                                  "roles": ["ROLE_HR"],
                                                  "status": "active"
                                                },
                                                {
                                                  "id": 2,
                                                  "username": "user5678",
                                                  "fullName": "Marie Martin",
                                                  "email": "marie@example.com",
                                                  "roles": ["ROLE_ADMIN"],
                                                  "status": "active"
                                                }
                                              ]
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Liste vide",
                                            value = """
                                            {
                                              "success": true,
                                              "message": "Aucun utilisateur trouvé.",
                                              "data": []
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/list")
    public ResponseEntity<?> listPersonnel() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            if (users.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Aucun utilisateur trouvé.", users));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des utilisateurs récupérée avec succès.", users));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Erreur lors de la récupération des utilisateurs : " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Attribuer ou modifier le rôle d'un employé",
            description = """
                    Permet de modifier le rôle d'un utilisateur existant associé à un employé.
                    
                    Processus :
                    1. Vérifie que l'employé existe
                    2. Vérifie que le rôle demandé existe
                    3. Vérifie qu'un utilisateur est associé à cet employé
                    4. Supprime l'ancien rôle
                    5. Attribue le nouveau rôle
                    
                    Note: Un utilisateur ne peut avoir qu'un seul rôle à la fois.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Rôle modifié avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Rôle modifié avec succès",
                                      "data": {
                                        "userId": 123,
                                        "username": "user4589",
                                        "email": "jean.dupont@example.com",
                                        "fullName": "Jean Dupont",
                                        "roleName": "ROLE_MANAGER"
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Employé, rôle ou utilisateur introuvable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Employé introuvable",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Employé introuvable avec l'ID 999",
                                              "data": null
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Rôle introuvable",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Rôle introuvable avec l'ID 10",
                                              "data": null
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Utilisateur introuvable",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Utilisateur introuvable pour cet employé",
                                              "data": null
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @Transactional
    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRoleToEmploye(
            @Parameter(
                    description = "Identifiants de l'employé et du rôle à attribuer",
                    required = true,
                    schema = @Schema(
                            implementation = AssignRoleRequestDTO.class,
                            example = """
                            {
                              "employeId": 123,
                              "roleId": 5
                            }
                            """
                    )
            )
            @RequestBody AssignRoleRequestDTO request) {
        try {
            // 1️⃣ Vérifier si l'employé existe
            Optional<Employe> employeOpt = employeRepository.findById(request.getEmployeId());
            if (employeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Employé introuvable avec l'ID " + request.getEmployeId(), null)
                );
            }
            Employe employe = employeOpt.get();

            // 2️⃣ Vérifier si le rôle existe
            Optional<Role> roleOpt = roleRepository.findById(request.getRoleId());
            if (roleOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Rôle introuvable avec l'ID " + request.getRoleId(), null)
                );
            }
            Role role = roleOpt.get();

            // 3️⃣ Vérifier si le User existe pour cet employé
            Optional<User> userOpt = userRepository.findByEmploye(employe);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Utilisateur introuvable pour cet employé", null)
                );
            }
            User user = userOpt.get();

            // 4️⃣ Remplacer le rôle existant
            user.getRoles().clear();  // supprimer l'ancien rôle
            user.getRoles().add(role); // ajouter le nouveau

            // 5️⃣ Sauvegarder
            userRepository.save(user);

            UserResponseDTO response = new UserResponseDTO();
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRoleName(user.getRoles().stream().findFirst().map(Role::getName).orElse(null));

            return ResponseEntity.ok(new ApiResponse<>(true, "Rôle modifié avec succès", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la modification du rôle : " + e.getMessage(), null)
            );
        }
    }

    @Operation(
            summary = "Récupérer tous les rôles disponibles",
            description = "Retourne la liste complète de tous les rôles configurés dans le système (ADMIN, HR, MANAGER, EMPLOYEE, etc.)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des rôles récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Liste des rôles récupérée avec succès",
                                      "data": [
                                        {
                                          "id": 1,
                                          "name": "ROLE_ADMIN"
                                        },
                                        {
                                          "id": 2,
                                          "name": "ROLE_HR"
                                        },
                                        {
                                          "id": 3,
                                          "name": "ROLE_MANAGER"
                                        },
                                        {
                                          "id": 4,
                                          "name": "ROLE_EMPLOYEE"
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        try {
            List<Role> roles = roleRepository.findAll();

            // Convertir les entités Role en DTO
            List<RoleDTO> rolesDTO = roles.stream()
                    .map(role -> new RoleDTO(role.getId(), role.getName()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des rôles récupérée avec succès", rolesDTO)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des rôles", null));
        }
    }

    @Operation(
            summary = "Récupérer un utilisateur avec ses entreprises associées",
            description = "Retourne les informations détaillées d'un utilisateur ainsi que la liste de toutes les entreprises auxquelles il a accès"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur et entreprises récupérés avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Utilisateur et entreprises récupérés avec succès",
                                      "data": {
                                        "userId": 123,
                                        "username": "user4589",
                                        "fullName": "Jean Dupont",
                                        "email": "jean.dupont@example.com",
                                        "companies": [
                                          {
                                            "id": 1,
                                            "name": "Entreprise A",
                                            "code": "ENT001"
                                          },
                                          {
                                            "id": 2,
                                            "name": "Entreprise B",
                                            "code": "ENT002"
                                          }
                                        ]
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur lors de la récupération",
                    content = @Content
            )
    })
    @GetMapping("/{userId}/companies")
    public ResponseEntity<?> getUserWithCompanies(
            @Parameter(
                    description = "Identifiant de l'utilisateur",
                    required = true,
                    example = "123"
            )
            @PathVariable Long userId) {
        try {
            UserWithCompaniesDTO dto = userCompanyService.getUserWithCompanies(userId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Utilisateur et entreprises récupérés avec succès", dto)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération de l'utilisateur et des entreprises", null));
        }
    }
}