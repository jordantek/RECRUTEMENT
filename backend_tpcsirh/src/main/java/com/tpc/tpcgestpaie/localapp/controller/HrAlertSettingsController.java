package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.event.HrAlertSettingsRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.event.HrAlertSettingsResponsDTO;
import com.tpc.tpcgestpaie.localapp.service.HrAlertSettingsService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr-alert-settings")
@RequiredArgsConstructor
public class HrAlertSettingsController {

    private final HrAlertSettingsService service;

    // =========================
    // 🔍 Résolution intelligente
    // =========================
    @GetMapping("/resolve")
    public ResponseEntity<ApiResponse<HrAlertSettingsResponsDTO>> resolve(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long userId
    ) {
        try {
            HrAlertSettingsResponsDTO dto = service.resolve(companyId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Paramètres résolus", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }


    @PostMapping
    public ResponseEntity<ApiResponse<HrAlertSettingsResponsDTO>> create(
            @Valid @RequestBody HrAlertSettingsRequestDTO dto
    ) {
        try {
            HrAlertSettingsResponsDTO saved = service.saveOrUpdate(
                    dto.companyId(),
                    dto.userId(),
                    dto.upcomingEventsDays(),
                    dto.requiredActionsDays(),
                    dto.enabled()
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "Paramètres enregistrés avec succès", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 🔄 Mise à jour
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HrAlertSettingsResponsDTO>> update(
            @PathVariable Long id,
            @RequestBody HrAlertSettingsRequestDTO dto
    ) {
        try {
            HrAlertSettingsResponsDTO updated = service.update(
                    id,
                    dto.upcomingEventsDays(),
                    dto.requiredActionsDays(),
                    dto.enabled()
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "Paramètres mis à jour", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 🧹 Soft delete
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> softDelete(@PathVariable Long id) {
        try {
            service.softDelete(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Paramètres supprimés (soft delete)", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // ⚡ Récupérer les configs par utilisateur
    // =========================
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<HrAlertSettingsResponsDTO>> getByUserId(@PathVariable Long userId) {
        try {
            HrAlertSettingsResponsDTO dto = service.findByUserId(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Paramètres récupérés pour l'utilisateur", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

}
