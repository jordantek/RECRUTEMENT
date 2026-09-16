package com.tpc.tpcgestpaie.localapp.controller.license;

import com.tpc.tpcgestpaie.localapp.dto.setup.LicenseData;
import com.tpc.tpcgestpaie.localapp.service.license.LicenseActivationService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/license")
@RequiredArgsConstructor
public class LicenseActivationController {

    private final LicenseActivationService licenseActivationService;


    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<?>> activateLicense(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {

        String licenseKey = request.get("licenseKey");

        log.info("🔑 Tentative d'activation avec clé: {}", licenseKey);


        if (licenseKey == null || licenseKey.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Clé de licence manquante")
            );
        }

        String activationIp = getClientIp(httpRequest);

        try {
            log.info("🔑 Tentative d'activation avec clé: {}, IP: {}", licenseKey, activationIp);

            // Récupérer d'abord les données du master pour connaître le type d'installation
            LicenseData licenseDataFromMaster;
            try {
                licenseDataFromMaster = licenseActivationService.getLicenseDataFromMaster(licenseKey);
            } catch (RuntimeException e) {
                // Erreur spécifique du master
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("❌ " + e.getMessage())
                );
            }

            if (licenseDataFromMaster == null) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Clé de license incorrect, contactez  la fonction support")
                );
            }

            boolean activated = licenseActivationService.activateLicense(licenseKey, activationIp);

            if (activated) {
                // Construire la réponse selon le type d'installation
                return buildActivationSuccessResponse(licenseDataFromMaster, licenseKey, activationIp);
            } else {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("❌ Échec de l'activation. Contactez la fonction support")
                );
            }

        } catch (RuntimeException e) {
            log.error("❌ Erreur spécifique lors de l'activation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("❌ " + e.getMessage())
            );
        } catch (Exception e) {
            log.error("💥 Erreur technique lors de l'activation", e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("❌ Erreur technique lors de l'activation")
            );
        }
    }
//    @PostMapping("/activate")
//    public ResponseEntity<ApiResponse<?>> activateLicense(
//            @RequestBody Map<String, String> request,
//            HttpServletRequest httpRequest) {
//
//        String licenseKey = request.get("licenseKey");
//
//        if (licenseKey == null || licenseKey.trim().isEmpty()) {
//            return ResponseEntity.badRequest().body(
//                    ApiResponse.error("Clé de licence manquante")
//            );
//        }
//
//        String activationIp = getClientIp(httpRequest);
//
//        try {
//            log.info("🔑 Tentative d'activation avec clé: {}, IP: {}", licenseKey, activationIp);
//
//            // Récupérer d'abord les données du master pour connaître le type d'installation
//            LicenseData licenseDataFromMaster = licenseActivationService.getLicenseDataFromMaster(licenseKey);
//
//            if (licenseDataFromMaster == null) {
//                return ResponseEntity.badRequest().body(
//                        ApiResponse.error("Licence non trouvée ou invalide sur le serveur master")
//                );
//            }
//
//            boolean activated = licenseActivationService.activateLicense(licenseKey, activationIp);
//
//            if (activated) {
//                // Construire la réponse selon le type d'installation
//                return buildActivationSuccessResponse(licenseDataFromMaster, licenseKey, activationIp);
//            } else {
//                return ResponseEntity.badRequest().body(
//                        ApiResponse.error("Échec de l'activation. Contactez la fonction support")
//                );
//            }
//
//        } catch (RuntimeException e) {
//            log.error("❌ Erreur spécifique lors de l'activation: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(
//                    ApiResponse.error("Erreur: " + e.getMessage())
//            );
//        } catch (Exception e) {
//            log.error("💥 Erreur technique lors de l'activation", e);
//            return ResponseEntity.badRequest().body(
//                    ApiResponse.error("Erreur technique: " + e.getMessage())
//            );
//        }
//    }

    /**
     * Construire la réponse de succès selon le type d'installation
     */
    private ResponseEntity<ApiResponse<?>> buildActivationSuccessResponse(
            LicenseData licenseData, String licenseKey, String activationIp) {

        // Déterminer le type d'installation
        boolean isLocalInstallation = isLocalInstallation(licenseData);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("licenseKey", licenseKey);
        responseData.put("activationIp", activationIp);
        responseData.put("installationType", isLocalInstallation ? "LOCAL" : "ONLINE");
        responseData.put("verifiedByMaster", true);

        // Ajouter les informations de l'entreprise (toujours disponibles depuis le master)
        if (licenseData.getCompanyInfo() != null) {
            responseData.put("company", licenseData.getCompanyInfo().getCompanyName());
            responseData.put("modules", licenseData.getModules());
            responseData.put("expiry", licenseData.getLicenseConfig().getExpiryDate());
            responseData.put("licenseType", licenseData.getLicenseConfig().getLicenseType());
        }

        // Message spécifique selon le type d'installation
        String message = isLocalInstallation ?
                "✅ Licence LOCALE activée avec succès" :
                "✅ Licence EN LIGNE activée avec succès";

        log.info("🎉 {}", message);
        return ResponseEntity.ok(ApiResponse.success(message, responseData));
    }

    @PostMapping("/status")
    public ResponseEntity<ApiResponse<?>> getLicenseStatus(@RequestBody Map<String, String> request) {
        String licenseKey = request.get("licenseKey");

        if (licenseKey == null || licenseKey.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Clé de licence manquante")
            );
        }

        try {
            // Vérifier le statut auprès du MASTER
            boolean isActive = licenseActivationService.isLicenseActive(licenseKey);

            if (isActive) {
                // Récupérer les données complètes depuis le MASTER
                LicenseData licenseData = licenseActivationService.getLicenseDataFromMaster(licenseKey);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("active", true);
                responseData.put("verifiedByMaster", true);

                if (licenseData != null && licenseData.getCompanyInfo() != null) {
                    responseData.put("company", licenseData.getCompanyInfo().getCompanyName());
                    responseData.put("modules", licenseData.getModules());
                    responseData.put("expiry", licenseData.getLicenseConfig().getExpiryDate());
                    responseData.put("licenseType", licenseData.getLicenseConfig().getLicenseType());
                    responseData.put("installationType",
                            isLocalInstallation(licenseData) ? "LOCAL" : "ONLINE");
                }

                return ResponseEntity.ok(ApiResponse.success(
                        "✅ Licence active (vérifiée par le master)",
                        responseData
                ));
            } else {
                return ResponseEntity.ok(ApiResponse.success(
                        "❌ Licence inactive ou invalide",
                        Map.of(
                                "active", false,
                                "verifiedByMaster", true,
                                "message", "La licence n'est pas active sur le serveur master"
                        )
                ));
            }

        } catch (RuntimeException e) {
            log.error("❌ Erreur lors de la vérification du statut: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            log.error("💥 Erreur technique lors de la vérification: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Erreur technique lors de la vérification: " + e.getMessage())
            );
        }
    }

    @GetMapping("/local-status")
    public ResponseEntity<ApiResponse<?>> getLocalLicenseStatus() {
        try {
            // Vérifier le statut local seulement (pour installations LOCALES)
            LicenseData licenseData = licenseActivationService.getCurrentLicense();

            if (licenseData != null) {
                boolean isActive = licenseActivationService.isLicenseValid(licenseData);
                boolean isLocal = isLocalInstallation(licenseData);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("active", isActive);
                responseData.put("verifiedByMaster", false);
                responseData.put("installationType", isLocal ? "LOCAL" : "ONLINE");
                responseData.put("company", licenseData.getCompanyInfo().getCompanyName());
                responseData.put("modules", licenseData.getModules());
                responseData.put("expiry", licenseData.getLicenseConfig().getExpiryDate());

                if (!isLocal) {
                    responseData.put("warning", "Installation ONLINE - Le statut doit être vérifié auprès du master");
                }

                String message = isActive ?
                        "✅ Licence active (vérification locale)" :
                        "❌ Licence expirée ou invalide (vérification locale)";

                return ResponseEntity.ok(ApiResponse.success(message, responseData));

            } else {
                return ResponseEntity.ok(ApiResponse.success(
                        "❌ Aucune licence active localement",
                        Map.of(
                                "active", false,
                                "verifiedByMaster", false,
                                "message", "Aucun token de licence trouvé en base de données"
                        )
                ));
            }

        } catch (Exception e) {
            log.error("💥 Erreur lors de la vérification locale: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(
                    "⚠️ Erreur de vérification locale",
                    Map.of(
                               "active", false,
                            "error", e.getMessage(),
                            "verifiedByMaster", false
                    )
            ));
        }
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<?>> getCurrentLicense() {
        try {
            // Récupérer les données de licence actuelles (priorité au master si disponible)
            LicenseData licenseData = licenseActivationService.getCurrentLicense();

            if (licenseData != null) {
                boolean isLocal = isLocalInstallation(licenseData);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("installationType", isLocal ? "LOCAL" : "ONLINE");
                responseData.put("source", isLocal ? "LOCAL_TOKEN" : "MASTER");
                responseData.put("company", licenseData.getCompanyInfo().getCompanyName());
                responseData.put("modules", licenseData.getModules());
                responseData.put("expiry", licenseData.getLicenseConfig().getExpiryDate());
                responseData.put("licenseType", licenseData.getLicenseConfig().getLicenseType());

                if (!isLocal) {
                    responseData.put("note", "Données récupérées depuis le serveur master");
                }

                return ResponseEntity.ok(ApiResponse.success(
                        "✅ Données de licence récupérées",
                        responseData
                ));
            } else {
                return ResponseEntity.ok(ApiResponse.success(
                        "❌ Aucune donnée de licence disponible",
                        Map.of("available", false)
                ));
            }

        } catch (Exception e) {
            log.error("💥 Erreur récupération données licence: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Erreur récupération données: " + e.getMessage())
            );
        }
    }

    @PostMapping("/test-connection")
    public ResponseEntity<ApiResponse<?>> testConnection(@RequestBody Map<String, String> request) {
        String licenseKey = request.get("licenseKey");

        if (licenseKey == null || licenseKey.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Clé de licence manquante pour le test")
            );
        }

        try {
            // Tester la connexion en tentant de récupérer les données du master
            LicenseData licenseData = licenseActivationService.getLicenseDataFromMaster(licenseKey);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("connected", true);
            responseData.put("masterReachable", true);

            if (licenseData != null) {
                responseData.put("licenseValid", true);
                responseData.put("company", licenseData.getCompanyInfo().getCompanyName());
                responseData.put("installationType",
                        isLocalInstallation(licenseData) ? "LOCAL" : "ONLINE");
            } else {
                responseData.put("licenseValid", false);
            }

            return ResponseEntity.ok(ApiResponse.success(
                    "✅ Connexion à la plateforme master réussie",
                    responseData
            ));

        } catch (RuntimeException e) {
            log.error("❌ Erreur connexion master: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(
                    "❌ Impossible de se connecter à la plateforme master",
                    Map.of(
                            "connected", false,
                            "masterReachable", false,
                            "error", e.getMessage()
                    )
            ));
        } catch (Exception e) {
            log.error("💥 Erreur test connexion: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(
                    "❌ Erreur de connexion",
                    Map.of(
                            "connected", false,
                            "masterReachable", false,
                            "error", e.getMessage()
                    )
            ));
        }
    }

    @GetMapping("/installation-info")
    public ResponseEntity<ApiResponse<?>> getInstallationInfo() {
        try {
            // Récupérer les informations sur le type d'installation actuelle
            LicenseData licenseData = licenseActivationService.getCurrentLicense();

            Map<String, Object> responseData = new HashMap<>();

            if (licenseData != null) {
                boolean isLocal = isLocalInstallation(licenseData);
                responseData.put("installationType", isLocal ? "LOCAL" : "ONLINE");
                responseData.put("hasLocalToken", isLocal);
                responseData.put("company", licenseData.getCompanyInfo().getCompanyName());
                responseData.put("licenseType", licenseData.getLicenseConfig().getLicenseType());
            } else {
                responseData.put("installationType", "NONE");
                responseData.put("hasLocalToken", false);
                responseData.put("message", "Aucune licence activée");
            }

            return ResponseEntity.ok(ApiResponse.success(
                    "✅ Informations d'installation récupérées",
                    responseData
            ));

        } catch (Exception e) {
            log.error("💥 Erreur récupération info installation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Erreur: " + e.getMessage())
            );
        }
    }

    /**
     * Vérifier si c'est une installation LOCALE
     */
    private boolean isLocalInstallation(LicenseData licenseData) {
        if (licenseData == null) {
            return false;
        }

        // Vérifier dans LicenseData.installationType
        if (licenseData.getLicenseConfig().getInstallationType() != null) {
            return licenseData.getLicenseConfig().getInstallationType().name().equals("LOCAL");
        }

        // Vérifier dans LicenseConfig.installationType
        if (licenseData.getLicenseConfig() != null &&
                licenseData.getLicenseConfig().getInstallationType() != null) {
            return "LOCAL".equalsIgnoreCase(
                    String.valueOf(licenseData.getLicenseConfig().getInstallationType())
            );
        }

        // Par défaut, considérer comme LOCAL
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        try {
            String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader != null && !xfHeader.isEmpty()) {
                return xfHeader.split(",")[0].trim();
            }

            String realIp = request.getHeader("X-Real-IP");
            if (realIp != null && !realIp.isEmpty()) {
                return realIp.trim();
            }

            return request.getRemoteAddr();

        } catch (Exception e) {
            log.warn("⚠️ Erreur lors de la récupération de l'IP client: {}", e.getMessage());
            return request.getRemoteAddr();
        }
    }
}