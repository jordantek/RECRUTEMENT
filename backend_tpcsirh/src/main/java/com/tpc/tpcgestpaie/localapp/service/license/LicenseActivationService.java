package com.tpc.tpcgestpaie.localapp.service.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Role;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.dto.setup.LicenseData;
import com.tpc.tpcgestpaie.localapp.model.license.LicenseToken;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.RoleRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.repository.license.LicenseTokenRepository;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class LicenseActivationService {
    @Value("${license.master.url:http://localhost:8087}")
    private String licenseMasterUrl;

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final LicenseTokenRepository licenseTokenRepository;
    private final LicenseTokenService licenseTokenService;
    private final RoleRepository roleRepository;

    public LicenseActivationService(CompanyRepository companyRepository,
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    ObjectMapper objectMapper,
                                    RestTemplate restTemplate,
                                    LicenseTokenRepository licenseTokenRepository,
                                    LicenseTokenService licenseTokenService, RoleRepository roleRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.licenseTokenRepository = licenseTokenRepository;
        this.licenseTokenService = licenseTokenService;
        this.roleRepository = roleRepository;
    }

    /**
     * Activer une licence (pour les deux types d'installation)
     */
    public boolean activateLicense(String licenseKey, String activationIp) {
        LicenseData licenseData = null;
        try {
            log.info("🔑 Début processus d'activation atomique: {}", licenseKey);

            // ÉTAPE 1: Vérifier auprès du master si la licence peut être activée
            licenseData = callMasterForPreActivation(licenseKey);

            if (licenseData == null) {
                log.error(" Licence non valide pour activation");
                return false;
            }

            // ÉTAPE 2: Vérifier les contraintes locales AVANT activation
            if (!checkLocalConstraints(licenseData)) {
                log.error(" Contraintes locales non respectées - annulation");
                return false;
            }

            // ÉTAPE 3: Appeler le master pour activation définitive
            boolean masterActivated = callMasterForFinalActivation(licenseKey, activationIp);
            if (!masterActivated) {
                log.error(" Échec activation sur le master - annulation");
                return false;
            }

            // ÉTAPE 4: Créer les entités locales selon le type d'installation
            createLocalEntities(licenseData, licenseKey);

            log.info("🎉 Activation atomique terminée avec succès pour: {}",
                    licenseData.getCompanyInfo().getCompanyName());
            return true;

        } catch (Exception e) {
            log.error(" Échec activation atomique pour la clé {}: {}", licenseKey, e.getMessage());
            return false;
        }
    }
    /**
     * Créer les entités locales selon le type d'installation
     */
    private void createLocalEntities(LicenseData licenseData, String licenseKey) {
        try {
            // Créer Company (TOUJOURS nécessaire pour les deux types)

            Company company = createCompany(licenseData.getCompanyInfo());
            company.setLicenseType(licenseData.getLicenseConfig().getLicenseType());
            log.info("🏢 Entreprise créée: {}", company.getName());

            // Créer User Admin (TOUJOURS nécessaire pour les deux types)
            User adminUser = createAdminUser(licenseData.getContactPersons(), company);
            log.info("👤 Admin créé: {}", adminUser.getEmail());

            // Créer le token de licence UNIQUEMENT pour les installations LOCALES
            if (isLocalInstallation(licenseData)) {
                createLicenseToken(licenseKey, licenseData, company.getName());
                log.info("🔐 Token de licence créé (Installation LOCALE)");
            } else {
                log.info("🌐 Installation EN LIGNE - Pas de token local nécessaire");
            }

        } catch (Exception e) {
            log.error("💥 Erreur lors de la création des entités locales: {}", e.getMessage());
            throw new RuntimeException("Erreur création entités locales", e);
        }
    }

    /**
     * Créer un token de licence chiffré
     */
    private void createLicenseToken(String licenseKey, LicenseData licenseData, String companyName) {
        try {
            log.info("🔐 Création token chiffré pour: {}", companyName);

            // Construire les données CLAIRES
            Map<String, Object> clearData = licenseTokenService.buildClearLicenseData(
                    licenseKey,
                    companyName,
                    licenseData
            );

            log.debug("📝 Données claires avant chiffrement: {} éléments", clearData.size());

            // CHIFFRER les données
            String encryptedToken = licenseTokenService.encryptLicenseData(clearData);

            log.debug("🔒 Token chiffré généré ({} caractères)", encryptedToken.length());

            // Sauvegarder le CHIFFRÉ dans la base
            LicenseToken licenseToken = new LicenseToken();
            licenseToken.setToken(encryptedToken);

            LicenseToken savedToken = licenseTokenRepository.save(licenseToken);
            log.info("💾 Token CHIFFRÉ sauvegardé en base - ID: {}", savedToken.getId());

        } catch (Exception e) {
            log.error("💥 Erreur lors de la création du token chiffré: {}", e.getMessage());
            throw new RuntimeException("Erreur création token licence", e);
        }
    }
    /**
     * Vérifier si l'installation est de type LOCAL
     */
    private boolean isLocalInstallation(LicenseData licenseData) {
        try {
            // Vérifier d'abord dans LicenseConfig
            if (licenseData.getLicenseConfig() != null &&
                    licenseData.getLicenseConfig().getInstallationType() != null) {

                String installationType = String.valueOf(licenseData.getLicenseConfig().getInstallationType());
                boolean isLocal = "LOCAL".equalsIgnoreCase(installationType);

                log.info("🏠 Type d'installation détecté: {} -> LOCAL: {}", installationType, isLocal);
                return isLocal;
            }

            // Par défaut, considérer comme LOCAL si non spécifié
            log.warn("⚠️ Type d'installation non spécifié, considéré comme LOCAL par défaut");
            return true;

        } catch (Exception e) {
            log.error(" Erreur vérification type d'installation: {}", e.getMessage());
            // En cas d'erreur, considérer comme LOCAL par sécurité
            return true;
        }
    }

    /**
     * Vérifier les contraintes locales avant activation
     */
    private boolean checkLocalConstraints(LicenseData licenseData) {
        try {
            String rccm = licenseData.getCompanyInfo().getRccm();
            String email = licenseData.getContactPersons().getAdminEmail();

            // Vérifier si entreprise existe déjà
            if (companyRepository.findByRccm(rccm) != null) {
                log.error(" Contrainte locale: Entreprise avec RCCM {} existe déjà", rccm);
                return false;
            }

            // Vérifier si utilisateur existe déjà
            if (userRepository.findByEmail(email).isPresent()) {
                log.error(" Contrainte locale: Utilisateur avec email {} existe déjà", email);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error(" Erreur vérification contraintes locales: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Pré-vérification auprès du master
     */
    private LicenseData callMasterForPreActivation(String licenseKey) {
        try {
            log.info("📞 Pré-vérification auprès du master");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("{\"licenseKey\": \"%s\"}", licenseKey);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                    licenseMasterUrl + "/api/license/pre-activate",
                    request,
                    ApiResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ApiResponse apiResponse = response.getBody();

                if (apiResponse.isSuccess()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseData = (Map<String, Object>) apiResponse.getData();

                    if (responseData != null && responseData.containsKey("licenseData")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> licenseResult = (Map<String, Object>) responseData.get("licenseData");

                        String jsonData = objectMapper.writeValueAsString(licenseResult);
                        LicenseData licenseData = objectMapper.readValue(jsonData, LicenseData.class);

                        log.info("✅ Pré-vérification master réussie pour: {}",
                                licenseData.getCompanyInfo().getCompanyName());
                        return licenseData;
                    }
                } else {
                    // Le master a répondu avec une erreur métier
                    String errorMessage = apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Licence non valide pour activation";
                    log.warn("⚠️ Master a refusé la pré-activation: {}", errorMessage);
                    return null;
                }
            }

            log.error(" Échec pré-vérification master - Statut HTTP: {}", response.getStatusCode());
            return null;

        } catch (HttpClientErrorException e) {
            String errorMessage = extractErrorMessageFromMaster(e.getResponseBodyAsString());
            log.warn("⚠️ Erreur client lors de la pré-vérification: {}", errorMessage);
            return null;

        } catch (Exception e) {
            log.error("💥 Erreur pré-vérification master: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Activation finale auprès du master
     */
    private boolean callMasterForFinalActivation(String licenseKey, String activationIp) {
        try {
            log.info("📞 Activation finale auprès du master");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("{\"licenseKey\": \"%s\"}", licenseKey);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                    licenseMasterUrl + "/api/license/verify-activate",
                    request,
                    ApiResponse.class
            );

            boolean success = response.getStatusCode().is2xxSuccessful() &&
                    response.getBody() != null &&
                    response.getBody().isSuccess();

            if (success) {
                log.info("✅ Activation finale master réussie");
            } else {
                log.error(" Échec activation finale master");
            }

            return success;

        } catch (Exception e) {
            log.error("💥 Erreur activation finale master: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Créer l'entreprise
     */
    private Company createCompany(com.tpc.tpcgestpaie.localapp.dto.setup.CompanyInfo companyInfo) {
        try {
            Company company = new Company();
            company.setName(companyInfo.getCompanyName());
            company.setEmail(companyInfo.getCompanyEmail());
            company.setPhone(companyInfo.getCompanyPhone());
            company.setAddress(companyInfo.getCompanyAddress());
            company.setCountry(companyInfo.getCompanyCountry());
            company.setRccm(companyInfo.getRccm());
            company.setEstEntreprisePrincipale(true);
            company.setIfu(companyInfo.getIfu());
            company.setCreatedAt(LocalDateTime.now());

            Company savedCompany = companyRepository.save(company);
            log.debug("🏢 Entreprise créée avec ID: {}", savedCompany.getId());
            return savedCompany;

        } catch (Exception e) {
            log.error("💥 Erreur lors de la création de l'entreprise: {}", e.getMessage());
            throw new RuntimeException("Erreur création entreprise: " + e.getMessage(), e);
        }
    }

    /**
     * Créer l'utilisateur admin
     */

    private User createAdminUser(com.tpc.tpcgestpaie.localapp.dto.setup.ContactPersons contacts, Company company) {
        try {

            Role role = roleRepository.findByName("ROLE_SUPER_ADMIN").orElseThrow();
            User admin = new User();
            admin.setFullName(contacts.getAdminFullName());
            admin.setEmail(contacts.getAdminEmail());
            admin.setPhone(contacts.getAdminPhone());
            admin.setPassword(passwordEncoder.encode(contacts.getAdminPassword()));
            admin.setCompany(company);
            admin.setRole("ROLE_SUPER_ADMIN");
            admin.setUsername(contacts.getAdminEmail());
            admin.setCreatedAt(LocalDateTime.now());
            admin.getRoles().add(role);
            User savedUser = userRepository.save(admin);
            log.debug("👤 Utilisateur admin créé avec ID: {}", savedUser.getId());
            return savedUser;

        } catch (Exception e) {
            log.error("💥 Erreur lors de la création de l'utilisateur admin: {}", e.getMessage());
            throw new RuntimeException("Erreur création utilisateur admin: " + e.getMessage(), e);
        }
    }

    /**
     * Récupérer la licence actuelle depuis le token en base (UNIQUEMENT pour LOCAL)
     */
    public LicenseData getCurrentLicense() {
        try {
            var latestToken = licenseTokenRepository.findLatestToken();
            if (latestToken.isEmpty()) {
                log.debug("📁 Aucun token de licence trouvé en base");
                return null;
            }

            LicenseToken token = latestToken.get();
            log.debug("🔍 Token trouvé - ID: {}, Créé le: {}", token.getId(), token.getCreatedAt());

            Map<String, Object> decryptedData = licenseTokenService.decryptLicenseData(token.getToken());
            log.debug("📄 Données déchiffrées: {} éléments", decryptedData.size());

            String jsonData = objectMapper.writeValueAsString(decryptedData);
            LicenseData licenseData = objectMapper.readValue(jsonData, LicenseData.class);

            // Vérifier que les données essentielles sont présentes
            if (licenseData.getCompanyInfo() == null) {
                log.error(" Données CompanyInfo manquantes dans le token");
                return null;
            }

            log.info("📄 Licence chargée depuis le token: {}", licenseData.getCompanyInfo().getCompanyName());
            return licenseData;

        } catch (Exception e) {
            log.error("💥 Erreur lecture token licence: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Vérifier si une licence est active (pour les deux types)
     */
    public boolean isLicenseActive(String licenseKey) {
        try {
            if (licenseKey == null || licenseKey.trim().isEmpty()) {
                log.warn("⚠️ Clé de licence manquante pour la vérification");
                return false;
            }

            log.info("🔍 Vérification du statut de la licence auprès du master: {}", licenseKey);
            return checkLicenseStatusWithMaster(licenseKey);

        } catch (Exception e) {
            log.error("💥 Erreur lors de la vérification de la licence: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifier le statut d'une licence auprès du MASTER
     */
    private boolean checkLicenseStatusWithMaster(String licenseKey) {
        try {
            log.info("📞 Vérification statut licence auprès du master: {}", licenseMasterUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("{\"licenseKey\": \"%s\"}", licenseKey);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                    licenseMasterUrl + "/api/license/verify",
                    request,
                    ApiResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ApiResponse apiResponse = response.getBody();

                if (apiResponse.isSuccess()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseData = (Map<String, Object>) apiResponse.getData();

                    if (responseData != null) {
                        Boolean isActive = (Boolean) responseData.get("success");
                        String status = (String) responseData.get("status");

                        boolean active = Boolean.TRUE.equals(isActive) || "ACTIVE".equals(status);
                        log.info("✅ Master confirme que la licence est active: {}", active);
                        return active;
                    }
                } else {
                    log.warn(" Master indique que la licence n'est pas active: {}", apiResponse.getMessage());
                    return false;
                }
            }

            log.error(" Erreur de communication avec le master");
            return false;

        } catch (HttpClientErrorException e) {
            log.error(" Erreur client lors de la vérification master: {}", e.getResponseBodyAsString());
            return false;
        } catch (HttpServerErrorException e) {
            log.error(" Erreur serveur lors de la vérification master: {}", e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            log.error("💥 Erreur lors de la vérification master: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Récupérer les données d'une licence depuis le MASTER avec gestion d'erreurs améliorée
     */
    public LicenseData getLicenseDataFromMaster(String licenseKey) {
        try {
            if (licenseKey == null || licenseKey.trim().isEmpty()) {
                throw new RuntimeException(" Clé de licence manquante");
            }

            log.info("📡 Récupération des données de licence depuis le master: {}", licenseKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("{\"licenseKey\": \"%s\"}", licenseKey);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                    licenseMasterUrl + "/api/license/verify",
                    request,
                    ApiResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ApiResponse apiResponse = response.getBody();

                if (apiResponse.isSuccess()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseData = (Map<String, Object>) apiResponse.getData();

                    if (responseData != null && responseData.containsKey("licenseData")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> licenseResult = (Map<String, Object>) responseData.get("licenseData");

                        String jsonData = objectMapper.writeValueAsString(licenseResult);
                        LicenseData licenseData = objectMapper.readValue(jsonData, LicenseData.class);

                        log.info("✅ Données de licence récupérées depuis le master");
                        return licenseData;
                    } else {
                        log.error(" Données de licence manquantes dans la réponse du master");
                        throw new RuntimeException(" Données de licence incomplètes sur le serveur master");
                    }
                } else {
                    // Le master a répondu mais avec une erreur métier
                    String errorMessage = apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : " Licence non valide sur le serveur master";

                    log.warn("⚠️ Master a refusé la licence: {}", errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            } else {
                log.error(" Erreur HTTP {} du master", response.getStatusCode());
                throw new RuntimeException(" Erreur de communication avec le serveur master");
            }

        } catch (HttpClientErrorException e) {
            // Erreur 4xx du master
            String errorMessage = extractErrorMessageFromMaster(e.getResponseBodyAsString());
            log.error(" Erreur client du master: {}", errorMessage);
            throw new RuntimeException(errorMessage);

        } catch (HttpServerErrorException e) {
            // Erreur 5xx du master
            log.error(" Erreur serveur du master: {}", e.getStatusCode());
            throw new RuntimeException(" Serveur master temporairement indisponible");

        } catch (Exception e) {
            log.error("💥 Erreur lors de la récupération des données depuis le master: {}", e.getMessage());
            throw new RuntimeException(" Impossible de contacter le serveur de licence");
        }
    }

    /**
     * Extraire le message d'erreur de la réponse du master
     */
    private String extractErrorMessageFromMaster(String responseBody) {
        try {
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                ApiResponse apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);
                if (apiResponse.getMessage() != null) {
                    return apiResponse.getMessage();
                }
            }
            return " Licence non trouvée sur le serveur master";
        } catch (Exception e) {
            log.debug(" Impossible de parser la réponse d'erreur du master: {}", responseBody);
            return " Licence non valide";
        }
    }

    /**
     * Vérifier si une licence est valide (date d'expiration)
     */
    public boolean isLicenseValid(LicenseData licenseData) {
        if (licenseData == null || licenseData.getLicenseConfig() == null) {
            return false;
        }

        java.time.LocalDate expiryDate = licenseData.getLicenseConfig().getExpiryDate();
        return expiryDate != null && expiryDate.isAfter(java.time.LocalDate.now());
    }

    /**
     * Méthode utilitaire pour récupérer les données de façon sécurisée
     */
    public LicenseData getLicenseDataForResponse(String licenseKey) {
        try {
            // Toujours privilégier le master comme source de vérité
            LicenseData masterData = getLicenseDataFromMaster(licenseKey);
            if (masterData != null) {
                return masterData;
            }

            // Fallback: utiliser le token local seulement si installation LOCALE
            LicenseData localData = getCurrentLicense();
            if (localData != null && isLocalInstallation(localData)) {
                return localData;
            }

            return null;

        } catch (Exception e) {
            log.error(" Erreur récupération données licence: {}", e.getMessage());
            return null;
        }
    }
}