package com.tpc.tpcgestpaie.localapp.controller.setup;

import com.tpc.tpcgestpaie.localapp.dto.setup.InstallationRequest;
import com.tpc.tpcgestpaie.localapp.dto.setup.InstallationResult;
import com.tpc.tpcgestpaie.localapp.dto.setup.InstallationStatus;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.service.setup.InstallationService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/installation")
public class InstallationController {

    private final InstallationService installationService;
    private final UserRepository userRepository;

    public InstallationController(InstallationService installationService,
                                  UserRepository userRepository) {
        this.installationService = installationService;
        this.userRepository = userRepository;
    }

    /**
     * Vérifier le statut d'installation
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<InstallationStatus>> getInstallationStatus() {
        try {
            boolean installationRequired = installationService.isInstallationRequired();

            // ⭐ CORRECTION : Utiliser userRepository au lieu de User
            Optional<User> defaultUser = Optional.ofNullable(userRepository.findByUsername("admin"));
            boolean hasDefaultUser = defaultUser.isPresent();

            InstallationStatus status = new InstallationStatus();
            status.setInstallationRequired(installationRequired);
            status.setHasDefaultUser(hasDefaultUser);
            status.setDefaultUsername("admin");
            status.setMessage(installationRequired ?
                    "Installation requise" : "Logiciel déjà installé");

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Statut d'installation", status)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
    /**
     * Effectuer l'installation initiale
     */
    @PostMapping("/perform")
    public ResponseEntity<ApiResponse<InstallationResult>> performInstallation(
            @RequestBody InstallationRequest request) {
        try {
            // Validation des données requises
            if (request.getCompanyName() == null || request.getCompanyName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Le nom de l'entreprise est requis", null));
            }

            if (request.getAdminFullName() == null || request.getAdminFullName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Le nom de l'administrateur est requis", null));
            }

            InstallationResult result = installationService.performInitialInstallation(request);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Installation réussie", result)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de l'installation: " + e.getMessage(), null));
        }
    }

    /**
     * Installation rapide avec valeurs par défaut
     */
    @PostMapping("/quick-install")
    public ResponseEntity<ApiResponse<InstallationResult>> quickInstall() {
        try {
            InstallationRequest request = new InstallationRequest();
            request.setCompanyName("Entreprise Client");
            request.setCompanyEmail("contact@entreprise.com");
            request.setCompanyPhone("+229 97 00 00 00");
            request.setCompanyAddress("Adresse de l'entreprise");
            request.setCompanyCountry("Bénin");
            request.setAdminFullName("Administrateur");
            request.setAdminEmail("admin@entreprise.com");
            request.setAdminPassword("admin123");

            InstallationResult result = installationService.performInitialInstallation(request);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Installation rapide réussie", result)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
}
