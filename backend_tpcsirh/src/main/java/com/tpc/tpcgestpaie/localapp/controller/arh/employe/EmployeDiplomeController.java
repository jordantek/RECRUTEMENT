package com.tpc.tpcgestpaie.localapp.controller.employe;

import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDiplomeDTO;
import com.tpc.tpcgestpaie.localapp.helper.EmployeDiplomeHelper;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import com.tpc.tpcgestpaie.localapp.service.EmployeDiplomeService;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employes-diplomes")
public class EmployeDiplomeController {

    private final EmployeDiplomeService diplomeService;
    private final EmployeDiplomeHelper diplomeHelper;
    private final UserService userService;
    private final RequestHelper requestHelper;
    private final AuditLogService auditService;
    private final NotificationService notificationService;

    public EmployeDiplomeController(
            EmployeDiplomeService diplomeService,
            EmployeDiplomeHelper diplomeHelper,
            UserService userService,
            RequestHelper requestHelper,
            AuditLogService auditService,
            NotificationService notificationService
    ) {
        this.diplomeService = diplomeService;
        this.diplomeHelper = diplomeHelper;
        this.userService = userService;
        this.requestHelper = requestHelper;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<EmployeDiplomeDTO> diplomes = diplomeService.getAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des diplômes récupérée avec succès", diplomes));
        } catch (Exception e) {
            return errorResponse("Erreur lors de la récupération des diplômes");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<EmployeDiplomeDTO> diplome = diplomeService.getById(id);
            return diplome
                    .<ResponseEntity<?>>map(value ->
                            ResponseEntity.ok(new ApiResponse<>(true, "Diplôme trouvé", value)))
                    .orElseGet(() ->
                            notFoundResponse("Diplôme non trouvé"));
        } catch (Exception e) {
            return errorResponse("Erreur lors de la récupération du diplôme");
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody EmployeDiplomeDTO diplomeDto) {
        try {
            User currentUser = userService.getCurrentUser();

            var errors = diplomeHelper.getInvalidFieldMessages(diplomeDto.toEntity());
            if (!errors.isEmpty()) {
                return validationErrorResponse(errors);
            }

            diplomeDto.setCreatedAt(LocalDateTime.now());
            diplomeDto.setAddedById(currentUser.getId());

            EmployeDiplomeDTO saved = diplomeService.save(diplomeDto);

            logAndNotify(currentUser, "Ajout d’un diplôme", "Un diplôme employé a été ajouté avec succès 🎓");

            return new ResponseEntity<>(new ApiResponse<>(true, "Diplôme créé avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse("Erreur lors de la création du diplôme");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EmployeDiplomeDTO updatedDto) {
        try {
            Optional<EmployeDiplomeDTO> existingOpt = diplomeService.getById(id);
            if (existingOpt.isEmpty()) {
                return notFoundResponse("Diplôme non trouvé");
            }

            var errors = diplomeHelper.getInvalidFieldMessages(updatedDto.toEntity());
            if (!errors.isEmpty()) {
                return validationErrorResponse(errors);
            }

            User currentUser = userService.getCurrentUser();

            EmployeDiplomeDTO toUpdate = existingOpt.get();
            toUpdate.setAnneeObtention(updatedDto.getAnneeObtention());
            toUpdate.setDiplomeId(updatedDto.getDiplomeId());
            toUpdate.setEmployeId(updatedDto.getEmployeId());
            toUpdate.setUpdatedAt(LocalDateTime.now());
            toUpdate.setAddedById(currentUser.getId());

            EmployeDiplomeDTO saved = diplomeService.save(toUpdate);

            logAndNotify(currentUser, "Modification d’un diplôme", "Le diplôme employé a été modifié avec succès ✏️");

            return ResponseEntity.ok(new ApiResponse<>(true, "Diplôme mis à jour avec succès", saved));
        } catch (Exception e) {
            return errorResponse("Erreur lors de la mise à jour du diplôme");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<EmployeDiplomeDTO> existing = diplomeService.getById(id);
            if (existing.isEmpty()) {
                return notFoundResponse("Diplôme non trouvé");
            }

            User currentUser = userService.getCurrentUser();

            diplomeService.delete(id);

            logAndNotify(currentUser, "Suppression d’un diplôme", "Le diplôme employé a été supprimé avec succès 🗑️");

            return ResponseEntity.ok(new ApiResponse<>(true, "Diplôme supprimé avec succès", null));
        } catch (Exception e) {
            return errorResponse("Erreur lors de la suppression du diplôme");
        }
    }

    // ========== Méthodes utilitaires privées pour la lisibilité ==========

    private ResponseEntity<ApiResponse<?>> errorResponse(String message) {
        return new ResponseEntity<>(new ApiResponse<>(false, message, null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<?>> notFoundResponse(String message) {
        return new ResponseEntity<>(new ApiResponse<>(false, message, null), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ApiResponse<?>> validationErrorResponse(List<ErrorResponse> errors) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
    }

    private void logAndNotify(User user, String logAction, String notificationMessage) {
        auditService.log(
                logAction,
                "employes_diplomes",
                user.getId(),
                logAction + " par " + user.getFullName(),
                user,
                requestHelper.getClientIp(),
                requestHelper.getUserAgent()
        );

        notificationService.createNotification(user, logAction, notificationMessage);
    }
}
