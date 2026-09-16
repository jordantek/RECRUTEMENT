package com.tpc.tpcgestpaie.localapp.controller.users;

import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.entreprise.MultiTenantAccessService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users/manage")
public class UserContextController {

    private final MultiTenantAccessService multiTenantAccessService;
    private final UserService userService;

    public UserContextController(MultiTenantAccessService multiTenantAccessService, UserService userService) {
        this.multiTenantAccessService = multiTenantAccessService;
        this.userService = userService;
    }

    @Transactional
    @GetMapping("/context")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserContext() {
        try {
            User currentUser = userService.getCurrentUser();
            Map<String, Object> context = multiTenantAccessService.getUserContext(currentUser);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Contexte utilisateur récupéré", context)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
}