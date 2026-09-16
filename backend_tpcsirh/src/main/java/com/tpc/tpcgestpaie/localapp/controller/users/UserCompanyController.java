package com.tpc.tpcgestpaie.localapp.controller.users;

import com.tpc.tpcgestpaie.localapp.dto.users.UserAvecEmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.users.UserEmployeService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserCompanyController {

    private final UserEmployeService userCompanyService;
    private final UserService userService;

    /**
     * Lister tous les utilisateurs d'une entreprise
     */
    @GetMapping("/entreprise/{companyId}")
    public ResponseEntity<ApiResponse<?>> getUsersByCompany(
            @PathVariable Long companyId) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<UserAvecEmployeDTO> users = userCompanyService.getUsersByCompany(companyId);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("users", users);
            responseData.put("total", users.size());
            responseData.put("companyId", companyId);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des utilisateurs de l'entreprise récupérée avec succès", responseData));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des utilisateurs: " + e.getMessage(), null));
        }
    }

    /**
     * Lister les utilisateurs actifs d'une entreprise
     */
    @GetMapping("/entreprise/{companyId}/actifs")
    public ResponseEntity<ApiResponse<?>> getActiveUsersByCompany(
            @PathVariable Long companyId) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<UserAvecEmployeDTO> users = userCompanyService.getActiveUsersByCompany(companyId);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("users", users);
            responseData.put("total", users.size());
            responseData.put("companyId", companyId);
            responseData.put("filtre", "actifs");

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des utilisateurs actifs de l'entreprise récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des utilisateurs actifs: " + e.getMessage(), null));
        }
    }

    /**
     * Rechercher des utilisateurs dans une entreprise
     */
    @GetMapping("/entreprise/{companyId}/recherche")
    public ResponseEntity<ApiResponse<?>> searchUsersInCompany(
            @PathVariable Long companyId,
            @RequestParam String q) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            if (q == null || q.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Le terme de recherche est requis", null));
            }

            List<UserAvecEmployeDTO> users = userCompanyService.searchUsersInCompany(companyId, q.trim());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("users", users);
            responseData.put("total", users.size());
            responseData.put("companyId", companyId);
            responseData.put("termeRecherche", q);
            responseData.put("resultats", users.size());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Résultats de la recherche récupérés avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la recherche: " + e.getMessage(), null));
        }
    }

    /**
     * Statistiques des utilisateurs par entreprise
     */
    @GetMapping("/entreprise/{companyId}/statistiques")
    public ResponseEntity<ApiResponse<?>> getUsersStatisticsByCompany(
            @PathVariable Long companyId) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<UserAvecEmployeDTO> allUsers = userCompanyService.getUsersByCompany(companyId);
            List<UserAvecEmployeDTO> activeUsers = userCompanyService.getActiveUsersByCompany(companyId);

            long totalUsers = allUsers.size();
            long activeUsersCount = activeUsers.size();
            long inactiveUsersCount = totalUsers - activeUsersCount;

            long usersWithEmploye = allUsers.stream()
                    .filter(user -> user.getEmploye() != null)
                    .count();
            long usersWithoutEmploye = totalUsers - usersWithEmploye;

            Map<String, Object> statistiques = new HashMap<>();
            statistiques.put("totalUtilisateurs", totalUsers);
            statistiques.put("utilisateursActifs", activeUsersCount);
            statistiques.put("utilisateursInactifs", inactiveUsersCount);
            statistiques.put("utilisateursAvecEmploye", usersWithEmploye);
            statistiques.put("utilisateursSansEmploye", usersWithoutEmploye);
            statistiques.put("companyId", companyId);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Statistiques des utilisateurs récupérées avec succès", statistiques));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des statistiques: " + e.getMessage(), null));
        }
    }
}