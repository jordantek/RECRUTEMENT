package com.tpc.tpcgestpaie.localapp.controller.alertes;

import com.tpc.tpcgestpaie.localapp.dto.alertes.AlerteConfigurationDTO;
import com.tpc.tpcgestpaie.localapp.dto.alertes.AlerteConfigurationRequestDTO;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.alertes.AlerteConfiguration;
import com.tpc.tpcgestpaie.localapp.service.Alertes.AlerteConfigurationService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alertes/mes-configurations")
@RequiredArgsConstructor
public class UserAlerteConfigurationController {

    private final AlerteConfigurationService configurationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getMyConfigurations() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<AlerteConfiguration> configs = configurationService.getUserConfigurations(currentUser);

            // Conversion en DTO
            List<AlerteConfigurationDTO> configDTOs = configs.stream()
                    .map(AlerteConfigurationDTO::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Vos configurations d'alertes", configDTOs));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getMyConfiguration(@PathVariable Long id) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            // Récupérer toutes les configs de l'utilisateur et filtrer
            List<AlerteConfiguration> userConfigs = configurationService.getUserConfigurations(currentUser);
            AlerteConfiguration config = userConfigs.stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (config == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Configuration non trouvée", null));
            }

            AlerteConfigurationDTO configDTO = AlerteConfigurationDTO.fromEntity(config);
            return ResponseEntity.ok(new ApiResponse<>(true, "Configuration récupérée", configDTO));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createMyConfiguration(
            @Valid @RequestBody AlerteConfigurationRequestDTO configRequest) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            // Convertir DTO en entité
            AlerteConfiguration config = configRequest.toEntity();
            config.setUser(currentUser);

            AlerteConfiguration savedConfig = configurationService.saveUserConfiguration(config, currentUser);
            AlerteConfigurationDTO savedConfigDTO = AlerteConfigurationDTO.fromEntity(savedConfig);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Configuration créée avec succès", savedConfigDTO));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateMyConfiguration(
            @PathVariable Long id,
            @Valid @RequestBody AlerteConfigurationRequestDTO configRequest) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            // Convertir DTO en entité
            AlerteConfiguration config = configRequest.toEntity();
            config.setId(id);
            config.setUser(currentUser);

            AlerteConfiguration savedConfig = configurationService.saveUserConfiguration(config, currentUser);
            AlerteConfigurationDTO savedConfigDTO = AlerteConfigurationDTO.fromEntity(savedConfig);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Configuration mise à jour avec succès", savedConfigDTO));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggleMyConfiguration(
            @PathVariable Long id,
            @RequestParam boolean enabled) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            AlerteConfiguration config = configurationService.toggleUserConfiguration(id, currentUser, enabled);
            AlerteConfigurationDTO configDTO = AlerteConfigurationDTO.fromEntity(config);

            String message = enabled ? "Alerte activée" : "Alerte désactivée";
            return ResponseEntity.ok(new ApiResponse<>(true, message, configDTO));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    @PostMapping("/{id}/reset")
    public ResponseEntity<ApiResponse<?>> resetMyConfiguration(@PathVariable Long id) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            AlerteConfiguration config = configurationService.resetToDefault(id, currentUser);
            AlerteConfigurationDTO configDTO = AlerteConfigurationDTO.fromEntity(config);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Configuration réinitialisée aux valeurs par défaut", configDTO));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    @PostMapping("/initialiser")
    public ResponseEntity<ApiResponse<?>> initialiserMesConfigurations() {
        try {
            User currentUser = userService.getCurrentUser();
            List<AlerteConfiguration> configs = configurationService.initializeUserConfigurations(currentUser);
            List<AlerteConfigurationDTO> configDTOs = AlerteConfigurationDTO.fromEntityList(configs);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Configurations initialisées avec succès", configDTOs));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    @GetMapping("/types-alertes")
    public ResponseEntity<ApiResponse<?>> getTypesAlertes() {
        try {
            AlerteConfiguration.TypeAlerte[] types = AlerteConfiguration.TypeAlerte.values();
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Types d'alertes récupérés", types));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
}