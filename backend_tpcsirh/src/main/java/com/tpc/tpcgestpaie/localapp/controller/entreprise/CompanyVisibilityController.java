package com.tpc.tpcgestpaie.localapp.controller.entreprise;

import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.entreprise.CompanyVisibilityService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gestion/entreprises")
public class CompanyVisibilityController {

    private final CompanyVisibilityService companyVisibilityService;
    private final UserService userService;

    public CompanyVisibilityController(CompanyVisibilityService companyVisibilityService,
                                       UserService userService) {
        this.companyVisibilityService = companyVisibilityService;
        this.userService = userService;
    }

    @GetMapping("/visible")
    public ResponseEntity<ApiResponse<List<CompanyDTO>>> getVisibleCompanies() {
        try {
            List<CompanyDTO> visibleCompanies = companyVisibilityService.getVisibleCompaniesForCurrentUser();

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Entreprises visibles récupérées", visibleCompanies)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    @Transactional
    @GetMapping("/visibility-context")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVisibilityContext() {
        try {
            User currentUser = userService.getCurrentUser();
            Map<String, Object> context = companyVisibilityService.getVisibilityContext(currentUser);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Contexte de visibilité récupéré", context)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

    @Transactional
    @GetMapping("/{companyId}/can-view")
    public ResponseEntity<ApiResponse<Boolean>> canViewCompany(@PathVariable Long companyId) {
        try {
            User currentUser = userService.getCurrentUser();
            boolean canView = companyVisibilityService.canUserViewCompany(currentUser, companyId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Droit de visualisation vérifié", canView)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
}