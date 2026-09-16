package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.ParametrePaieDTO;
import com.tpc.tpcgestpaie.localapp.service.ParametrePaieService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parametre-paie")
public class ParametrePaieController {

    private final ParametrePaieService parametrePaieService;

    public ParametrePaieController(ParametrePaieService parametrePaieService) {
        this.parametrePaieService = parametrePaieService;
    }

    // 🔹 Récupérer le paramètre unique
    @GetMapping
    public ResponseEntity<?> getUnique() {
        try {
            ParametrePaieDTO dto = parametrePaieService.getUnique();
            return ResponseEntity.ok(new ApiResponse<>(true, "Paramètre récupéré avec succès", dto));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération du paramètre", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔹 Créer ou mettre à jour automatiquement
    @PostMapping
    public ResponseEntity<?> saveOrUpdate(@RequestBody ParametrePaieDTO dto) {
        try {
            ParametrePaieDTO saved = parametrePaieService.saveOrUpdate(dto);
            return new ResponseEntity<>(new ApiResponse<>(true, "Paramètre créé/mis à jour avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de l’enregistrement du paramètre", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔹 Mise à jour explicite
    @PutMapping
    public ResponseEntity<?> update(@RequestBody ParametrePaieDTO dto) {
        try {
            ParametrePaieDTO updated = parametrePaieService.update(dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Paramètre mis à jour avec succès", updated));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour du paramètre", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
