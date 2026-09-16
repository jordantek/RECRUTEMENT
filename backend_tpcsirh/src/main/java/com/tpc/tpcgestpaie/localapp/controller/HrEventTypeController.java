package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.event.HrEventTypeResponsDto;
import com.tpc.tpcgestpaie.localapp.service.HrEventTypeService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hr-event-types")
@RequiredArgsConstructor
public class HrEventTypeController {

    private final HrEventTypeService service;

    // =========================
    // 🔹 Récupérer tous les types d'événement actifs
    // =========================
    @GetMapping
    public ResponseEntity<ApiResponse<List<HrEventTypeResponsDto>>> getAll() {
        try {
            List<HrEventTypeResponsDto> dtos = service.getAll()
                    .stream()
                    .map(service::toDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des types d'événement", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 🔹 Récupérer par ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HrEventTypeResponsDto>> getById(@PathVariable Long id) {
        try {
            HrEventTypeResponsDto dto = service.toDto(service.getById(id));
            return ResponseEntity.ok(new ApiResponse<>(true, "Type d'événement récupéré", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 🔹 Récupérer par slug
    // =========================
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<HrEventTypeResponsDto>> getBySlug(@PathVariable String slug) {
        try {
            HrEventTypeResponsDto dto = service.toDto(service.getBySlug(slug));
            return ResponseEntity.ok(new ApiResponse<>(true, "Type d'événement récupéré", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 🔹 Créer un nouveau type d'événement
    // =========================
   /* @PostMapping
    public ResponseEntity<ApiResponse<HrEventTypeResponsDto>> create(
            @Valid @RequestBody HrEventTypeResponsDto dto
    ) {
        try {
            HrEventTypeResponsDto saved = service.toDto(service.create(service.fromDto(dto)));
            return ResponseEntity.ok(new ApiResponse<>(true, "Type d'événement créé avec succès", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }*/

    // =========================
    // 🔹 Mettre à jour un type d'événement
    // =========================
  /*  @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HrEventTypeResponsDto>> update(
            @PathVariable Long id,
            @RequestBody HrEventTypeResponsDto dto
    ) {
        try {
            HrEventTypeResponsDto updated = service.toDto(service.update(id, service.fromDto(dto)));
            return ResponseEntity.ok(new ApiResponse<>(true, "Type d'événement mis à jour", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }*/

    // =========================
    // 🔹 Soft delete
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> softDelete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Type d'événement supprimé (soft delete)", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 🔹 Activer / désactiver
    // =========================
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<HrEventTypeResponsDto>> toggleEnabled(@PathVariable Long id) {
        try {
            HrEventTypeResponsDto updated = service.toDto(service.toggleEnabled(id));
            return ResponseEntity.ok(new ApiResponse<>(true, "Statut activé/désactivé avec succès", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
