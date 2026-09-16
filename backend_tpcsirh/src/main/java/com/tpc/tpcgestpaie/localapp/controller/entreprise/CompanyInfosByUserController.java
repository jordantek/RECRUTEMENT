package com.tpc.tpcgestpaie.localapp.controller.entreprise;


import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.entreprise.CompanyTypeService;
import com.tpc.tpcgestpaie.localapp.service.entreprise.CompanyVisibilityService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies/by/user")
public class CompanyInfosByUserController {

    private final CompanyVisibilityService companyVisibilityService;
    private final UserService userService;
    private final CompanyTypeService companyTypeService;


    public CompanyInfosByUserController(CompanyVisibilityService companyVisibilityService, UserService userService, CompanyTypeService companyTypeService) {
        this.companyVisibilityService = companyVisibilityService;
        this.userService = userService;
        this.companyTypeService = companyTypeService;
    }

    /**
     * 1. Liste des entreprises pour les utilisateurs non-EMPLOYEE
     */
    @GetMapping("/visible")
    public ResponseEntity<ApiResponse<List<CompanyDTO>>> getCompaniesForNonEmployee() {
        try {
            User currentUser = userService.getCurrentUser();

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non connecté", null));
            }

            // Vérifier si l'utilisateur est EMPLOYEE
            boolean isEmployee = currentUser.getRoles().stream()
                    .anyMatch(role -> "ROLE_EMPLOYEE".equals(role.getName()));

            if (isEmployee) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false,
                                "Les employés n'ont pas accès à la liste des entreprises",
                                null));
            }

            // Récupérer les entreprises visibles
            List<CompanyDTO> visibleCompanies = companyVisibilityService.getVisibleCompaniesForCurrentUser();

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des entreprises récupérée", visibleCompanies)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    /**
     * 2. Entreprise cliente de l'utilisateur
     */
    @Transactional
    @GetMapping("/my-client-company")
    public ResponseEntity<ApiResponse<CompanyDTO>> getMyClientCompany() {
        try {
            User currentUser = userService.getCurrentUser();

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non connecté", null));
            }

            // Vérifier si l'utilisateur a une entreprise
            if (currentUser.getCompany() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false,
                                "Aucune entreprise associée à cet utilisateur",
                                null));
            }

            Company userCompany = currentUser.getCompany();

            // Vérifier si c'est une entreprise cliente (principale)
            if (!userCompany.isClientCompany()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false,
                                "Votre entreprise n'est pas une entreprise cliente",
                                null));
            }

            CompanyDTO companyDTO = CompanyDTO.fromEntity(userCompany);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Entreprise cliente récupérée", companyDTO)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    /**
     * 3. Type de licence de l'entreprise cliente de l'utilisateur
     */
    @Transactional
    @GetMapping("/my-company-license")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyCompanyLicense() {
        try {
            User currentUser = userService.getCurrentUser();

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non connecté", null));
            }

            // Vérifier si l'utilisateur a une entreprise
            if (currentUser.getCompany() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false,
                                "Aucune entreprise associée à cet utilisateur",
                                null));
            }

            Company userCompany = currentUser.getCompany();

            // Vérifier si c'est une entreprise cliente
            if (!userCompany.isClientCompany()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false,
                                "Votre entreprise n'est pas une entreprise cliente",
                                null));
            }

            Map<String, Object> licenseInfo = new HashMap<>();
            licenseInfo.put("companyId", userCompany.getId());
            licenseInfo.put("companyName", userCompany.getName());
            licenseInfo.put("licenseType", userCompany.getLicenseType().name());
            licenseInfo.put("licenseLabel", getLicenseLabel(userCompany.getLicenseType()));
            licenseInfo.put("canManageCompanies", userCompany.canManageCompanies());
            licenseInfo.put("managedCompaniesCount", userCompany.getManagedCompanies().size());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Information de licence récupérée", licenseInfo)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    /**
     * Méthode utilitaire pour le libellé de la licence
     */
    private String getLicenseLabel(Company.LicenseType licenseType) {
        switch (licenseType) {
            case SINGLE_COMPANY:
                return "Licence Single Company - Gère uniquement cette entreprise";
            case MULTI_COMPANY:
                return "Licence Multi Company - Peut gérer plusieurs entreprises";
            default:
                return "Licence inconnue";
        }
    }
}