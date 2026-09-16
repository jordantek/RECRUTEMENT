package com.tpc.tpcgestpaie.localapp.controller.contrat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratUpdateDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.StatutContratDTO;
import com.tpc.tpcgestpaie.localapp.repository.*;
import com.tpc.tpcgestpaie.localapp.service.contrat.StatutContratService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/statut-contrat")
public class StatutContratController {

    private final StatutContratService statutContratService;
    private final StatutContratRepository statutContratRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final CategorieEmployeRepository categorieEmployeRepository;
    private final DepartementRepository departementRepository;
    private final PosteRepository posteRepository;

    public StatutContratController(StatutContratService statutContratService, StatutContratRepository statutContratRepository, ContratEmployeRepository contratEmployeRepository, CategorieEmployeRepository categorieEmployeRepository, DepartementRepository departementRepository, PosteRepository posteRepository) {
        this.statutContratService = statutContratService;
        this.statutContratRepository = statutContratRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.categorieEmployeRepository = categorieEmployeRepository;
        this.departementRepository = departementRepository;
        this.posteRepository = posteRepository;
    }

    @PostMapping(value = "/creer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> updateContratQ(
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "preuve", required = false) MultipartFile preuveFile) {

        try {
            // Désérialiser le JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            ContratUpdateDTO dto = objectMapper.readValue(dtoJson, ContratUpdateDTO.class);

            // Attacher le fichier au DTO
            if (preuveFile != null) {
                dto.setPreuve(preuveFile);
            }

            // 🔥 Le service retourne déjà un DTO
            StatutContratDTO statutDTO = statutContratService.archiverEtModifierContrat(dto);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Contrat mis à jour avec succès", statutDTO)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Erreur de validation", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", e.getMessage()));
        }
    }



    @GetMapping("/employe/{employeId}")
    public ResponseEntity<ApiResponse<?>> getStatutsByEmploye(@PathVariable Long employeId) {
        try {
            List<StatutContratDTO> statuts = statutContratService.getStatutsByEmploye(employeId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des statuts de l'employé", statuts)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", e.getMessage()));
        }
    }
}
