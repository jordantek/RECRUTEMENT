package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.users.AdminEligibilityService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AdminAccessController {

    private final AdminEligibilityService adminEligibilityService;
    private final UserService userService;

    public AdminAccessController(AdminEligibilityService adminEligibilityService,
                                 UserService userService) {
        this.adminEligibilityService = adminEligibilityService;
        this.userService = userService;
    }

    /**
     * Vérifie si l'utilisateur peut accéder à l'administration
     */
    @GetMapping("/can-access-admin")
    public ResponseEntity<ApiResponse<Boolean>> canAccessAdmin() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non connecté", null));
            }

            boolean canAccess = adminEligibilityService.canAccessAdmin(currentUser);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Droits d'administration vérifiés", canAccess)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la vérification", null));
        }
    }

    /**
     * Récupère tous les droits de l'utilisateur
     */
    @GetMapping("/user-permissions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserPermissions() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non connecté", null));
            }

            Map<String, Object> permissions = new HashMap<>();
            permissions.put("canAccessAdmin", adminEligibilityService.canAccessAdmin(currentUser));
            permissions.put("hasFullAdminAccess", adminEligibilityService.hasFullAdminAccess(currentUser));
            permissions.put("hasLimitedAdminAccess", adminEligibilityService.hasLimitedAdminAccess(currentUser));
            permissions.put("canManageCompanies", adminEligibilityService.canManageCompanies(currentUser));
            permissions.put("canManageUsers", adminEligibilityService.canManageUsers(currentUser));
            permissions.put("isRegularEmployee", adminEligibilityService.isRegularEmployee(currentUser));
            permissions.put("adminLevel", adminEligibilityService.getAdminLevel(currentUser).name());

            // Ajouter les rôles de l'utilisateur
            permissions.put("userRoles", currentUser.getRoles().stream()
                    .map(role -> role.getName())
                    .toList());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Permissions utilisateur récupérées", permissions)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des permissions", null));
        }
    }
}