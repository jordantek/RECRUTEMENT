package com.tpc.tpcgestpaie.localapp.controller.license;

import com.tpc.tpcgestpaie.localapp.service.license.LicenseValidationService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/license-token")
@RequiredArgsConstructor
public class LicenseTokenController {

    private final LicenseValidationService licenseValidationService;

    /**
     * Informations complètes de la licence (déchiffrées)
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLicenseInfo() {
        try {
            boolean isActive = licenseValidationService.isLicenseActive();
            Map<String, Object> clearData = licenseValidationService.getDecryptedLicenseInfo();

            clearData.put("isActive", isActive);

            log.info("📄 Informations licence récupérées avec succès");
            return ResponseEntity.ok(
                    ApiResponse.success("Informations licence récupérées", clearData)
            );

        } catch (Exception e) {
            log.error("❌ Erreur récupération licence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            "Impossible de récupérer les informations de la licence",
                            "ERR_LICENSE_INFO",
                            Map.of()
                    ));
        }
    }

    /**
     * Statut simplifié de la licence
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLicenseStatus() {
        try {
            boolean isActive = licenseValidationService.isLicenseActive();

            Map<String, Object> response = Map.of(
                    "active", isActive,
                    "companyName", licenseValidationService.getLicenseInformation("companyName"),
                    "expiryDate", licenseValidationService.getLicenseInformation("expiryDate"),
                    "checkedAt", LocalDateTime.now().toString()
            );

            return ResponseEntity.ok(
                    ApiResponse.success("Statut de la licence", response)
            );

        } catch (Exception e) {
            log.error("❌ Erreur vérification statut licence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            "Erreur lors de la vérification du statut de la licence",
                            "ERR_LICENSE_STATUS",
                            Map.of()
                    ));
        }
    }

    /**
     * Vérification d’un module spécifique
     */
    @GetMapping("/check-module/{moduleName}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkModule(
            @PathVariable String moduleName
    ) {
        try {
            boolean isEnabled = licenseValidationService.isModuleEnabled(moduleName);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Statut du module",
                            Map.of(
                                    "module", moduleName,
                                    "enabled", isEnabled
                            )
                    )
            );

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Module inconnu : {}", moduleName);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                            "Module inconnu : " + moduleName,
                            "ERR_MODULE_UNKNOWN",
                            Map.of("module", moduleName)
                    ));

        } catch (Exception e) {
            log.error("❌ Erreur vérification module {}", moduleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            "Erreur lors de la vérification du module",
                            "ERR_MODULE_CHECK",
                            Map.of()
                    ));
        }
    }
}
