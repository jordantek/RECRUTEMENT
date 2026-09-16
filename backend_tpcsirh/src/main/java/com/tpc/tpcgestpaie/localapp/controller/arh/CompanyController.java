package com.tpc.tpcgestpaie.localapp.controller.arh;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyPaieConfigDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.PayrollParametersCheckResponse;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.users.AdminEligibilityService;
import com.tpc.tpcgestpaie.localapp.service.util.FileStorageService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.CountryUtils;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/arh/companies")
public class CompanyController {

    private final ObjectMapper objectMapper;
    private final RequestHelper requestHelper;
    private final UserService userService;
    private final CompanyService companyService;
    private final FileStorageService fileStorageService;
    private final AdminEligibilityService adminEligibilityService;

    public CompanyController(ObjectMapper objectMapper, RequestHelper requestHelper,
                             UserService userService, CompanyService companyService,
                             FileStorageService fileStorageService, AdminEligibilityService adminEligibilityService) {
        this.objectMapper = objectMapper;
        this.requestHelper = requestHelper;
        this.userService = userService;
        this.companyService = companyService;
        this.fileStorageService = fileStorageService;
        this.adminEligibilityService = adminEligibilityService;
    }

    @GetMapping("/payroll-parameters-check/{companyId}")
    public ResponseEntity<PayrollParametersCheckResponse> checkPayrollParameters(
            @PathVariable Long companyId) {

        Optional<Company> companyOptional = companyService.findById(companyId);

        if (!companyOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Company company = companyOptional.get();
        boolean allParametersSet = company.arePayrollParametersComplete();

        String message = allParametersSet
                ? "Tous les paramètres de paie sont renseignés"
                : "Certains paramètres de paie ne sont pas renseignés";

        PayrollParametersCheckResponse response = new PayrollParametersCheckResponse(
                allParametersSet,
                message,
                company.getId(),
                company.getName()
        );

        return ResponseEntity.ok(response);
    }
    // ⭐ NOUVEAU : Création d'un client (entreprise principale)
    @PostMapping(value = "/clients/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createClientCompany(
            @RequestParam(value = "logo", required = false) MultipartFile logoFile,
            @RequestParam("data") String companyDataJson) {
        try {
            // Convertir le JSON en Map
            Map<String, Object> requestBody = objectMapper.readValue(companyDataJson, Map.class);

            // Vérification des champs obligatoires
            List<String> missingFields = requestHelper.getMissingFields(requestBody,
                    "name", "nss", "rss", "address", "country", "email", "phone", "rccm", "ifu",
                    "directorName", "directorPhone", "tvaVal", "activityAreas");

            if (!missingFields.isEmpty()) {
                String errorMessage = "Champs manquants : " + String.join(", ", missingFields);
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, errorMessage, null));
            }

            List<ErrorResponse> errors = validateCompanyData(requestBody, null);

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Validation des données échouée", errors)
                );
            }

            // Conversion en DTO
            CompanyDTO companyDTO = objectMapper.convertValue(requestBody, CompanyDTO.class);

            // ⭐ FORCER LE TYPE CLIENT
            companyDTO.setLicenseType(Company.LicenseType.MULTI_COMPANY);

            // Traitement du logo
            if (logoFile != null && !logoFile.isEmpty()) {
                String logoPath = fileStorageService.storeLogo(logoFile);
                companyDTO.setLogo(logoPath);
            }

            // Récupération utilisateur connecté
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ApiResponse<>(false, "Utilisateur non trouvé", null)
                );
            }

            // Création du client
            CompanyDTO createdCompany = companyService.createClientCompany(companyDTO, currentUser);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Client créé avec succès", createdCompany)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Une erreur est survenue : " + e.getMessage(), null)
            );
        }
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCompany(
            @RequestPart("data") @Valid CompanyRequestDTO request, // Spring valide tout ici !
            @RequestPart(value = "logo", required = false) MultipartFile logoFile) {
        try {
            User currentUser = userService.getCurrentUser();

            // 1. Vérification des droits
            if (!adminEligibilityService.isFullAdminInMainCompany(currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ApiResponse<>(false, "Accès refusé : Droits insuffisants", null)
                );
            }

            CompanyDTO companyDTO = new CompanyDTO();
            String country_code = CountryUtils.getCodeFromName(request.country(), Locale.ENGLISH);
            companyDTO.setName(request.name());
            companyDTO.setNss(request.nss());
            companyDTO.setRss(new BigDecimal(request.rss()));
            companyDTO.setAddress(request.address());
            companyDTO.setCountry(request.country());
            companyDTO.setEmail(request.email());
            companyDTO.setPhone(request.phone());
            companyDTO.setRccm(request.rccm());
            companyDTO.setIfu(request.ifu());
            companyDTO.setDirectorName(request.directorName());
            companyDTO.setDirectorPhone(request.directorPhone());
            companyDTO.setCountryCode(country_code);
            companyDTO.setWebSite(request.webSite());
            companyDTO.setDirectorEmail(request.directorEmail());

            // 3. Gestion du fichier Logo
            if (logoFile != null && !logoFile.isEmpty()) {
                String logoPath = fileStorageService.storeLogo(logoFile);
                companyDTO.setLogo(logoPath);
            }

            // 4. Appel au service
           CompanyDTO createdCompany = companyService.createCompany(companyDTO, currentUser);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Entreprise créée avec succès",createdCompany)
            );

        } catch (Exception e) {
            log.error("Erreur création entreprise", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la création : " + e.getMessage(), null)
            );
        }
    }





    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCompany(
            @PathVariable Long id,
            @RequestPart("data") @Valid CompanyRequestDTO request, // Spring valide tout ici !
            @RequestPart(value = "logo", required = false) MultipartFile logoFile) {
        try {
            User currentUser = userService.getCurrentUser();

            // 1. Vérification des droits
            if (!adminEligibilityService.isFullAdminInMainCompany(currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ApiResponse<>(false, "Accès refusé : Droits insuffisants", null)
                );
            }

            CompanyDTO companyDTO = new CompanyDTO();
            String country_code = CountryUtils.getCodeFromName(request.country(), Locale.ENGLISH);
            companyDTO.setName(request.name());
            companyDTO.setNss(request.nss());
            companyDTO.setRss(new BigDecimal(request.rss()));
            companyDTO.setAddress(request.address());
            companyDTO.setCountry(request.country());
            companyDTO.setEmail(request.email());
            companyDTO.setPhone(request.phone());
            companyDTO.setRccm(request.rccm());
            companyDTO.setIfu(request.ifu());
            companyDTO.setDirectorName(request.directorName());
            companyDTO.setDirectorPhone(request.directorPhone());
            companyDTO.setCountryCode(country_code);
            companyDTO.setWebSite(request.webSite());
            companyDTO.setDirectorEmail(request.directorEmail());

            // 3. Gestion du fichier Logo
            if (logoFile != null && !logoFile.isEmpty()) {
                String logoPath = fileStorageService.storeLogo(logoFile);
                companyDTO.setLogo(logoPath);
            }

            // 4. Appel au service
            CompanyDTO createdCompany = companyService.updateCompany(id,companyDTO, currentUser);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Information modifier avec succès",createdCompany)
            );

        } catch (Exception e) {
            log.error("Erreur Mise à jours", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la création : " + e.getMessage(), null)
            );
        }
    }

    // ⭐ NOUVEAU : Mise à jour de la licence
    @PutMapping("/{id}/license")
    public ResponseEntity<?> updateCompanyLicense(
            @PathVariable Long id,
            @RequestParam Company.LicenseType licenseType) {
        try {
            // Récupération utilisateur connecté
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ApiResponse<>(false, "Utilisateur non trouvé", null)
                );
            }

            // Mise à jour de la licence
            CompanyDTO updatedCompany = companyService.updateLicense(id, licenseType, currentUser);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Licence mise à jour avec succès", updatedCompany)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Une erreur est survenue : " + e.getMessage(), null)
            );
        }
    }

    @PutMapping(value = "/config-paie/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePaieConfigCompany(
            @PathVariable Long id,
            @RequestBody Map<String, Object> paieConfigData) {
        try {
            // Vérification des champs obligatoires pour la configuration paie
            List<String> missingFields = requestHelper.getMissingFields(paieConfigData,
                    "vps", "vpsEffectDate", "signatoryName", "ca", "modeJouissanceConge",
                    "nbrJourTravail", "nbrJourConge", "heuresParJour", "heuresParSemaine");

            if (!missingFields.isEmpty()) {
                String errorMessage = "Champs de configuration paie manquants : " + String.join(", ", missingFields);
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, errorMessage, null));
            }

            // Validation des données
            List<ErrorResponse> errors = validatePaieConfigData(paieConfigData);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Validation des données de configuration paie échouée", errors)
                );
            }

            // Récupération utilisateur connecté
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ApiResponse<>(false, "Utilisateur non trouvé", null)
                );
            }

            // Mise à jour de la configuration paie de l'entreprise
            CompanyPaieConfigDTO updatedConfig = companyService.updatePaieConfig(id, paieConfigData, currentUser);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Configuration paie mise à jour avec succès", updatedConfig)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Une erreur est survenue : " + e.getMessage(), null)
            );
        }
    }

    // ⭐ NOUVEAU : Récupérer tous les clients
    @GetMapping("/clients")
    public ResponseEntity<?> getAllClientCompanies() {
        try {
            List<CompanyDTO> clients = companyService.getClientCompanies();
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des clients récupérée avec succès", clients)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la récupération des clients : " + e.getMessage(), null)
            );
        }
    }

    // ⭐ NOUVEAU : Récupérer les entreprises gérées par un client
    @GetMapping("/clients/{clientId}/managed")
    public ResponseEntity<?> getManagedCompanies(@PathVariable Long clientId) {
        try {
            List<CompanyDTO> managedCompanies = companyService.getManagedCompanies(clientId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des entreprises gérées récupérée avec succès", managedCompanies)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la récupération des entreprises gérées : " + e.getMessage(), null)
            );
        }
    }

    // Endpoint pour récupérer le logo
    @GetMapping("/logo/{fileName:.+}")
    public ResponseEntity<Resource> getLogo(@PathVariable String fileName) {
        try {
            Resource resource = fileStorageService.loadLogo(fileName);
            String contentType = determineContentType(fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + extractFileName(fileName) + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllCompanies() {
        try {
            List<CompanyDTO> companies = companyService.getAllCompaniesDTO();
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des entreprises récupérée avec succès", companies)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la récupération des entreprises : " + e.getMessage(), null)
            );
        }
    }

    @GetMapping("/non-assigne")
    public ResponseEntity<ApiResponse<List<Company>>> getCompaniesNotAssigned() {
        try {
            List<Company> companies = companyService.getCompaniesNotAssigned();
            return ResponseEntity.ok(new ApiResponse<>(true, "Entreprises non affectées récupérées", companies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la récupération des entreprises : " + e.getMessage(), null)
            );
        }
    }

    // Endpoint pour récupérer une entreprise par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        try {
            CompanyDTO companyDTO = companyService.getCompanyDTOById(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Entreprise récupérée avec succès", companyDTO)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, "Entreprise non trouvée", null)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la récupération de l'entreprise : " + e.getMessage(), null)
            );
        }
    }

    // Endpoint pour mettre à jour seulement le logo
    @PutMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCompanyLogo(
            @PathVariable Long id,
            @RequestParam("logo") MultipartFile logoFile) {
        try {
            // Récupération utilisateur connecté
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ApiResponse<>(false, "Utilisateur non trouvé", null)
                );
            }

            // Mise à jour du logo
            CompanyDTO updatedCompany = companyService.updateCompanyLogo(id, logoFile, currentUser);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Logo mis à jour avec succès", updatedCompany)
            );

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, "Entreprise non trouvée", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la mise à jour du logo : " + e.getMessage(), null)
            );
        }
    }

    //route de stistique


    // Endpoint pour supprimer une entreprise
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        try {
            // Récupération utilisateur connecté
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ApiResponse<>(false, "Utilisateur non trouvé", null)
                );
            }

            companyService.delete(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Entreprise supprimée avec succès", null)
            );

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, "Entreprise non trouvée", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la suppression de l'entreprise : " + e.getMessage(), null)
            );
        }
    }

    // ⭐ MÉTHODES UTILITAIRES PRIVÉES

    private List<ErrorResponse> validateCompanyData(Map<String, Object> requestBody, Long excludeId) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Validations d'unicité avec exclusion
        if (requestHelper.isValueExistsExcept(Company.class, "name", requestBody.get("name"), excludeId)) {
            errors.add(new ErrorResponse("name", "Le nom de la société existe déjà"));
        }

        String email = (String) requestBody.get("email");
        if (requestHelper.isValidEmail(email)) {
            errors.add(new ErrorResponse("email", "Email invalide"));
        } else if (requestHelper.isValueExistsExcept(Company.class, "email", email, excludeId)) {
            errors.add(new ErrorResponse("email", "L'email de la société existe déjà"));
        }

        if (requestHelper.isValueExistsExcept(Company.class, "phone", requestBody.get("phone"), excludeId)) {
            errors.add(new ErrorResponse("phone", "Le numéro de téléphone existe déjà"));
        }

        if (requestHelper.isValueExistsExcept(Company.class, "nss", requestBody.get("nss"), excludeId)) {
            errors.add(new ErrorResponse("nss", "Le NSS existe déjà"));
        }

        if (requestHelper.isValueExistsExcept(Company.class, "rccm", requestBody.get("rccm"), excludeId)) {
            errors.add(new ErrorResponse("rccm", "Le RCCM existe déjà"));
        }

        if (requestHelper.isValueExistsExcept(Company.class, "ifu", requestBody.get("ifu"), excludeId)) {
            errors.add(new ErrorResponse("ifu", "L'IFU existe déjà"));
        }

        return errors;
    }

    private List<ErrorResponse> validatePaieConfigData(Map<String, Object> paieConfigData) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Validation de vps
        Object vpsObj = paieConfigData.get("vps");
        if (vpsObj != null) {
            try {
                double vps = Double.parseDouble(vpsObj.toString());
                if (vps < 0) {
                    errors.add(new ErrorResponse("vps", "Le VPS doit être un nombre positif"));
                }
            } catch (NumberFormatException e) {
                errors.add(new ErrorResponse("vps", "Le VPS doit être un nombre valide"));
            }
        }

        // Validation de la date
        String vpsEffectDateStr = (String) paieConfigData.get("vpsEffectDate");
        if (vpsEffectDateStr != null) {
            try {
                LocalDate.parse(vpsEffectDateStr);
            } catch (DateTimeParseException e) {
                errors.add(new ErrorResponse("vpsEffectDate", "Format de date invalide. Utilisez le format yyyy-MM-dd"));
            }
        }

        // Validation de ca
        Object caObj = paieConfigData.get("ca");
        if (caObj != null) {
            try {
                double ca = Double.parseDouble(caObj.toString());
                if (ca < 0) {
                    errors.add(new ErrorResponse("ca", "Le CA doit être un nombre positif"));
                }
            } catch (NumberFormatException e) {
                errors.add(new ErrorResponse("ca", "Le CA doit être un nombre valide"));
            }
        }

        // Validation des nombres
        validatePositiveNumber(paieConfigData, "nbrJourTravail", "Nombre de jours de travail", errors);
        validatePositiveNumber(paieConfigData, "nbrJourConge", "Nombre de jours de congé", errors);
        validatePositiveNumber(paieConfigData, "heuresParJour", "Heures par jour", errors);
        validatePositiveNumber(paieConfigData, "heuresParSemaine", "Heures par semaine", errors);

        return errors;
    }

    private void validatePositiveNumber(Map<String, Object> requestBody, String fieldName, String fieldLabel, List<ErrorResponse> errors) {
        Object value = requestBody.get(fieldName);
        if (value != null) {
            try {
                int intValue = Integer.parseInt(value.toString());
                if (intValue <= 0) {
                    errors.add(new ErrorResponse(fieldName, fieldLabel + " doit être un nombre positif"));
                }
            } catch (NumberFormatException e) {
                errors.add(new ErrorResponse(fieldName, fieldLabel + " doit être un nombre entier valide"));
            }
        }
    }

    private String determineContentType(String fileName) {
        String actualFileName = extractFileName(fileName);
        String extension = actualFileName.substring(actualFileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            default -> "application/octet-stream";
        };
    }

    private String extractFileName(String filePath) {
        int lastSlashIndex = filePath.lastIndexOf("/");
        return (lastSlashIndex != -1) ? filePath.substring(lastSlashIndex + 1) : filePath;
    }
}