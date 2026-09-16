package com.tpc.tpcgestpaie.localapp.controller.conge;

import com.tpc.tpcgestpaie.localapp.dto.conge.initialisation.SaisieSoldeInitialDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.initialisation.ResultatInitialisationDTO;
import com.tpc.tpcgestpaie.localapp.service.conge.InitialisationSoldeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conges/initialisation")
@RequiredArgsConstructor
@Tag(name = "Initialisation Soldes", description = "Migration des soldes congés")
public class InitialisationController {

    private final InitialisationSoldeService initialisationService;

    @PostMapping("/solde")  // ⭐ Changé de "/complet" à "/solde" pour refléter la simplification
    @Operation(summary = "Saisie directe du solde initial constaté (sans calcul de taux)")
    public ResponseEntity<ResultatInitialisationDTO> initialiser(
            @Valid @RequestBody SaisieSoldeInitialDTO dto) {

        ResultatInitialisationDTO result = initialisationService.initialiserSolde(dto);
        return ResponseEntity.ok(result);
    }
}