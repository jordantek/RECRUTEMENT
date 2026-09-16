package com.tpc.tpcgestpaie.localapp.controller;


import com.tpc.tpcgestpaie.localapp.config.JwtUtils;
import com.tpc.tpcgestpaie.localapp.dto.UserDTO;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.helper.UserHelper;
import com.tpc.tpcgestpaie.localapp.model.Role;
import com.tpc.tpcgestpaie.localapp.model.Status;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.AuditLogRepository;
import com.tpc.tpcgestpaie.localapp.repository.RoleRepository;
import com.tpc.tpcgestpaie.localapp.repository.StatusRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import com.tpc.tpcgestpaie.localapp.service.CustomUserDetailsService;

import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthentificationController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserHelper userHelper;
    private final StatusRepository statusRepository;
    private final RequestHelper resquestHelper;
    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final RoleRepository roleRepository;

    public AuthentificationController(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      AuthenticationManager authenticationManager,
                                      JwtUtils jwtUtils,
                                      CustomUserDetailsService customUserDetailsService,
                                      UserHelper userHelper,
                                      StatusRepository statusRepository,
                                      RequestHelper resquestHelper,
                                      AuditLogRepository auditLogRepository,
                                      AuditLogService auditService,
                                      NotificationService notificationService,
                                      RoleRepository roleRepository
                                      ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
        this.userHelper = userHelper;
        this.statusRepository = statusRepository;
        this.resquestHelper = resquestHelper;
        this.auditLogRepository = auditLogRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> requestBody) {

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
            String username = userHelper.generateUsername("user", 4);
            Status defaultStatus = statusRepository.findByName("active");

            //Créer l'utilisateur
            User newUser = new User();
            Role hrRole = roleRepository.findByName("ROLE_SUPER_ADMIN").orElseThrow();
            newUser.setFullName((String) requestBody.get("fullName"));
            newUser.setEmail((String) requestBody.get("email"));
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode((String) requestBody.get("password")));
            newUser.setRole("admin");
            newUser.getRoles().add(hrRole);
            newUser.setStatus(defaultStatus);

            // Enregistrer l'utilisateur
            User savedUser = userRepository.save(newUser);

            // Vérifier si l'enregistrement a réussi
            if (savedUser.getId() != null) {
                String ipAddress = resquestHelper.getClientIp(); // Cette méthode récupère l'IP de l'utilisateur
                String userAgent = resquestHelper.getUserAgent(); // Cette méthode récupère le User-Agent de l'utilisateur
                auditService.log(
                        "Ajout/Inscription d'un utilisateur",
                        "users",
                        savedUser.getId(),
                        "Ajout/Inscription d'un utilisateur",
                        savedUser,
                        ipAddress,
                        userAgent
                );
            }
            //Créer une notification
            notificationService.createNotification(savedUser,"Bienvenue sur TALENT  GEST PAIE", "Vous pouvez maintenant vous connecter");
            //Créer un DTO à retourner
            UserDTO userDTO = new UserDTO(newUser);
            ApiResponse<UserDTO> successResponse = new ApiResponse<>(
                    true,
                    "Utilisateur enregistré avec succès",
                    userDTO
            );
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            // En cas d’erreur
            ApiResponse<Object> errorResponse = new ApiResponse<>(
                    false,
                    "Une erreur est survenue lors de l'enregistrement de l'utilisateur : " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody Map<String, Object> userData,
            HttpServletRequest request
    ) {
        try {
            // Vérification de la présence des données
            if (userData == null || !userData.containsKey("username") || !userData.containsKey("password")) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Nom d'utilisateur ou mot de passe manquant", null));
            }
            String username = Objects.toString(userData.get("username"), null);
            String password = Objects.toString(userData.get("password"), null);

            if (username == null || password == null || username.isBlank() || password.isBlank()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Nom d'utilisateur ou mot de passe invalide", null));
            }

            System.out.println("Tentative de connexion pour l'utilisateur: " + username);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            if (authentication.isAuthenticated()) {
                // Générer le token JWT
                String token = jwtUtils.generateJwtToken(username);

                // Récupérer l'utilisateur connecté depuis la BDD
                User user = userRepository.findByUsernameWithStatus(username);
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Utilisateur introuvable", null));
                }

                // Mettre à jour la dernière connexion
                String ipAddress = resquestHelper.getClientIp();
                String userAgent = resquestHelper.getUserAgent();

                user.setLastLoginAt(LocalDateTime.now());
                user.setLastLoginIp(ipAddress);
                user.setAuthToken(token);
                userRepository.save(user);

                // Audit et notification
                auditService.log(
                        "Connexion d'un utilisateur",
                        "users",
                        user.getId(),
                        "Connexion réussie",
                        user,
                        ipAddress,
                        userAgent
                );

                notificationService.createNotification(
                        user,
                        "Connexion réussie",
                        "Vous vous êtes connecté avec succès"
                );

                // Réponse de succès
                UserDTO user_info = new UserDTO(user);
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", token);
                authData.put("type", "Bearer");
                authData.put("user", user_info);

                return ResponseEntity.ok(new ApiResponse<>(true, "Authentification réussie", authData));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Identifiants invalides", null));

        } catch (AuthenticationException ex) {
            System.out.println("Échec de l'authentification: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Identifiants incorrects", null));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String username = jwtUtils.getUsernameFromRequest(request);

        if (username != null) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                // Notification ou log d’audit
                notificationService.createNotification(
                        user,
                        "Déconnexion",
                        "Vous vous êtes déconnecté avec succès depuis l'IP : "
                );
            }
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Déconnecté avec succès", null));
    }

}