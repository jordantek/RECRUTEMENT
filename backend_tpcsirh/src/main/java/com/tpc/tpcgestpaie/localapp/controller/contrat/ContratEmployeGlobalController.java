package com.tpc.tpcgestpaie.localapp.controller.contrat;

import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratEmployeGlobalDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratEmployeRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratEmployeResponseDTO;
import com.tpc.tpcgestpaie.localapp.helper.*;
import com.tpc.tpcgestpaie.localapp.service.contrat.ContratGlobalService;
import com.tpc.tpcgestpaie.localapp.service.emailConfig.EmailConfigService;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// Spring Mail

// Thymeleaf

// Fichiers

// Java util
import java.util.Map;

// Ton modèle
import com.tpc.tpcgestpaie.localapp.model.User;

// Exception


@RestController
@RequestMapping("/api/enregistrement-contrat")
public class ContratEmployeGlobalController {

    private final UserService userService;
    private final ContratGlobalService contratService;
    private final ContratEmployeHelper contratHelper;
    private final EmployeDiplomeHelper diplomeHelper;

    @Autowired
    private EmailConfigService emailConfigService;
    @Autowired
    private EmployeService employeService;


    public ContratEmployeGlobalController(
            UserService userService,
            ContratGlobalService contratService,
            ContratEmployeHelper contratHelper,
            EmployeDiplomeHelper diplomeHelper
) {
        this.userService = userService;
        this.contratService = contratService;
        this.contratHelper = contratHelper;
        this.diplomeHelper = diplomeHelper;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createGlobalContrat(@RequestBody ContratEmployeGlobalDTO globalDto) {
        try {
            User currentUser = userService.getCurrentUser();
            List<ErrorResponse> allErrors = new ArrayList<>();

            // ✅ 1. Validation basique des champs (DTO)
            if (globalDto.getContratEmploye() == null) {
                allErrors.add(new ErrorResponse("contratEmploye", "Le contrat employé est requis."));
            } else {
                allErrors.addAll(contratHelper.getInvalidFieldMessages(globalDto.getContratEmploye().toEntity()));
            }

            if (globalDto.getDiplomes() != null) {
                for (var diplome : globalDto.getDiplomes()) {
                    allErrors.addAll(diplomeHelper.getInvalidFieldMessages(diplome.toEntity()));
                }
            }

            if (!allErrors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de validation", allErrors));
            }
            // ✅ 2. Appel du service métier
            Map<String, Object> data = contratService.createGlobalContrat(globalDto, currentUser);

            return new ResponseEntity<>(
                    new ApiResponse<>(true, "Contrat complet créé avec succès", null),
                    HttpStatus.CREATED
            );

        }catch (Exception e) {
            e.printStackTrace(); // pour debug dans la console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur : " + e.getMessage(), null));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getContratById(@PathVariable Long id) {
        try {
            ContratEmployeResponseDTO contrat = contratService.getContratById(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Contrat récupéré avec succès", contrat)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur : " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateContrat(
            @PathVariable Long id,
            @Valid @RequestBody ContratEmployeRequestDTO dto
    ) {
        try {

            // 🔐 User connecté
            User currentUser = userService.getCurrentUser();

            // 🚀 Service
            Map<String, Object> result = contratService.updateContrat(id, dto, currentUser);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Contrat mis à jour avec succès",
                            "result"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Erreur serveur : " + e.getMessage(),
                            null
                    ));
        }
    }

}
