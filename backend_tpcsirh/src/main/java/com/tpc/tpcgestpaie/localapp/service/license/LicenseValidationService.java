package com.tpc.tpcgestpaie.localapp.service.license;

import com.tpc.tpcgestpaie.localapp.model.license.LicenseToken;
import com.tpc.tpcgestpaie.localapp.repository.license.LicenseTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class LicenseValidationService {

    private final LicenseTokenRepository licenseTokenRepository;
    private final LicenseTokenService licenseTokenService;

    public LicenseValidationService(LicenseTokenRepository licenseTokenRepository,
                                    LicenseTokenService licenseTokenService) {
        this.licenseTokenRepository = licenseTokenRepository;
        this.licenseTokenService = licenseTokenService;
    }

    /**
     * Vérifier si une licence est active via le token
     */
    /**
     * Vérifier si la licence est active (déchiffre et vérifie la date)
     */
    public boolean isLicenseActive() {
        try {
            Optional<LicenseToken> latestToken = licenseTokenRepository.findLatestToken();
            if (latestToken.isEmpty()) {
                log.warn("⚠️ Aucun token trouvé en base de données");
                return false;
            }

            LicenseToken token = latestToken.get();

            // DÉCHIFFRER pour vérifier la validité
            boolean isValid = licenseTokenService.isEncryptedTokenValid(token.getToken());

            log.info("🔍 Licence active: {}", isValid);
            return isValid;

        } catch (Exception e) {
            log.error("💥 Erreur vérification licence: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Récupérer les données de licence DÉCHIFFRÉES
     */
    public Map<String, Object> getDecryptedLicenseInfo() {
        try {
            Optional<LicenseToken> latestToken = licenseTokenRepository.findLatestToken();
            if (latestToken.isEmpty()) {
                throw new RuntimeException("Aucun token de licence trouvé en base");
            }

            LicenseToken token = latestToken.get();
            log.info("🔍 Token trouvé en base - ID: {}", token.getId());

            // ÉTAPE CRUCIALE : DÉCHIFFRER le token de la base
            Map<String, Object> clearData = licenseTokenService.decryptLicenseData(token.getToken());

            log.info("✅ Données déchiffrées - Société: {}", clearData.get("companyName"));
            return clearData;

        } catch (Exception e) {
            log.error("💥 Erreur lors du déchiffrement: {}", e.getMessage());
            throw new RuntimeException("Impossible de déchiffrer les informations de licence", e);
        }
    }
    /**
     * Récupérer les informations de licence depuis le token
     */

    public boolean isModuleEnabled(String moduleName) {
        try {
            // DÉCHIFFRER les données pour vérifier les modules
            Map<String, Object> clearData = getDecryptedLicenseInfo();

            @SuppressWarnings("unchecked")
            java.util.List<String> modules = (java.util.List<String>) clearData.get("modules");

            boolean enabled = modules != null && modules.contains(moduleName);
            log.info("🔍 Module '{}' activé: {}", moduleName, enabled);

            return enabled;

        } catch (Exception e) {
            log.error("💥 Erreur vérification module {}: {}", moduleName, e.getMessage());
            return false;
        }
    }

    /**
     * Récupérer une information spécifique déchiffrée
     */
    public String getLicenseInformation(String key) {
        try {
            Map<String, Object> clearData = getDecryptedLicenseInfo();
            Object value = clearData.get(key);
            return value != null ? value.toString() : null;

        } catch (Exception e) {
            log.error("💥 Erreur récupération info '{}': {}", key, e.getMessage());
            return null;
        }
    }
    /**
     * Vérifier si un module spécifique est activé
     */

    /**
     * Vérifier si une licence existe (première installation)
     */
    public boolean hasActiveLicense() {
        return licenseTokenRepository.existsAnyToken();
    }
}