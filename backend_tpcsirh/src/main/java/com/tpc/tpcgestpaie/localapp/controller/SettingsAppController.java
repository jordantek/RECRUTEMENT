package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.SettingsApp.SettingsAppRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.SettingsApp.SettingsAppResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.SettingsApp;
import com.tpc.tpcgestpaie.localapp.service.SettingsAppService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsAppController {

    private final SettingsAppService settingsAppService;

    // =========================
    // 💾 Création ou mise à jour du thème
    // =========================
    @PostMapping("/theme")
    public ResponseEntity<ApiResponse<SettingsAppResponseDTO>> saveTheme(
            @Valid @RequestBody SettingsAppRequestDTO dto
    ) {
        try {

            SettingsApp setting = settingsAppService.saveThemeColor(
                    dto.companyId(),
                    dto.themeColor()
            );

            // Mapping Entity -> DTO
            SettingsAppResponseDTO response = settingsAppService.mapToResponse(setting);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Couleur du thème enregistrée avec succès", response)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    // =========================
// 🔍 Récupérer la configuration d'une entreprise
// =========================
    @GetMapping("/color")
    public ResponseEntity<ApiResponse<SettingsAppResponseDTO>> getSettings() {
        try {

            SettingsApp setting = settingsAppService.getFirstSettings();

            SettingsAppResponseDTO response = settingsAppService.mapToResponse(setting);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Configuration récupérée avec succès", response)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
// 🔍 Récupérer la configuration d'une entreprise
// =========================
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<SettingsAppResponseDTO>> getSettingsByCompany(
            @PathVariable Long companyId
    ) {
        try {

            SettingsApp setting = settingsAppService.getSettingsByCompany(companyId);

            SettingsAppResponseDTO response = settingsAppService.mapToResponse(setting);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Configuration récupérée avec succès", response)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}