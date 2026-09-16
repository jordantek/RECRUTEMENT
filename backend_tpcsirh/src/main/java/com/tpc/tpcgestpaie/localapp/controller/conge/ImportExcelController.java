package com.tpc.tpcgestpaie.localapp.controller.conge;

import com.tpc.tpcgestpaie.localapp.dto.conge.initialisation.ImportSoldeBatchDTO;
import com.tpc.tpcgestpaie.localapp.service.conge.ImportSoldeExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/conges/initialisation")
@RequiredArgsConstructor
@Tag(name = "Initialisation Soldes", description = "Migration des soldes congés")
public class ImportExcelController {

    private final ImportSoldeExcelService importExcelService;

    @PostMapping(value = "/import-excel", consumes = "multipart/form-data")
    @Operation(summary = "Import Excel des soldes par matricule")
    public ResponseEntity<ImportSoldeBatchDTO> importerExcel(
            @RequestParam("fichier") MultipartFile fichier) {
        ImportSoldeBatchDTO result = importExcelService.importerExcel(fichier);
        return ResponseEntity.ok(result);
    }
}