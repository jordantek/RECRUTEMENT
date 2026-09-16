package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.ItsTrancheDTO;
import com.tpc.tpcgestpaie.localapp.service.ItsTrancheService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/its-tranche")
public class ItsTrancheController {

    private final ItsTrancheService service;

    public ItsTrancheController(ItsTrancheService service) {
        this.service = service;
    }

    // 🔹 Get unique
    @GetMapping
    public ResponseEntity<?> getUnique() {
        try {
            ItsTrancheDTO dto = service.getUnique();
            return ResponseEntity.ok(new ApiResponse<>(true, "Tranche récupérée avec succès", dto));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔹 Save or Update
    @PostMapping
    public ResponseEntity<?> saveOrUpdate(@RequestBody ItsTrancheDTO dto) {
        try {
            ItsTrancheDTO saved = service.saveOrUpdate(dto);
            return new ResponseEntity<>(new ApiResponse<>(true, "Tranche créée/mise à jour avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de l’enregistrement", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔹 Update explicite
    @PutMapping
    public ResponseEntity<?> update(@RequestBody ItsTrancheDTO dto) {
        try {
            ItsTrancheDTO updated = service.update(dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tranche mise à jour avec succès", updated));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
