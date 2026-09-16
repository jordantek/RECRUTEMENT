package com.tpc.tpcgestpaie.localapp.controller.users;

import com.tpc.tpcgestpaie.localapp.dto.users.*;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.*;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.EmployeSuperieurService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.users.UserCompanyAccessService;
import com.tpc.tpcgestpaie.localapp.service.users.UserEmployeService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/management")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeService employeService;
    private final UserCompanyAccessService userCompanyAccessService;
    private final UserEmployeService userEmployeService;
    private final ContratEmployeRepository contratEmployeRepository;
    private final EmployeSuperieurService employeSuperieurService;

    // === CRÉATION ===
//    @PostMapping
//    public ResponseEntity<ApiResponse<?>> createUser(
//            @Valid @RequestBody UserCreateRequestDTO userRequest) {
//
//        try {
//            User currentUser = userService.getCurrentUser();
//            if (currentUser == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
//            }
//
//            // Vérifier les doublons
//            if (userRepository.existsByUsername(userRequest.getUsername())) {
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body(new ApiResponse<>(false, "Ce nom d'utilisateur existe déjà", null));
//            }
//
//            if (userRepository.existsByEmail(userRequest.getEmail())) {
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body(new ApiResponse<>(false, "Cet email est déjà utilisé", null));
//            }
//
//            // Vérifier que l'employé existe
//            Optional<Employe> employeOpt = employeRepository.findById(userRequest.getEmployeId());
//            if (employeOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new ApiResponse<>(false, "Employé non trouvé", null));
//            }
//            // Vérifier que l'entreprise existe
//            Optional<Company> companyOpt = companyRepository.findById(userRequest.getCompanyId());
//            if (companyOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new ApiResponse<>(false, "Entreprise non trouvée", null));
//            }
//
//            // Vérifier que l'employé n'a pas déjà un compte
//            if (userRepository.existsByEmployeId(userRequest.getEmployeId())) {
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body(new ApiResponse<>(false, "Cet employé a déjà un compte utilisateur", null));
//            }
//
//            Employe employe = employeOpt.get();
//            Company company = companyOpt.get();
//
//            // Récupérer les rôles
//            Set<Role> roles = new HashSet<>();
//            if (userRequest.getRoleIds() != null && !userRequest.getRoleIds().isEmpty()) {
//                List<Role> rolesList = roleRepository.findAllById(userRequest.getRoleIds());
//                if (rolesList.size() != userRequest.getRoleIds().size()) {
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                            .body(new ApiResponse<>(false, "Un ou plusieurs rôles sont introuvables", null));
//                }
//                roles = new HashSet<>(rolesList);
//            } else {
//                // Rôle par défaut
//                Optional<Role> defaultRole = roleRepository.findByName("EMPLOYE");
//                defaultRole.ifPresent(roles::add);
//            }
//
//            // Récupérer le statut
//            Optional<Status> statusOpt = statusRepository.findByNameOptional(userRequest.getStatus());
//            if (statusOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new ApiResponse<>(false, "Statut non trouvé", null));
//            }
//
//            // Créer l'utilisateur
//            User newUser = new User();
//            newUser.setEmploye(employe);
//            newUser.setCompany(company);
//            newUser.setFullName(employe.getNom() + " " + employe.getPrenom());
//            newUser.setEmail(userRequest.getEmail());
//            newUser.setUsername(userRequest.getUsername());
//            newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
//            newUser.setRoles(roles);
//            newUser.setStatus(statusOpt.get());
//            newUser.setAddedBy(currentUser);
//
//            User savedUser = userRepository.save(newUser);
//
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(new ApiResponse<>(true, "Utilisateur créé avec succès", mapToResponse(savedUser)));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>(false, "Erreur lors de la création de l'utilisateur: " + e.getMessage(), null));
//        }
//    }


    @PostMapping("/full")
    public ResponseEntity<ApiResponse<User>> createOrUpdateUserFull(
            @Valid @RequestBody UserFullRequestDTO dto) {
        try {
            User saved = userEmployeService.createOrUpdateUserWithHierarchy(dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur et hiérarchie enregistrés", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }


    // === LECTURE ===
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable Long userId) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }

            User user = userOpt.get();

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Utilisateur récupéré avec succès", mapToDetailedResponse(user)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de l'utilisateur: " + e.getMessage(), null));
        }
    }


    @GetMapping("/details/{userId}")
    public ResponseEntity<ApiResponse<UserResponseWithHierarchyDTO>> getUser(
            @PathVariable Long userId) {
        try {
            UserResponseWithHierarchyDTO userDto = userEmployeService.getUserWithHierarchy(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur récupéré avec succès", userDto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération de l'utilisateur", null));
        }
    }

    // === MISE À JOUR ===
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequestDTO updateRequest) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }

            User user = userOpt.get();

            // Vérifier les permissions d'accès
            if (!hasAccessToUser(currentUser, user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Accès non autorisé à cet utilisateur", null));
            }

            // Mise à jour des champs de base
            if (updateRequest.getFullName() != null) {
                user.setFullName(updateRequest.getFullName());
            }

            if (updateRequest.getEmail() != null) {
                // Vérifier que l'email n'est pas déjà utilisé par un autre utilisateur
                if (!user.getEmail().equals(updateRequest.getEmail()) &&
                        userRepository.existsByEmailAndIdNot(updateRequest.getEmail(), userId)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ApiResponse<>(false, "Cet email est déjà utilisé par un autre utilisateur", null));
                }
                user.setEmail(updateRequest.getEmail());
            }

            if (updateRequest.getPhone() != null) {
                user.setPhone(updateRequest.getPhone());
            }

            // Mise à jour du statut
            if (updateRequest.getStatus() != null) {
                Optional<Status> statusOpt = statusRepository.findByNameOptional(updateRequest.getStatus());
                if (statusOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Statut non trouvé", null));
                }
                user.setStatus(statusOpt.get());
            }

            // Mise à jour des rôles
            if (updateRequest.getRoleIds() != null && !updateRequest.getRoleIds().isEmpty()) {
                List<Role> rolesList = roleRepository.findAllById(updateRequest.getRoleIds());
                if (rolesList.size() != updateRequest.getRoleIds().size()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Un ou plusieurs rôles sont introuvables", null));
                }
                user.setRoles(new HashSet<>(rolesList));
            }

            // Mise à jour de l'entreprise
            if (updateRequest.getCompanyId() != null) {
                Optional<Company> companyOpt = companyRepository.findById(updateRequest.getCompanyId());
                if (companyOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Entreprise non trouvée", null));
                }
                user.setCompany(companyOpt.get());
            }

            // Mise à jour de l'employé associé
            if (updateRequest.getEmployeId() != null) {
                Optional<Employe> employeOpt = employeRepository.findById(updateRequest.getEmployeId());
                if (employeOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Employé non trouvé", null));
                }

                // Vérifier que l'employé n'a pas déjà un autre compte
                if (!user.getEmploye().getId().equals(updateRequest.getEmployeId()) &&
                        userRepository.existsByEmployeId(updateRequest.getEmployeId())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ApiResponse<>(false, "Cet employé a déjà un compte utilisateur", null));
                }

                user.setEmploye(employeOpt.get());
            }

            // Réinitialisation du mot de passe
            if (Boolean.TRUE.equals(updateRequest.getResetPassword())) {
                if (updateRequest.getNewPassword() == null || updateRequest.getNewPassword().trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse<>(false, "Le nouveau mot de passe est requis", null));
                }
                user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
            }

            user.setUpdatedAt(LocalDateTime.now());
            User updatedUser = userRepository.save(user);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Utilisateur mis à jour avec succès", mapToDetailedResponse(updatedUser)));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), null));
        }
    }

    /**
     * Liste de tous les utilisateurs avec leur hiérarchie
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseWithHierarchyDTO>>> getAllUsers() {
        try {
            List<UserResponseWithHierarchyDTO> users = userEmployeService.getAllUsersWithHierarchy();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des utilisateurs récupérée avec succès", users));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des utilisateurs", null));
        }
    }


    @PutMapping("/user/roles/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUserRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds) {

        try {

            // Vérifier si l'utilisateur à modifier existe
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }

            User user = userOpt.get();

            // Vérifier que roleIds n'est pas vide
            if (roleIds == null || roleIds.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "La liste des rôles ne peut pas être vide", null));
            }

            // Récupérer les rôles
            List<Role> rolesList = roleRepository.findAllById(roleIds);

            // Vérifier que tous les rôles existent
            if (rolesList.size() != roleIds.size()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Un ou plusieurs rôles sont introuvables", null));
            }

            // Mise à jour des rôles uniquement
            user.setRoles(new HashSet<>(rolesList));
            user.setUpdatedAt(LocalDateTime.now());
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Rôles mis à jour avec succès",
                            mapToDetailedResponse(updatedUser))
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la mise à jour des rôles: " + e.getMessage(), null));
        }
    }

    @PutMapping("/user/password/reste/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUserPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {

        try {
            String newPassword = body.get("newPassword");

            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Le nouveau mot de passe est requis", null));
            }

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }

            User user = userOpt.get();

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Mot de passe mis à jour avec succès",
                            null)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la mise à jour du mot de passe : " + e.getMessage(), null));
        }
    }



    // === SUPPRESSION (soft delete) ===
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long userId) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }

            User user = userOpt.get();

            // Vérifier les permissions d'accès
            if (!hasAccessToUser(currentUser, user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Accès non autorisé à cet utilisateur", null));
            }

            // Soft delete
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", userId);
            responseData.put("deletedAt", user.getDeletedAt());
            responseData.put("message", "Utilisateur supprimé avec succès");

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Utilisateur supprimé avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), null));
        }
    }

    // === RÉACTIVATION ===
    @PatchMapping("/{userId}/reactiver")
    public ResponseEntity<ApiResponse<?>> reactivateUser(@PathVariable Long userId) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }

            User user = userOpt.get();

            // Vérifier les permissions d'accès
            if (!hasAccessToUser(currentUser, user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Accès non autorisé à cet utilisateur", null));
            }

            // Réactiver l'utilisateur
            user.setDeletedAt(null);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", userId);
            responseData.put("reactivatedAt", LocalDateTime.now());
            responseData.put("message", "Utilisateur réactivé avec succès");

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Utilisateur réactivé avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la réactivation de l'utilisateur: " + e.getMessage(), null));
        }
    }

    // === LISTE DES UTILISATEURS PAR ENTREPRISE ===
    @GetMapping("/entreprise/{companyId}")
    public ResponseEntity<ApiResponse<?>> getUsersByCompany(@PathVariable Long companyId) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            // Vérifier l'accès à l'entreprise
            List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);
            if (!accessibleCompanyIds.contains(companyId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Accès non autorisé à cette entreprise", null));
            }

            List<UserAvecEmployeDTO> users = userEmployeService.getUsersByCompany(companyId);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("users", users);
            responseData.put("total", users.size());
            responseData.put("companyId", companyId);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des utilisateurs de l'entreprise récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des utilisateurs: " + e.getMessage(), null));
        }
    }

    // === ENDPOINTS EXISTANTS ===

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<?>> getAvailableRoles() {
        try {
            List<Role> roles = roleRepository.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des rôles récupérée", roles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des rôles", null));
        }
    }

    @GetMapping("/statuses")
    public ResponseEntity<ApiResponse<?>> getAvailableStatuses() {
        try {
            List<Status> statuses = statusRepository.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des statuts récupérée", statuses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des statuts", null));
        }
    }

    @GetMapping("/sans-compte/entreprise/{companyId}")
    public ResponseEntity<ApiResponse<?>> getEmployesSansCompteByCompany(
            @PathVariable Long companyId) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            // Vérifier l'accès à l'entreprise
            List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);
            if (!accessibleCompanyIds.contains(companyId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false,
                                "Accès non autorisé à cette entreprise", null));
            }

            EmployesSansCompteParEntrepriseDTO result =
                    employeService.getEmployesSansCompteByCompany(companyId);

            if (result == null) {
                return ResponseEntity.ok(new ApiResponse<>(true,
                        "Aucun employé sans compte dans cette entreprise",
                        Map.of("companyId", companyId, "employes", List.of())));
            }

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des employés sans compte récupérée avec succès", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des employés: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{employeId}/has-compte")
    public ResponseEntity<ApiResponse<?>> checkEmployeHasCompte(@PathVariable Long employeId) {
        try {
            boolean hasCompte = employeService.employeHasUserAccount(employeId);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("employeId", employeId);
            responseData.put("hasCompte", hasCompte);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Vérification effectuée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la vérification: " + e.getMessage(), null));
        }
    }

    // === MÉTHODES UTILITAIRES PRIVÉES ===

    /**
     * Vérifie si l'utilisateur courant a accès à l'utilisateur cible
     */
    private boolean hasAccessToUser(User currentUser, User targetUser) {
        // Un utilisateur peut modifier son propre compte
        if (currentUser.getId().equals(targetUser.getId())) {
            return true;
        }

        // Les administrateurs ont accès à tous les utilisateurs
        if (currentUser.hasRole("ROLE_ADMIN") || currentUser.hasRole("ROLE_SUPER_ADMIN")) {
            return true;
        }

        // Les managers ont accès aux utilisateurs de leur entreprise
        if (currentUser.hasRole("ROLE_MANAGER") &&
                currentUser.getCompany() != null &&
                currentUser.getCompany().getId().equals(targetUser.getCompany().getId())) {
            return true;
        }

        return false;
    }

    /**
     * Mappe un User vers une réponse détaillée
     */
    private Object mapToDetailedResponse(User user) {
        return new Object() {
            public final Long id = user.getId();
            public final String username = user.getUsername();
            public final String email = user.getEmail();
            public final String fullName = user.getFullName();
            public final String phone = user.getPhone();
            public final String status = user.getStatus().getName();
            public final LocalDateTime lastLoginAt = user.getLastLoginAt();
            public final LocalDateTime createdAt = user.getCreatedAt();
            public final LocalDateTime updatedAt = user.getUpdatedAt();
            public final LocalDateTime deletedAt = user.getDeletedAt();

            public final Object employe = user.getEmploye() != null ? new Object() {
                public final Long id = user.getEmploye().getId();
                public final String matricule = user.getEmploye().getMatricule();
                public final String nom = user.getEmploye().getNom();
                public final String prenom = user.getEmploye().getPrenom();
                public final String email = user.getEmploye().getEmail();
                public final String telephone = user.getEmploye().getTelephone();
            } : null;

            public final Object company = user.getCompany() != null ? new Object() {
                public final Long id = user.getCompany().getId();
                public final String name = user.getCompany().getName();
            } : null;

            public final List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            public final Object addedBy = user.getAddedBy() != null ? new Object() {
                public final Long id = user.getAddedBy().getId();
                public final String username = user.getAddedBy().getUsername();
                public final String fullName = user.getAddedBy().getFullName();
            } : null;
        };
    }

    /**
     * Mappe un User vers une réponse simple (pour la création)
     */
    private Object mapToResponse(User user) {
        return new Object() {
            public final Long id = user.getId();
            public final String username = user.getUsername();
            public final String email = user.getEmail();
            public final String fullName = user.getFullName();
            public final String status = user.getStatus().getName();
            public final Long employeId = user.getEmploye().getId();
            public final String employeNom = user.getEmploye().getNom();
            public final String employePrenom = user.getEmploye().getPrenom();
            public final Long companyId = user.getCompany().getId();
            public final String companyName = user.getCompany().getName();
            public final List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
        };
    }
}