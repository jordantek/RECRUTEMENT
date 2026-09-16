package com.tpc.tpcgestpaie.localapp.controller.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpc.tpcgestpaie.localapp.dto.util.CompanyInitRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.util.CompanyInitResponseDTO;
import com.tpc.tpcgestpaie.localapp.dto.util.EmailConfigCreateDTO;
import com.tpc.tpcgestpaie.localapp.model.util.EmailConfig;
import com.tpc.tpcgestpaie.localapp.service.util.CompanyInitService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/setup")
public class CompanyInitController {

    private final CompanyInitService companyInitService;

    public CompanyInitController(CompanyInitService companyInitService) {
        this.companyInitService = companyInitService;
    }

    @GetMapping("/initial-exists")
    public ResponseEntity<?> initialConfigExists() {
        try {
            boolean exists = companyInitService.initialConfigExists();
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Vérification de la configuration initiale effectuée", exists)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la vérification de la configuration", null));
        }
    }

    @PostMapping(value = "/company", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> initCompany(
            @RequestPart("payload") String rawJson,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile) {

        try {
            // 1️⃣ Parser le JSON en DTO
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            CompanyInitRequestDTO payload = objectMapper.readValue(rawJson, CompanyInitRequestDTO.class);

            // 2️⃣ Vérifier si initial config existe
            if (companyInitService.initialConfigExists()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "La configuration initiale existe déjà pour cette entreprise.", null));
            }

            // 3️⃣ Appel du service en passant le DTO et le fichier logo
            CompanyInitResponseDTO resp = companyInitService.initCompany(payload, logoFile);

            // 4️⃣ Retour normal
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Entreprise créée avec succès", resp)
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Erreur lors de la création de l'entreprise: " + ex.getMessage(), null)
            );
        }
    }

    @PutMapping("/email-config/{id}")
    public ResponseEntity<?> updateEmailConfig(
            @PathVariable Long id,
            @RequestBody EmailConfigCreateDTO payload) {

        try {
            EmailConfig updated = companyInitService.updateEmailConfig(id, payload);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Configuration email mise à jour avec succès", updated)
            );

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur lors de la mise à jour de la configuration email: " + ex.getMessage(), null));
        }
    }


}
