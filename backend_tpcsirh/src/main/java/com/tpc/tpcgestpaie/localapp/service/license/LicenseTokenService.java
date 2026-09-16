package com.tpc.tpcgestpaie.localapp.service.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LicenseTokenService {

    private static final String ALGORITHM = "AES";

    @Value("${license.encryption.key:MySuperSecretKey123}")
    private String encryptionKey;

    private final ObjectMapper objectMapper;

    public LicenseTokenService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * CHIFFRER : Prend les données en clair → retourne le token chiffré
     */
    public String encryptLicenseData(Map<String, Object> clearData) {
        try {
            log.info("🔒 Début chiffrement des données de licence...");

            // Convertir les données claires en JSON
            String jsonData = objectMapper.writeValueAsString(clearData);
            log.debug("📝 Données claires à chiffrer: {}", jsonData);

            // Chiffrer les données
            String encryptedToken = encrypt(jsonData);

            log.info("✅ Données chiffrées avec succès - Taille: {} caractères", encryptedToken.length());
            return encryptedToken;

        } catch (Exception e) {
            log.error("💥 Erreur lors du chiffrement des données: {}", e.getMessage());
            throw new RuntimeException("Erreur de chiffrement", e);
        }
    }

    /**
     * DÉCHIFFRER : Prend le token chiffré → retourne les données en clair
     */
    public Map<String, Object> decryptLicenseData(String encryptedToken) {
        try {
            log.info("🔓 Début déchiffrement du token...");

            // Déchiffrer les données
            String jsonData = decrypt(encryptedToken);
            log.debug("📄 Données déchiffrées: {}", jsonData);

            // Convertir JSON en Map
            Map<String, Object> clearData = objectMapper.readValue(jsonData, Map.class);

            log.info("✅ Données déchiffrées avec succès");
            return clearData;

        } catch (Exception e) {
            log.error("💥 Erreur lors du déchiffrement: {}", e.getMessage());
            throw new RuntimeException("Token invalide ou corrompu", e);
        }
    }

    /**
     * Méthode de chiffrement AES
     */
    private String encrypt(String clearData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(getEncryptionKeyBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(clearData.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Méthode de déchiffrement AES
     */
    private String decrypt(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(getEncryptionKeyBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    /**
     * Préparer la clé de chiffrement (16, 24 ou 32 bytes)
     */
    private byte[] getEncryptionKeyBytes() {
        String key = encryptionKey;
        if (key.length() < 16) {
            key = String.format("%-16s", key).substring(0, 16);
        } else if (key.length() < 24) {
            key = String.format("%-24s", key).substring(0, 24);
        } else if (key.length() < 32) {
            key = String.format("%-32s", key).substring(0, 32);
        } else {
            key = key.substring(0, 32);
        }
        return key.getBytes();
    }

    /**
     * Construire les données CLAIRES pour la licence
     */
    public Map<String, Object> buildClearLicenseData(String licenseKey,
                                                     String companyName,
                                                     com.tpc.tpcgestpaie.localapp.dto.setup.LicenseData licenseData) {
        Map<String, Object> clearData = new HashMap<>();

        // Données en CLAIR avant chiffrement
        clearData.put("licenseKey", licenseKey);
        clearData.put("companyName", companyName);
        clearData.put("modules", licenseData.getModules());
        clearData.put("licenseType", licenseData.getLicenseConfig().getLicenseType());
        clearData.put("durationMonths", licenseData.getLicenseConfig().getDurationMonths());
        clearData.put("expiryDate", licenseData.getLicenseConfig().getExpiryDate().toString());
        clearData.put("activationDate", java.time.LocalDateTime.now().toString());
        clearData.put("version", "1.0");

        log.debug("📋 Données claires construites: {} éléments", clearData.size());
        return clearData;
    }

    /**
     * Vérifier si un token chiffré est valide (non expiré)
     */
    public boolean isEncryptedTokenValid(String encryptedToken) {
        try {
            // DÉCHIFFRER pour vérifier
            Map<String, Object> clearData = decryptLicenseData(encryptedToken);
            String expiryDateStr = (String) clearData.get("expiryDate");
            java.time.LocalDate expiryDate = java.time.LocalDate.parse(expiryDateStr);

            boolean isValid = expiryDate.isAfter(java.time.LocalDate.now());
            log.info("🔍 Token valide: {}", isValid);
            return isValid;

        } catch (Exception e) {
            log.error("❌ Erreur validation token: {}", e.getMessage());
            return false;
        }
    }
}