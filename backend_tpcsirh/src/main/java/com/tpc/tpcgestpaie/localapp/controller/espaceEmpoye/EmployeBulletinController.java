package com.tpc.tpcgestpaie.localapp.controller.espaceEmpoye;

import com.tpc.tpcgestpaie.localapp.dto.paie.BulletinPaieDTO;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.paie.bulletin.BulletinPaieService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/espace-employe/bulletins")
public class EmployeBulletinController {

    private final UserService userService;
    private final BulletinPaieService bulletinPaieService;

    public EmployeBulletinController(
            UserService userService,
            BulletinPaieService bulletinPaieService
    ) {
        this.userService = userService;
        this.bulletinPaieService = bulletinPaieService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getMyBulletins() {

        try {
            User currentUser = userService.getCurrentUser();

            if (currentUser == null || currentUser.getEmploye() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                false,
                                "Employé non trouvé",
                                null
                        ));
            }

            System.out.println( "id employé :" +currentUser.getEmploye().getId());

            List<BulletinPaieDTO> data =
                    bulletinPaieService.getAllBulletinsByEmploye(
                            currentUser.getEmploye().getId()
                    );

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Liste des bulletins récupérée avec succès",
                            data
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Erreur lors de la récupération des bulletins : " + e.getMessage(),
                            null
                    ));
        }
    }
}
