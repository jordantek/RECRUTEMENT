    package com.tpc.tpcgestpaie.localapp.controller.paie;

    import com.tpc.tpcgestpaie.localapp.dto.paie.MensualiteDTO;
    import com.tpc.tpcgestpaie.localapp.helper.MensualiteHelper;
    import com.tpc.tpcgestpaie.localapp.model.*;
    import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
    import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
    import com.tpc.tpcgestpaie.localapp.repository.RubriqueRepository;
    import com.tpc.tpcgestpaie.localapp.repository.paie.InstitutionRepository;
    import com.tpc.tpcgestpaie.localapp.repository.paie.MensualiteRepository;
    import com.tpc.tpcgestpaie.localapp.service.EmployeService;
    import com.tpc.tpcgestpaie.localapp.service.RubriqueService;
    import com.tpc.tpcgestpaie.localapp.service.paie.MensualiteService;
    import com.tpc.tpcgestpaie.localapp.service.CompanyService;
    import com.tpc.tpcgestpaie.localapp.service.UserService;
    import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
    import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.bind.annotation.*;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/paie/mensualites")
    public class MensualiteController {

        private final MensualiteService mensualiteService;
        private final MensualiteHelper mensualiteHelper;
        private final MensualiteRepository mensualiteRepository;
        private final UserService userService;
        private final CompanyService companyService;
        private final ContratEmployeRepository contratEmployeRepository;
        private final EmployeRepository employeRepository;
        private final InstitutionRepository institutionRepository;
        private final RubriqueRepository rubriqueRepository;
        private final RubriqueService rubriqueService;
        private final EmployeService employeService;
        private Long id;

        public MensualiteController(MensualiteService mensualiteService,
                                    MensualiteHelper mensualiteHelper,
                                    MensualiteRepository mensualiteRepository,
                                    UserService userService,
                                    CompanyService companyService, ContratEmployeRepository contratEmployeRepository, EmployeRepository employeRepository, InstitutionRepository institutionRepository, RubriqueRepository rubriqueRepository, RubriqueService rubriqueService, EmployeService employeService) {
            this.mensualiteService = mensualiteService;
            this.mensualiteHelper = mensualiteHelper;
            this.mensualiteRepository = mensualiteRepository;
            this.userService = userService;
            this.companyService = companyService;
            this.contratEmployeRepository = contratEmployeRepository;
            this.employeRepository = employeRepository;
            this.institutionRepository = institutionRepository;
            this.rubriqueRepository = rubriqueRepository;
            this.rubriqueService = rubriqueService;
            this.employeService = employeService;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<MensualiteDTO>>> getAll() {
            List<MensualiteDTO> list = mensualiteService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des mensualités récupérée", list));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<MensualiteDTO>> getById(@PathVariable Long id) {
            Optional<MensualiteDTO> opt = mensualiteService.findById(id);
            if (opt.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Mensualité trouvée", opt.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Mensualité non trouvée", null));
        }

        @Transactional
        @PostMapping
        public ResponseEntity<?> create(@RequestBody MensualiteDTO dto) {
            try {
                System.out.println("début mensualité: " );
                List<ErrorResponse> errors = new ArrayList<>();
                User currentUser = userService.getCurrentUser();

                if (dto.getMontantMensuel() == null) {
                    errors.add(new ErrorResponse("montant", "Le montant est requis."));
                }
                if (dto.getCompanyId() == null) {
                    errors.add(new ErrorResponse("companyId", "L'entreprise est requise."));
                }
                if (dto.getContratEmployeId() == null) {
                    errors.add(new ErrorResponse("contratEmployeId", "Le contrat employé est requis."));
                }

                if (dto.getInstitutionId() == null) {
                    errors.add(new ErrorResponse("institutionId", "L'institution est requise."));
                }

                if (!errors.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs requis manquants", errors));
                }

                // Vérifier existence du contrat employé
                Optional<ContratEmploye> contratOpt = contratEmployeRepository.findById(dto.getContratEmployeId());
                if (contratOpt.isEmpty()) {
                    errors.add(new ErrorResponse("contratEmployeId", "Contrat employé introuvable."));
                    return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entités référencées introuvables", errors));
                }

                ContratEmploye contratEmploye = contratOpt.get();

                // Récupérer l'employé depuis le contrat
                Employe employe = contratEmploye.getEmploye();
                if (employe == null) {
                    errors.add(new ErrorResponse("employe", "Employé introuvable dans le contrat."));
                    return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entités référencées introuvables", errors));
                }

                Company company = companyService.getById(dto.getCompanyId());
                if (company == null) {
                    errors.add(new ErrorResponse("companyId", "Entreprise introuvable."));
                }

                if (!institutionRepository.existsById(dto.getInstitutionId())) {
                    errors.add(new ErrorResponse("institutionId", "Institution introuvable."));
                }
                if (!errors.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entités référencées introuvables", errors));
                }

    //            Rubrique rubrique = rubriqueRepository.findById(dto.getRubriqueId()).get();

                Optional<Rubrique> rubrique = rubriqueService.findByLibelle("MENSUALITES");
//                dto.setRubriqueId(rubrique.get().getId());
                Institution institution = institutionRepository.findById(dto.getInstitutionId()).get();

                // Construction de l'entité mensuailte avec l'employé récupéré depuis contrat
                Mensualite entity = mensualiteService.toEntity(
                        dto,
                        contratEmploye,
                        employe,
                        company,
                        institution,
                        currentUser
                );
               List<ErrorResponse> validationErrors = mensualiteHelper.getInvalidFieldMessages(entity);
                if (!validationErrors.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", validationErrors));
                }
                MensualiteDTO saved = mensualiteService.save(dto);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(true, "Mensualité créée avec succès", saved));


            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(false, "Erreur lors de la création de la mensualité", null));
            }
        }

        @PutMapping("/{id}")
        public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MensualiteDTO dto) {
            try {
                Optional<MensualiteDTO> existingOpt = mensualiteService.findById(id);
                if (existingOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Mensualité non trouvée", null));
                }

                // Validation basique des champs obligatoires
                List<ErrorResponse> errors = new ArrayList<>();
                if (dto.getMontantMensuel() == null) {
                    errors.add(new ErrorResponse("montant", "Le montant est requis."));
                }
                if (dto.getCompanyId() == null) {
                    errors.add(new ErrorResponse("companyId", "L'entreprise est requise."));
                }
                if (!errors.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs requis manquants", errors));
                }

                User currentUser = userService.getCurrentUser();

                // On récupère la company juste pour validation, mais on utilise la méthode update du service qui fait tout
                Company company = companyService.getById(dto.getCompanyId());
                if (company == null) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));
                }

                // Valide les règles métiers sur l'entité construite à partir du DTO
                Mensualite entity = mensualiteService.toEntity(dto,
                        contratEmployeRepository.findById(dto.getContratEmployeId()).orElse(null),
                        employeRepository.findById(dto.getEmployeId()).orElse(null),
                        company,
                        institutionRepository.findById(dto.getInstitutionId()).orElse(null),
                        currentUser);

                List<ErrorResponse> validationErrors = mensualiteHelper.getInvalidFieldMessages(entity);
                if (!validationErrors.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", validationErrors));
                }

                dto.setUpdatedAt(LocalDateTime.now());

                // Appel à la méthode update du service
                MensualiteDTO saved = mensualiteService.update(id, dto);

                return ResponseEntity.ok(new ApiResponse<>(true, "Mensualité mise à jour avec succès", saved));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(false, "Erreur lors de la mise à jour de la mensualité", null));
            }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
            if (!mensualiteService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Mensualité non trouvée", null));
            }
            mensualiteService.delete(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Mensualité supprimée avec succès", null));
        }

        @GetMapping("/par-entreprise/{companyId}")
        public ResponseEntity<ApiResponse<List<MensualiteDTO>>> getByCompany(@PathVariable Long companyId) {
            if (!companyService.existsById(companyId)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }
            List<MensualiteDTO> list = mensualiteService.findByCompanyId(companyId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des mensualités de l'entreprise", list));
        }

        @GetMapping("/par-employe/{employeId}")
        public ResponseEntity<ApiResponse<List<MensualiteDTO>>> getByEmploye(@PathVariable Long employeId) {
            // Vérifie que l'employé existe
            if (!employeService.existsById(employeId)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Employé introuvable", null));
            }

            // Récupère les mensualités pour l'employé
            List<MensualiteDTO> mensualites = mensualiteService.getMensualitesParEmploye(employeId);

            // Retourne la réponse
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des mensualités de l'employé", mensualites)
            );
        }

        @PutMapping("/solder/{id}")
        public ResponseEntity<?> solder(@PathVariable Long id) {
            try {
                Optional<Mensualite> optMensualite = mensualiteRepository.findById(id);
                if (optMensualite.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(
                                    "Mensualité non trouvée",
                                    "NOT_FOUND",
                                    List.of("Aucune mensualité trouvée avec l’ID " + id)
                            ));
                }

                mensualiteService.setEstSoldee(id);
                return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        "Mensualité soldée avec succès",
                        "OK"
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(
                                "Erreur lors de la validation de la mensualité",
                                "VALIDATION_ERROR",
                                List.of(e.getMessage())
                        ));
            }
        }
        @DeleteMapping("/delete/{id}")
        public ResponseEntity<?> deleteMensualite(@PathVariable Long id) {
            try {
                mensualiteService.softDelete(id);
                return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        "Mensualité supprimée avec succès",
                        "OK"
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(
                                "Erreur lors de la suppression de la mensualité",
                                "VALIDATION_ERROR",
                                List.of(e.getMessage())
                        ));
            }
        }

        @PutMapping("/restaurer/{id}")
        public ResponseEntity<?> restaurer(@PathVariable Long id) {
            try {
                Optional<Mensualite> optMensualite = mensualiteRepository.findById(id);
                if (optMensualite.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(
                                    "Mensualité non trouvée",
                                    "NOT_FOUND",
                                    List.of("Aucune mensualité trouvée avec l’ID " + id)
                            ));
                }

                mensualiteService.restauterSolde(id);
                return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        "Mensualité restaurée avec succès",
                        "OK"
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(
                                "Erreur lors de la restauration de la mensualité",
                                "VALIDATION_ERROR",
                                List.of(e.getMessage())
                        ));
            }
        }



    }
