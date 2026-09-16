package com.tpc.tpcgestpaie.localapp.controller.traitementSalaire;

import com.tpc.tpcgestpaie.localapp.dto.TraitementSalaire.ApercuSalaireDTO;
import com.tpc.tpcgestpaie.localapp.dto.TraitementSalaireLogDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.BulletinPaieDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.TraitementSalaireLog;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.DepartementService;
import com.tpc.tpcgestpaie.localapp.service.TraitementSalaireService;
import com.tpc.tpcgestpaie.localapp.service.paie.bulletin.BulletinPaieService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/traitement-salaire")
public class TraitementSalaireController {

    private final CompanyService companyService;
    private final TraitementSalaireService traitementSalaireService;
    private final BulletinPaieService bulletinPaieService;
    private final DepartementService departementService;

    public TraitementSalaireController(CompanyService companyService, TraitementSalaireService traitementSalaireService, BulletinPaieService bulletinPaieService, DepartementService departementService) {
        this.companyService = companyService;
        this.traitementSalaireService = traitementSalaireService;
        this.bulletinPaieService = bulletinPaieService;
        this.departementService = departementService;
    }

    @Transactional
    @PostMapping("/apercu-avant")
    public ResponseEntity<?> apercuAvant(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("Aperçu avant traitement de paie");
            Long companyId = Long.parseLong(requestBody.get("companyId").toString());
            String mois = requestBody.get("mois").toString();
            Long departement = requestBody.get("departement") != null
                    ? Long.parseLong(requestBody.get("departement").toString())
                    : null;

            Optional<Company> companyOpt = companyService.findById(companyId);
            if (companyOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                                "Entreprise introuvable"
                                , "NOT_FOUND"
                                , List.of("Aucune entreprise avec l’ID " + companyId)
                        ));
            }

            if (companyOpt.get().getRss() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                                "Rss de l'entreprise pas renseigné"
                                , "NOT_FOUND"
                                , List.of("Aucune entreprise avec l’ID " + companyId)
                        ));
            }

            if (!bulletinPaieService.getByMoisAndCompanyAndStatus(mois, companyId, Status.VALIDATED).isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(
                                "Le mois traité et validé",
                                "TRAITEMENT_VALIDE",
                                List.of("Le mois est déjà traité et validé")));
            }

            List<ApercuSalaireDTO> apercuSalaireDTO = traitementSalaireService.apercuAvant(companyId, mois, departement);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Aperçu avant traitement de paie"
                    , apercuSalaireDTO
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(
                            "Erreur lors du traitement de la paie"
                            , "VALIDATION_ERROR"
                            , List.of(e.getMessage())
                    ));
        }

    }


    @PostMapping("/calculer-salaire")
    public ResponseEntity<?> calculeSalaire(@RequestBody Map<String, Object> requestBody) {
        try {
            Long companyId = Long.parseLong(requestBody.get("companyId").toString());
            String mois = requestBody.get("mois").toString();
            Long departementId = requestBody.get("departement") != null
                    ? Long.parseLong(requestBody.get("departement").toString())
                    : null;

            Optional<Company> companyOpt = companyService.findById(companyId);
            if (companyOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                                "Entreprise introuvable",
                                "NOT_FOUND",
                                List.of("Aucune entreprise avec l'ID " + companyId)
                        ));
            }

            Company company = companyOpt.get();

            // VÉRIFICATION DE LA COMPLÉTUDE DES INFORMATIONS DE L'ENTREPRISE
            if (!company.arePayrollParametersComplete()) {
                List<String> missingParams = getMissingPayrollParameters(company);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(
                                "Paramètres de paie incomplets",
                                "INCOMPLETE_PARAMETERS",
                                List.of("Certains paramètres de paie obligatoires ne sont pas renseignés : " + String.join(", ", missingParams))
                        ));
            }

            // Vérification de l'existence du département si spécifié
            if (departementId != null) {
                boolean departementExists = departementService.existsByIdAndCompanyId(departementId, companyId);
                if (!departementExists) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(
                                    "Département introuvable",
                                    "NOT_FOUND",
                                    List.of("Aucun département avec l'ID " + departementId + " pour cette entreprise")
                            ));
                }
            }

            // Vérification si le mois est déjà traité
            if (!bulletinPaieService.getByMoisAndCompanyAndStatus(mois, companyId, Status.VALIDATED).isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Le mois est déjà traité", "VALIDATION_ERROR", List.of("Le mois est déjà traité")));
            } else {
                bulletinPaieService.deleteOldBulletinsPendingByCompanyAndMoisAndStatus(companyId, mois, Status.PENDING);
            }

            // Traitement de paie
            traitementSalaireService.calculerSalaires(companyId, mois, departementId);

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Traitement de paie effectué avec succès",
                    Map.of(
                            "entreprise", company.getName(),
                            "mois", mois,
                            "departement", departementId != null ?
                                    departementService.findById(departementId).get().getLibelle() : "Tous les départements"
                    )
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(
                            "Erreur lors du traitement de la paie",
                            "VALIDATION_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }

    /**
     * Récupère la liste des paramètres de paie manquants
     */
    private List<String> getMissingPayrollParameters(Company company) {
        List<String> missingParams = new ArrayList<>();

        if (company.getRss() == null) missingParams.add("RSS");
        if (company.getVps() == 0.0) missingParams.add("VPS");
        if (company.getNbrJourTravail() == null) missingParams.add("Nombre de jours de travail");
        if (company.getNbrJourConge() == null) missingParams.add("Nombre de jours de congé");
        if (company.getHeuresParJour() == null) missingParams.add("Heures par jour");
        if (company.getHeuresParSemaine() == null) missingParams.add("Heures par semaine");

        return missingParams;
    }

//    @PostMapping("/calculer-salaire")
//    public ResponseEntity<?> calculeSalaire(@RequestBody Map<String, Object> requestBody) {
//        try {
//            Long companyId = Long.parseLong(requestBody.get("companyId").toString());
//            String mois = requestBody.get("mois").toString();
//            Long departementId = requestBody.get("departement") != null
//                    ? Long.parseLong(requestBody.get("departement").toString())
//                    : null;
//
//            Optional<Company> companyOpt = companyService.findById(companyId);
//            if (companyOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(ApiResponse.error(
//                                "Entreprise introuvable",
//                                "NOT_FOUND",
//                                List.of("Aucune entreprise avec l'ID " + companyId)
//                        ));
//            }
//
//            // Vérification de l'existence du département si spécifié
//            if (departementId != null) {
//                boolean departementExists = departementService.existsByIdAndCompanyId(departementId, companyId);
//                if (!departementExists) {
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                            .body(ApiResponse.error(
//                                    "Département introuvable",
//                                    "NOT_FOUND",
//                                    List.of("Aucun département avec l'ID " + departementId + " pour cette entreprise")
//                            ));
//                }
//            }
//
//            // Vérification si le mois est déjà traité
//            if (!bulletinPaieService.getByMoisAndCompanyAndStatus(mois, companyId, Status.VALIDATED).isEmpty()){
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(ApiResponse.error("Le mois est déjà traité", "VALIDATION_ERROR", List.of("Le mois est déjà traité")));
//            } else {
//                bulletinPaieService.deleteOldBulletinsPendingByCompanyAndMoisAndStatus(companyId, mois, Status.PENDING);
//            }
//
//            // Traitement de paie
//            traitementSalaireService.calculerSalaires(companyId, mois, departementId);
//
//            return ResponseEntity.ok(new ApiResponse<>(
//                    true,
//                    "Traitement de paie effectué avec succès",
//                    Map.of(
//                            "entreprise", companyOpt.get().getName(),
//                            "mois", mois,
//                            "departement", departementId != null ?
//                                    departementService.findById(departementId).get().getLibelle() : "Tous les départements"
//                    )
//            ));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(ApiResponse.error(
//                            "Erreur lors du traitement de la paie",
//                            "VALIDATION_ERROR",
//                            List.of(e.getMessage())
//                    ));
//        }
//    }
//


    @PostMapping("/apercu-apres")
    public ResponseEntity<?> apercuApres(@RequestBody Map<String, Object> requestBody) {
        try {
          //  System.out.println("Appel de l'aperçu après traitement de la paie");

            // Extraction et validation des paramètres
            if (!requestBody.containsKey("companyId") || !requestBody.containsKey("mois")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(
                                "Paramètres requis manquants",
                                "MISSING_PARAMETERS",
                                List.of("Les paramètres 'companyId' et 'mois' sont requis")
                        ));
            }

            Long companyId;
            String mois;
            try {
                companyId = Long.parseLong(requestBody.get("companyId").toString());
                mois = requestBody.get("mois").toString();
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(
                                "Format des paramètres incorrect",
                                "INVALID_PARAMETERS",
                                List.of("Le champ 'companyId' doit être un nombre valide")
                        ));
            }

            Long departement = null;
            if (requestBody.get("departement") != null) {
                try {
                    departement = Long.parseLong(requestBody.get("departement").toString());
                } catch (NumberFormatException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.error(
                                    "Format du paramètre 'departement' incorrect",
                                    "INVALID_DEPARTMENT_ID",
                                    List.of("Le champ 'departement' doit être un nombre valide")
                            ));
                }
            }

            // Vérification de l'existence de l'entreprise
            Optional<Company> companyOpt = companyService.findById(companyId);
            if (companyOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                                "Entreprise introuvable",
                                "COMPANY_NOT_FOUND",
                                List.of("Aucune entreprise trouvée avec l’ID " + companyId)
                        ));
            }

            // Traitement
            List<BulletinPaieDTO> bulletinPaieDTOS = traitementSalaireService.apercuApres(companyId, mois, departement);

            if (bulletinPaieDTOS.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(
                                "Ce mois n'a pas encore été traité",
                                "NO_TREATMENT_FOUND",
                                List.of("Aucun bulletin de paie trouvé pour l’entreprise et le mois fournis")
                        ));
            }

            // Réponse en cas de succès
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Aperçu généré avec succès après traitement de la paie",
                    bulletinPaieDTOS
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            "Erreur inattendue lors de la génération de l’aperçu",
                            "UNEXPECTED_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }


    @PostMapping("/log-taraitement")
    public ResponseEntity<?> logTraitement(@RequestBody Map<String, Object> requestBody) {
        try {
            Long companyId = Long.parseLong(requestBody.get("companyId").toString());


            Optional<Company> companyOpt = companyService.findById(companyId);
            if (companyOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                                "Entreprise introuvable",
                                "NOT_FOUND",
                                List.of("Aucune entreprise trouvée avec l’ID " + companyId)
                        ));
            }

            List<TraitementSalaireLogDTO> log = traitementSalaireService.getLogByCompanyId(companyId);
            if (log == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                                "Traitement introuvable",
                                "NOT_FOUND",
                                List.of("Aucun traitement trouvée pour l'entreprise " + companyOpt.get().getName())
                        ));
            }

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Traitement de paie effectué avec succès",
                    log
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(
                            "Erreur lors du traitement de la paie",
                            "VALIDATION_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }

    @PostMapping("/validated")
    public ResponseEntity<?> validatedLog(@RequestBody Map<String, Object> requestBody) {
        try {
                Long logId = Long.parseLong(requestBody.get("logId").toString());
                Long companyId = Long.parseLong(requestBody.get("companyId").toString());
                String mois = requestBody.get("mois").toString();
                Long departement = requestBody.get("departement") != null
                        ? Long.parseLong(requestBody.get("departement").toString())
                        : null;

                Optional<Company> companyOpt = companyService.findById(companyId);
                if (companyOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(
                                    "Entreprise introuvable",
                                    "NOT_FOUND",
                                    List.of("Aucune entreprise trouvée avec l’ID " + companyId)
                            ));
                }

                TraitementSalaireLog log = traitementSalaireService.vadationTraitement(logId, null, companyId, mois);
                return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        "Traitement validé avec succès",
                        log
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(
                            "Erreur lors du traitement de la paie",
                            "VALIDATION_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }

    @PostMapping("/reset-log")
    public ResponseEntity<?> restaurerLog(@RequestBody Map<String, Object> requestBody) {
        try {
            Long logId = Long.parseLong(requestBody.get("logId").toString());
            Long companyId = Long.parseLong(requestBody.get("companyId").toString());
            String mois = requestBody.get("mois").toString();
            Long departement = requestBody.get("departement") != null
                    ? Long.parseLong(requestBody.get("departement").toString())
                    : null;

            Optional<Company> companyOpt = companyService.findById(companyId);
            if (companyOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                                "Entreprise introuvable",
                                "NOT_FOUND",
                                List.of("Aucune entreprise trouvée avec l’ID " + companyId)
                        ));
            }

            TraitementSalaireLog log = traitementSalaireService.restaurerTraitement(logId, null, companyId, mois);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Traitement restauré avec succès",
                    log
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(
                            "Erreur lors de la restauration du traitement de la paie",
                            "VALIDATION_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }

}
