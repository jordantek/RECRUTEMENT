package com.tpc.tpcgestpaie.localapp.controller.common;

import com.tpc.tpcgestpaie.localapp.model.Tva;
import com.tpc.tpcgestpaie.localapp.service.TvaService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/common/tva")
public class TvaController {

    private final TvaService tvaService;

    public TvaController(TvaService tvaService) {
        this.tvaService = tvaService;
    }


    @GetMapping("/list")
    public ResponseEntity<?> getAllTauxTva() {
        try {
            List<Tva> tauxList = tvaService.getAllTauxTva();

            ApiResponse<List<Tva>> response = new ApiResponse<>(
                    true,
                    "Liste des taux de TVA récupérée avec succès",
                    tauxList
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Object> errorResponse = new ApiResponse<>(
                    false,
                    "Une erreur est survenue lors de la récupération des notifications : " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
