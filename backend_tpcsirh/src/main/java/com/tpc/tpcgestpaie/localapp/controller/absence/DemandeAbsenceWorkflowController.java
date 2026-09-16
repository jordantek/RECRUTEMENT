package com.tpc.tpcgestpaie.localapp.controller.absence;

import com.tpc.tpcgestpaie.localapp.dto.absence.*;
import com.tpc.tpcgestpaie.localapp.enums.UniteAbsence;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.absence.SuiviAbsence;
import com.tpc.tpcgestpaie.localapp.model.absence.ValidationNiveau;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerie;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.absence.DemandeAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.absence.SuiviAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.absence.ValidationNiveauRepository;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.absence.CalculJoursAbsenceService;
import com.tpc.tpcgestpaie.localapp.service.absence.DemandeAbsenceWorkflowService;
import com.tpc.tpcgestpaie.localapp.service.jourFerie.JourFerieEntrepriseService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Tag(
        name = "Gestion administrative/Demandes d'Absence",
        description = "API complète pour la gestion du cycle de vie des demandes d'absence avec validation hiérarchique dynamique et suivi temps réel"
)
@RestController
@RequestMapping("/api/workflow-absence")
@RequiredArgsConstructor
@Validated
public class DemandeAbsenceWorkflowController {

    private final DemandeAbsenceWorkflowService workflowService;
    private final UserService userService;
    private final EmployeRepository employeRepository;
    private final DemandeAbsenceRepository demandeAbsenceRepository;
    private final CalculJoursAbsenceService calculJoursAbsenceService;
    private final ValidationNiveauRepository validationNiveauRepository;
    private final SuiviAbsenceRepository suiviAbsenceRepository;
    private final JourFerieEntrepriseService jourFerieEntrepriseService;

    // ========================================
    // 1. CRÉATION DE DEMANDE
    // ========================================

    @Operation(
            summary = "Créer une nouvelle demande d'absence",
            description = "Crée une demande avec récupération automatique de la hiérarchie depuis employe_superieur. " +
                    "Tous les validateurs sont notifiés dès la création."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Demande créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"success\": true, \"message\": \"Demande créée avec succès\", " +
                                            "\"data\": {\"demande\": {...}, \"niveauxValidation\": 3}}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Données invalides, doublon ou chevauchement",
                    content = @Content
            )
    })
    @PostMapping(value = "/creer", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> creerDemande(
            @ModelAttribute @Valid DemandeAbsenceCreateDTO dto) {
        try {
            DemandeAbsence demande = workflowService.creerDemande(dto);
            DemandeAbsenceResponseDTO response = new DemandeAbsenceResponseDTO(demande);

            Map<String, Object> data = new HashMap<>();
            data.put("demande", response);
            data.put("niveauxValidation", demande.getHierarchieValidation().getNiveaux().size());
            data.put("message", "Tous les validateurs ont été notifiés");

            return ResponseEntity.ok(new ApiResponse<>(true, "Demande créée avec succès", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ========================================
    // 2. VALIDATION PAR NIVEAU
    // ========================================

    @Operation(
            summary = "Valider ou rejeter une demande à un niveau donné",
            description = "Le validateur du niveau en cours peut approuver ou rejeter."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Validation effectuée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    @PostMapping("/valider-absences")
    public ResponseEntity<ApiResponse<?>> validerParNiveau(
            @Valid @RequestBody ValidationNiveauDTO dto) {

        try {
            User currentUser = userService.getCurrentUser();
            Long validateurId = currentUser.getEmploye().getId();

            DemandeAbsence demandeAbsence = demandeAbsenceRepository.getById(dto.getDemandeId());
            if (demandeAbsence == null) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Demande non trouvée", null));
            }

            // ✅ VERIFICATION AVANT APPEL SERVICE
            if (Boolean.FALSE.equals(dto.getAccepter())) {
                if (dto.getRaisonRejet() == null || dto.getRaisonRejet().trim().isEmpty()) {
                    return ResponseEntity.ok(
                            new ApiResponse<>(false,
                                    "La raison du rejet est obligatoire lorsque la demande est refusée.",
                                    null)
                    );
                }
            }

            workflowService.validerParNiveau(dto, validateurId);
            DemandeAbsence demande = workflowService.getDemandeById(dto.getDemandeId());

            Map<String, Object> data = new HashMap<>();
            data.put("demandeId", demande.getId());
            data.put("statutActuel", demande.getStatut().name());

            String message;

            if (dto.getAccepter()) {
                if (demande.getNiveauValidationEnCours() != null) {
                    data.put("prochainNiveau", demande.getNiveauValidationEnCours());
                    message = "Demande approuvée, transmise au niveau "
                            + demande.getNiveauValidationEnCours();
                } else {
                    message = "Demande approuvée définitivement";
                }
            } else {
                data.put("raisonRejet", dto.getRaisonRejet());
                message = "Demande rejetée. Tous les validateurs ont été notifiés.";
            }

            return ResponseEntity.ok(new ApiResponse<>(true, message, data));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ========================================
    // 3. CONFIRMATION DE DÉPART
    // ========================================

    @Operation(
            summary = "Confirmer le départ effectif en congé",
            description = "L'employé confirme son départ. Pour les heures : heure de début du premier jour."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Départ confirmé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    @PostMapping("/confirmer-depart")
    public ResponseEntity<ApiResponse<?>> confirmerDepart(
            @Valid @RequestBody ConfirmationDepartDTO dto) {
        try {
            User currentUser = userService.getCurrentUser();
            Long employeId = currentUser.getEmploye().getId();

            workflowService.confirmerDepart(dto, employeId);

            DemandeAbsence demande = workflowService.getDemandeById(dto.getDemandeId());
            SuiviAbsence suivi = workflowService.getSuiviByDemandeId(dto.getDemandeId());

            Map<String, Object> data = new HashMap<>();
            data.put("datePrevue", suivi.getDateDepartPrevue());
            data.put("dateEffective", suivi.getDateDepartEffective());
            data.put("heureEffective", suivi.getHeureDepartEffective());
            data.put("statutActuel", demande.getStatut().name());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Départ confirmé. Supérieurs notifiés.", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ========================================
    // 4. CONFIRMATION DE RETOUR
    // ========================================

    @Operation(
            summary = "Confirmer le retour effectif au travail",
            description = "L'employé confirme son retour. Pour les heures : heure de fin du dernier jour. " +
                    "Le système calcule automatiquement la durée totale."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Retour confirmé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    @PostMapping("/confirmer-retour")
    public ResponseEntity<ApiResponse<?>> confirmerRetour(
            @Valid @RequestBody ConfirmationRetourDTO dto) {
        try {
            User currentUser = userService.getCurrentUser();
            Long employeId = currentUser.getEmploye().getId();

            workflowService.confirmerRetour(dto, employeId);

            DemandeAbsence demande = workflowService.getDemandeById(dto.getDemandeId());
            SuiviAbsence suivi = workflowService.getSuiviByDemandeId(dto.getDemandeId());

            Map<String, Object> data = new HashMap<>();
            data.put("joursPrevu", suivi.getJoursPrevu());
            data.put("joursEffectifs", suivi.getJoursEffectifs());
            data.put("heuresPrevu", suivi.getHeuresPrevu());
            data.put("heuresEffectifs", suivi.getHeuresEffectifs());
            data.put("ecartJours", suivi.getEcartJours());
            data.put("ecartHeures", suivi.getEcartHeures());
            data.put("dateDepartEffective", suivi.getDateDepartEffective());
            data.put("dateRetourEffective", suivi.getDateRetourEffective());
            data.put("statutActuel", demande.getStatut().name());

            String message = "Retour confirmé. ";
            if (suivi.getEcartJours() != null && suivi.getEcartJours() > 0) {
                message += suivi.getEcartJours() + " jour(s) de moins que prévu.";
            } else if (suivi.getEcartJours() != null && suivi.getEcartJours() < 0) {
                message += Math.abs(suivi.getEcartJours()) + " jour(s) de plus que prévu.";
            } else {
                message += "Durée conforme au prévu.";
            }

            return ResponseEntity.ok(new ApiResponse<>(true, message, data));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ========================================
    // 5. REPORTER UNE DEMANDE
    // ========================================

    @Operation(
            summary = "Reporter une demande approuvée à de nouvelles dates",
            description = "Permet de modifier les dates d'un congé approuvé mais pas encore commencé."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande reportée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    @PostMapping("/reporter")
    public ResponseEntity<ApiResponse<?>> reporterDemande(
            @Valid @RequestBody ReportAbsenceDTO dto) {
        try {
            User currentUser = userService.getCurrentUser();
            Long employeId = currentUser.getEmploye().getId();

            DemandeAbsence demandeAvant = workflowService.getDemandeById(dto.getDemandeId());
            String anciennesDates = demandeAvant.getDateDebut() + " au " + demandeAvant.getDateFin();

            workflowService.reporterDemande(dto, employeId);
            DemandeAbsence demandeApres = workflowService.getDemandeById(dto.getDemandeId());

            Map<String, Object> data = new HashMap<>();
            data.put("anciennesDates", anciennesDates);
            data.put("nouvellesDates", demandeApres.getDateDebut() + " au " + demandeApres.getDateFin());
            data.put("nouveauxJours", demandeApres.getNombreJours());
            data.put("statutActuel", demandeApres.getStatut().name());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Demande reportée. Supérieurs notifiés.", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ========================================
    // 6. RÉCUPÉRATION - MES VALIDATIONS
    // ========================================

    @Operation(
            summary = "Récupérer mes demandes en attente de validation",
            description = "Retourne les demandes où je suis validateur avec filtrage possible."
    )
    @GetMapping("/mes-validations")
    public ResponseEntity<ApiResponse<?>> getMonHistoriqueValidations(
            @RequestParam(required = false) String filtre) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser.getEmploye() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Aucun employé associé", null));
            }

            Long employeId = currentUser.getEmploye().getId();
            List<DemandeValidationEnAttenteDTO> historique = workflowService.getHistoriqueValidations(employeId);

            List<DemandeValidationEnAttenteDTO> resultatFiltre = historique;
            if (filtre != null && !"TOUS".equals(filtre)) {
                resultatFiltre = historique.stream()
                        .filter(d -> filtre.equals(d.getCategorie()))
                        .collect(Collectors.toList());
            }

            Map<String, Long> stats = historique.stream()
                    .collect(Collectors.groupingBy(
                            DemandeValidationEnAttenteDTO::getCategorie,
                            Collectors.counting()
                    ));

            Map<String, Object> data = new HashMap<>();
            data.put("historique", resultatFiltre);
            data.put("total", resultatFiltre.size());
            data.put("statistiques", stats);
            data.put("aValider", historique.stream().filter(DemandeValidationEnAttenteDTO::isEstEnAttenteDeMoi).count());
            data.put("dejaValide", historique.stream().filter(DemandeValidationEnAttenteDTO::isEstTermineePourMoi).count());

            return ResponseEntity.ok(new ApiResponse<>(true, "Historique récupéré", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur technique", null));
        }
    }

    // ========================================
    // 7. DÉTAILS D'UNE DEMANDE
    // ========================================

    @Operation(
            summary = "Récupérer les détails complets d'une demande",
            description = "Retourne la demande avec hiérarchie, validations, suivi et calcul des jours."
    )
    @GetMapping("/details/{demandeId}")
    public ResponseEntity<ApiResponse<?>> getDemandeDetails(
            @Parameter(description = "ID de la demande", required = true)
            @PathVariable Long demandeId) {
        try {
            DemandeAbsence demande = workflowService.getDemandeById(demandeId);
            List<ValidationNiveau> validations = validationNiveauRepository.findByDemandeAbsenceId(demandeId);
            Optional<SuiviAbsence> suiviOpt = suiviAbsenceRepository.findByDemandeAbsenceId(demandeId);
            CalculJoursAbsenceService.CalculJoursAbsenceResultat calcul =
                    calculJoursAbsenceService.calculerJoursAbsenceReels(demande);

            DemandeAbsenceResponseDTO response = new DemandeAbsenceResponseDTO(
                    demande, validations, suiviOpt.orElse(null), calcul);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Détails récupérés avec succès", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Demande non trouvée: " + e.getMessage(), null));
        }
    }

    // ========================================
    // 8. SUIVI D'UNE DEMANDE
    // ========================================

    @Operation(
            summary = "Récupérer le suivi temps réel d'une demande",
            description = "Retourne les informations de suivi : dates effectives, confirmations, etc."
    )
    @GetMapping("/{demandeId}/suivi")
    public ResponseEntity<ApiResponse<?>> getSuiviDemande(@PathVariable Long demandeId) {
        try {
            SuiviAbsence suivi = workflowService.getSuiviByDemandeId(demandeId);
            SuiviAbsenceResponseDTO response = new SuiviAbsenceResponseDTO(suivi);
            return ResponseEntity.ok(new ApiResponse<>(true, "Suivi récupéré", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Suivi non trouvé", null));
        }
    }

    // ========================================
    // 9. TABLEAU DE BORD VALIDATEUR
    // ========================================

    @Operation(
            summary = "Tableau de bord pour validateur",
            description = "Vue d'ensemble des demandes à valider."
    )
    @GetMapping("/dashboard-validateur")
    public ResponseEntity<ApiResponse<?>> getDashboardValidateur() {
        try {
            User currentUser = userService.getCurrentUser();
            Long employeId = currentUser.getEmploye().getId();

            List<DemandeAbsence> enAttente = workflowService.getDemandesEnAttenteValidationPourEmploye(employeId);

            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("enAttente", enAttente.size());
//            dashboard.put("demandesEnAttente", enAttente.stream()
//                    .map(DemandeAbsenceResponseDTO::new)
//                    .collect(Collectors.toList()));

            return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard récupéré", dashboard));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/mes-demandes")
    public ResponseEntity<ApiResponse<?>> getMesDemandes() {
        try {
            User currentUser = userService.getCurrentUser();
            Long employeId = currentUser.getEmploye().getId();

            List<DemandeAbsence> demandes = workflowService.getMesDemandes(employeId);
            List<DemandeAbsenceResponseDTO> responses = demandes.stream()
                    .map(DemandeAbsenceResponseDTO::new)
                    .toList();

            return ResponseEntity.ok(new ApiResponse<>(true, "Liste récupérée avec succès", responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{companyId}/demandes-absences")
    public ResponseEntity<ApiResponse<?>> getDemandesByCompany(@PathVariable Long companyId) {
        List<DemandeAbsence> demandes = workflowService.getDemandesByCompany(companyId);
        List<DemandeAbsenceResponseDTO> responses = demandes.stream()
                .map(DemandeAbsenceResponseDTO::new)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, "Liste récupérée avec succès", responses));
    }

    @PostMapping("/{demandeId}/annuler")
    public ResponseEntity<ApiResponse<DemandeAbsence>> annulerDemande(@PathVariable Long demandeId) {
        try {
            DemandeAbsence demandeAnnulee = workflowService.annulerDemande(demandeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Demande annulée avec succès", demandeAnnulee));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ========================================
    // CALCUL PRÉVISIONNEL DES JOURS
    // ========================================

    @Operation(
            summary = "Calculer les jours d'absence prévisionnels",
            description = "Calcule les jours calendaires, fériés exclus et ouvrés décomptés avant création."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Calcul effectué"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @GetMapping("/calculer-jours")
    public ResponseEntity<ApiResponse<?>> calculerJoursAbsence(
            @RequestParam Long employeId,
            @RequestParam LocalDate dateDebut,
            @RequestParam LocalDate dateFin,
            @RequestParam UniteAbsence unite,
            @RequestParam(required = false) String heureDebut,
            @RequestParam(required = false) String heureFin,
            @RequestParam(required = false, defaultValue = "1") Integer demiJourneesParJour) {

        try {
            Employe employe = employeRepository.findById(employeId)
                    .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

            if (dateFin.isBefore(dateDebut)) {
                throw new RuntimeException("La date de fin doit être après la date de début");
            }

            Long companyId = employe.getCompany() != null ? employe.getCompany().getId() : null;
            if (companyId == null) {
                throw new RuntimeException("Employé non rattaché à une entreprise");
            }

            // Récupérer les jours fériés
            int anneeDebut = dateDebut.getYear();
            int anneeFin = dateFin.getYear();
            Set<LocalDate> datesFeries = new HashSet<>();

            for (int annee = anneeDebut; annee <= anneeFin; annee++) {
                List<JourFerie> joursFeriesAnnee = jourFerieEntrepriseService.getJoursFeriesEffectifs(companyId, annee);
                datesFeries.addAll(
                        joursFeriesAnnee.stream()
                                .map(JourFerie::getDateFerie)
                                .filter(date -> !date.isBefore(dateDebut) && !date.isAfter(dateFin))
                                .collect(Collectors.toSet())
                );
            }

            Map<String, Object> data = new HashMap<>();
            data.put("dateDebut", dateDebut.toString());
            data.put("dateFin", dateFin.toString());
            data.put("unite", unite.name());

            switch (unite) {
                case JOURNEE_ENTIERE -> {
                    CalculJoursAbsenceService.CalculJoursAbsenceResultat calcul =
                            calculJoursAbsenceService.calculerJoursAbsenceReels(companyId, dateDebut, dateFin);

                    data.put("joursCalendaires", calcul.getJoursCalendaires());
                    data.put("joursFeriesExclus", calcul.getJoursFeriesTravailles());
                    data.put("joursOuvres", calcul.getJoursAbsenceReels());
                }

                case DEMI_JOURNEE -> {
                    if (demiJourneesParJour == null || demiJourneesParJour < 1 || demiJourneesParJour > 2) {
                        throw new RuntimeException("demiJourneesParJour doit être 1 ou 2");
                    }

                    long nbJoursFeries = datesFeries.size();
                    long nbJoursCalendairesTotal = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
                    long nbJoursCalendairesTravailles = nbJoursCalendairesTotal - nbJoursFeries;

                    double joursOuvresParJour = demiJourneesParJour * 0.5;
                    double totalJoursOuvres = nbJoursCalendairesTravailles * joursOuvresParJour;

                    data.put("demiJourneesParJour", demiJourneesParJour);
                    data.put("joursCalendairesTotal", (int) nbJoursCalendairesTotal);
                    data.put("joursFeriesExclus", (int) nbJoursFeries);
                    data.put("joursCalendairesTravailles", (int) nbJoursCalendairesTravailles);
                    data.put("joursOuvres", arrondir(totalJoursOuvres, 2));
                    data.put("totalHeures", (int) (totalJoursOuvres * 8));
                }

                case HEURE -> {
                    if (heureDebut == null || heureFin == null) {
                        throw new RuntimeException("heureDebut et heureFin obligatoires");
                    }

                    LocalTime debut = LocalTime.parse(heureDebut);
                    LocalTime fin = LocalTime.parse(heureFin);

                    if (!fin.isAfter(debut)) {
                        throw new RuntimeException("L'heure de fin doit être après l'heure de début");
                    }

                    long heuresParJour = ChronoUnit.HOURS.between(debut, fin);
                    if (heuresParJour <= 0) {
                        throw new RuntimeException("La durée doit être d'au moins 1 heure");
                    }

                    long nbJoursFeries = datesFeries.size();
                    long nbJoursCalendairesTotal = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
                    long nbJoursCalendairesTravailles = nbJoursCalendairesTotal - nbJoursFeries;

                    long totalHeures = nbJoursCalendairesTravailles * heuresParJour;
                    double totalJoursOuvres = totalHeures / 8.0;

                    data.put("heureDebut", heureDebut);
                    data.put("heureFin", heureFin);
                    data.put("heuresParJour", (int) heuresParJour);
                    data.put("joursCalendairesTotal", (int) nbJoursCalendairesTotal);
                    data.put("joursFeriesExclus", (int) nbJoursFeries);
                    data.put("joursCalendairesTravailles", (int) nbJoursCalendairesTravailles);
                    data.put("totalHeures", (int) totalHeures);
                    data.put("joursOuvresEquivalent", arrondir(totalJoursOuvres, 2));
                }
            }

            data.put("joursFeriesListe", datesFeries.stream()
                    .sorted()
                    .map(LocalDate::toString)
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(new ApiResponse<>(true, "Calcul effectué avec succès", data));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    private double arrondir(double valeur, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(valeur * factor) / factor;
    }
}