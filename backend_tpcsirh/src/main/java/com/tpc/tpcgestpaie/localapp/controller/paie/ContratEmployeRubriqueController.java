package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.dto.contrat.RubriqueDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.ContratEmployeRubriqueDTO;
import com.tpc.tpcgestpaie.localapp.helper.ContratEmployeRubriqueHelper;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.ContratEmployeRubrique;
import com.tpc.tpcgestpaie.localapp.model.Rubrique;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.RubriqueRepository;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/paie/contrats-employes-rubriques")
public class ContratEmployeRubriqueController {

    private final ContratEmployeRubriqueService contratRubriqueService;
    private final ContratEmployeRubriqueHelper contratRubriqueHelper;
    private final ContratEmployeRepository contratEmployeRepository;
    private final RubriqueRepository rubriqueRepository;
    private final UserService userService;
    private final RequestHelper requestHelper;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final ContratEmployeService contratEmployeService;
    private final ContratEmployeRepository contratRepository;

    public ContratEmployeRubriqueController(
            ContratEmployeRubriqueService contratRubriqueService,
            ContratEmployeRubriqueHelper contratRubriqueHelper, ContratEmployeRepository contratEmployeRepository, RubriqueRepository rubriqueRepository,
            UserService userService,
            RequestHelper requestHelper,
            AuditLogService auditService,
            NotificationService notificationService, ContratEmployeService contratEmployeService, ContratEmployeRepository contratRepository
    ) {
        this.contratRubriqueService = contratRubriqueService;
        this.contratRubriqueHelper = contratRubriqueHelper;
        this.contratEmployeRepository = contratEmployeRepository;
        this.rubriqueRepository = rubriqueRepository;
        this.userService = userService;
        this.requestHelper = requestHelper;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.contratEmployeService = contratEmployeService;
        this.contratRepository = contratRepository;
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<ContratEmployeRubriqueDTO> list = contratRubriqueService.getAllRubriques();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des rubriques contrat employé récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des rubriques", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

@GetMapping("/{id}/employe")
public ResponseEntity<?> getRubriquesDto(@PathVariable Long id) {
     try {
         if (id == null || !contratRepository.existsById(id)) {
             return new ResponseEntity<>(new ApiResponse<>(false, "Contrat employé introuvable", null), HttpStatus.NOT_FOUND);

         }
        List<RubriqueDTO> rubriques = contratEmployeService.getRubriquesDtoByContratId(id);
        if (rubriques.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Rubrique vide", null), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Rubriques trouvées", rubriques));

    } catch (Exception e) {
        return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la rubrique", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<ContratEmployeRubriqueDTO> dto = Optional.ofNullable(contratRubriqueService.getById(id));
            if (dto.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Rubrique trouvée", dto.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Rubrique non trouvée", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la rubrique", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ContratEmployeRubriqueDTO dto) {
        try {
            User currentUser = userService.getCurrentUser();

            List<ErrorResponse> errors = new ArrayList<>();

            // Vérifier que l'id du contrat employé est présent
            if (dto.getContratEmployeId() == null) {
                errors.add(new ErrorResponse("contrat_employe", "Le contrat employé est obligatoire"));
            }

            // Vérifier que l'id de la rubrique est présent
            if (dto.getRubriqueId() == null) {
                errors.add(new ErrorResponse("rubrique", "La rubrique est obligatoire"));
            }

            // Si des erreurs de champ manquant sont détectées, les retourner immédiatement
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Champs obligatoires manquants", errors));
            }

            // Puis récupérer les entités en base
            Optional<ContratEmploye> contratOpt = contratEmployeRepository.findById(dto.getContratEmployeId());
            if (contratOpt.isEmpty()) {
                errors.add(new ErrorResponse("contrat_employe_inexistant", "Contrat employé non trouvé"));
            }

            Optional<Rubrique> rubriqueOpt = rubriqueRepository.findById(dto.getRubriqueId());
            if (rubriqueOpt.isEmpty()) {
                errors.add(new ErrorResponse("rubrique_inexistante", "Rubrique non trouvée"));
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de cohérence des données", errors));
            }

            ContratEmploye contratEmploye = contratOpt.get();
            Rubrique rubrique = rubriqueOpt.get();

            // Construire l'entité complète à valider
            ContratEmployeRubrique entity = dto.toEntity(contratEmploye, rubrique, currentUser,contratEmploye.getCompany());

            // Validation métier via helper
            List<ErrorResponse> validationErrors = contratRubriqueHelper.getInvalidFieldMessages(entity);
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de validation", validationErrors));
            }

            // Ajouter métadonnées sur le DTO (date, user)
            dto.setCreatedAt(LocalDateTime.now());
            dto.setAddedById(currentUser.getId());

            // Sauvegarde via le service
            ContratEmployeRubriqueDTO saved = contratRubriqueService.save(dto, currentUser);

            return new ResponseEntity<>(new ApiResponse<>(true, "Rubrique créée avec succès", saved), HttpStatus.CREATED);

        } catch (Exception e) {
            // Facultatif : logger l’erreur
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création de la rubrique", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ContratEmployeRubriqueDTO updatedDto) {
        try {
            Optional<ContratEmployeRubriqueDTO> existingOpt = Optional.ofNullable(contratRubriqueService.getById(id));
            if (existingOpt.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Rubrique non trouvée", null), HttpStatus.NOT_FOUND);
            }

            User currentUser = userService.getCurrentUser();

            // Récupérer les entités liées pour validation
            Optional<ContratEmploye> contratOpt = contratEmployeRepository.findById(updatedDto.getContratEmployeId());
            if (contratOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Contrat employé non trouvé", null));
            }
            ContratEmploye contratEmploye = contratOpt.get();

            Optional<Rubrique> rubriqueOpt = rubriqueRepository.findById(updatedDto.getRubriqueId());
            if (rubriqueOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Rubrique non trouvée", null));
            }
            Rubrique rubrique = rubriqueOpt.get();

            // Construire l’entité complète à valider
            ContratEmployeRubrique entityToValidate = updatedDto.toEntity(contratEmploye, rubrique, currentUser,contratEmploye.getCompany());

            // Validation via helper
            List<ErrorResponse> errors = contratRubriqueHelper.getInvalidFieldMessages(entityToValidate);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            // Mettre à jour les champs dans le DTO existant
            ContratEmployeRubriqueDTO toUpdate = existingOpt.get();

            toUpdate.setMontant(updatedDto.getMontant());
            toUpdate.setDateDebut(updatedDto.getDateDebut());
            toUpdate.setDateFin(updatedDto.getDateFin());
            toUpdate.setStatut(updatedDto.getStatut());
            toUpdate.setContratEmployeId(updatedDto.getContratEmployeId());
            toUpdate.setRubriqueId(updatedDto.getRubriqueId());
            toUpdate.setUpdatedAt(LocalDateTime.now());
            toUpdate.setAddedById(currentUser.getId());

            ContratEmployeRubriqueDTO saved = contratRubriqueService.save(toUpdate,currentUser,contratEmploye.getCompany().getId());

            auditService.log(
                    "Modification d’une rubrique contrat employé",
                    "contrats_employes_rubriques",
                    currentUser.getId(),
                    "Modification d’une rubrique contrat employé par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Rubrique modifiée",
                    "La rubrique contrat employé a été modifiée avec succès ✏️"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Rubrique mise à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour de la rubrique", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<ContratEmployeRubriqueDTO> existing = Optional.ofNullable(contratRubriqueService.getById(id));
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Rubrique non trouvée", null), HttpStatus.NOT_FOUND);
            }

            User currentUser = userService.getCurrentUser();
            ContratEmployeRubriqueDTO rubrique = existing.get();

            // Suppression logique (ou physique selon ta méthode)
            // rubrique.setDeletedAt(LocalDateTime.now());
            contratRubriqueService.save(rubrique,currentUser);

            auditService.log(
                    "Suppression d’une rubrique contrat employé",
                    "contrats_employes_rubriques",
                    currentUser.getId(),
                    "Suppression d’une rubrique contrat employé par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Rubrique supprimée",
                    "La rubrique contrat employé a été supprimée avec succès 🗑️"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Rubrique supprimée avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression de la rubrique", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
