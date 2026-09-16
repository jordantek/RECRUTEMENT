package com.tpc.tpcgestpaie.localapp.controller.employe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpc.tpcgestpaie.localapp.dto.EnfantEmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.EnfantEmploye;
import com.tpc.tpcgestpaie.localapp.service.EnfantEmployeService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/arh/employes/enfant")
public class EnfantEmployeController {

    private final EnfantEmployeService enfantEmployeService;

    public EnfantEmployeController(EnfantEmployeService enfantEmployeService) {
        this.enfantEmployeService = enfantEmployeService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<EnfantEmployeDTO> enfantDtos = enfantEmployeService.findAll()
                    .stream().map(enfantEmployeService::getEnfant).toList();

            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des enfants récupérée avec succès", enfantDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération : " + e.getMessage(), null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> requestBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Enregistrement du module pour gérer LocalDate/LocalDateTime
            mapper.registerModule(new JavaTimeModule());

            Object enfantData = requestBody.get("enfant");
            EnfantEmployeDTO enfantDTO = mapper.convertValue(enfantData, EnfantEmployeDTO.class);

            // Conversion manuelle vers l'entité EnfantEmploye
            EnfantEmploye enfant = new EnfantEmploye();
            enfant.setNom(enfantDTO.getNom());
            enfant.setPrenom(enfantDTO.getPrenom());
            enfant.setSexe(enfantDTO.getSexe());
            enfant.setDateNaissance(enfantDTO.getDateNaissance());
            enfant.setLieuNaissance(enfantDTO.getLieuNaissance());// LocalDate est supporté

            // Récupération et conversion de l'ID employé
            Object employeIdRaw = requestBody.get("employeId");
            if (employeIdRaw == null) {
                throw new IllegalArgumentException("L'ID de l'employé est requis.");
            }
            Long employeId = ((Number) employeIdRaw).longValue();

            EnfantEmploye created = enfantEmployeService.create(enfant, employeId);
            EnfantEmployeDTO dto = enfantEmployeService.getEnfant(created);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Enfant créé avec succès", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur lors de la création : " + e.getMessage(), null));
        }
    }

    @GetMapping("/by-employe/{employeId}")
    public ResponseEntity<?> getByEmployeId(@PathVariable Long employeId) {
        try {
            List<EnfantEmployeDTO> enfantDtos = enfantEmployeService.findByEmployeId(employeId)
                    .stream().map(enfantEmployeService::getEnfant).toList();

            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des enfants de l'employé récupérée avec succès", enfantDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération : " + e.getMessage(), null));
        }
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            EnfantEmploye enfant = enfantEmployeService.findById(id);
            EnfantEmployeDTO dto = enfantEmployeService.getEnfant(enfant);
            return ResponseEntity.ok(new ApiResponse<>(true, "Enfant trouvé", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Enfant introuvable : " + e.getMessage(), null));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> requestBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            Object enfantData = requestBody.get("enfant");
            EnfantEmployeDTO enfantDTO = mapper.convertValue(enfantData, EnfantEmployeDTO.class);

            // Conversion DTO -> entité
            EnfantEmploye enfant = new EnfantEmploye();
            enfant.setNom(enfantDTO.getNom());
            enfant.setPrenom(enfantDTO.getPrenom());
            enfant.setDateNaissance(enfantDTO.getDateNaissance());
            enfant.setLieuNaissance(enfantDTO.getLieuNaissance());
            enfant.setSexe(enfantDTO.getSexe());
            enfant.setEstDecede(enfantDTO.getEstDecede());

            EnfantEmploye updated = enfantEmployeService.update(id, enfant);
            EnfantEmployeDTO dto = enfantEmployeService.getEnfant(updated);

            return ResponseEntity.ok(new ApiResponse<>(true, "Enfant mis à jour", dto));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur lors de la mise à jour : " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            enfantEmployeService.softDelete(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Enfant supprimé avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la suppression : " + e.getMessage(), null));
        }
    }
}
