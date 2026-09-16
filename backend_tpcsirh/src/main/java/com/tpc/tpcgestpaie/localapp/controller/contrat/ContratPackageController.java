package com.tpc.tpcgestpaie.localapp.controller.contrat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratConsolideDTO;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.StatutContrat;
import com.tpc.tpcgestpaie.localapp.repository.StatutContratRepository;
import com.tpc.tpcgestpaie.localapp.service.bulletun.ContratCdiService;
import com.tpc.tpcgestpaie.localapp.service.contrat.ContratConsolidationService;
import com.tpc.tpcgestpaie.localapp.service.contrat.ContratPackageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contrat-package")
public class ContratPackageController {

    private final ContratPackageService contratPackageService;
    private final StatutContratRepository statutContratRepository;
    private final ContratConsolidationService contratConsolidationService;
    private final ContratCdiService contratService;

    public ContratPackageController(ContratPackageService contratPackageService, StatutContratRepository statutContratRepository, ContratConsolidationService contratConsolidationService, ContratCdiService contratService) {
        this.contratPackageService = contratPackageService;
        this.statutContratRepository = statutContratRepository;
        this.contratConsolidationService = contratConsolidationService;
        this.contratService = contratService;
    }

    @GetMapping("/{employeId}/zip")
    public ResponseEntity<byte[]> downloadContratPackage(
            @PathVariable Long employeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReference) {

        try {
            if (dateReference == null) {
                dateReference = LocalDate.now();
            }

            byte[] zipBytes = contratPackageService.createContratPackage(employeId, dateReference);

            String filename = String.format("package_contrat_%s_%s.zip",
                    employeId, dateReference.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(org.springframework.http.ContentDisposition.attachment()
                    .filename(filename)
                    .build());
            headers.setContentLength(zipBytes.length);

            return new ResponseEntity<>(zipBytes, headers, org.springframework.http.HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint de test simple pour debugger
     */
    @GetMapping("/{employeId}/test")
    public ResponseEntity<Map<String, Object>> testConsolidation(
            @PathVariable Long employeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReference) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (dateReference == null) {
                dateReference = LocalDate.now();
            }

//            log.info("🧪 TEST - Début du test pour employé {} à la date {}", employeId, dateReference);

            // Test 1: Vérifier si l'employé existe
            response.put("employeId", employeId);
            response.put("dateReference", dateReference);

            // Test 2: Vérifier les statuts d'initialisation
            var statutInitial = statutContratRepository.findStatutInitialisationByEmployeId(employeId);
            response.put("statutInitialTrouve", statutInitial.isPresent());

            if (statutInitial.isPresent()) {
                response.put("statutInitialId", statutInitial.get().getId());
                response.put("statutInitialDate", statutInitial.get().getDateEffet());
                response.put("statutInitialSnapshotLength",
                        statutInitial.get().getSnapshotJson() != null ? statutInitial.get().getSnapshotJson().length() : 0);
            }

            // Test 3: Essayer la consolidation
            try {
                ContratConsolideDTO result = contratConsolidationService.getContratConsolide(employeId, dateReference);
                response.put("consolidationReussie", true);
                response.put("contratInitialId", result.getContratInitial().getId());
                response.put("contratFinalId", result.getContratFinal().getId());
                response.put("nombreModifications", result.getModifications().size());
            } catch (Exception e) {
                response.put("consolidationReussie", false);
                response.put("erreurConsolidation", e.getMessage());
            }

            response.put("success", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
//            log.error("❌ Erreur lors du test", e);
            response.put("success", false);
            response.put("erreur", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{employeId}/debug-snapshot")
    public ResponseEntity<Map<String, Object>> debugSnapshot(
            @PathVariable Long employeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReference) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (dateReference == null) {
                dateReference = LocalDate.now();
            }

            // Trouver le statut d'initialisation
            var statutInitial = statutContratRepository.findStatutInitialisationByEmployeId(employeId);

            if (!statutInitial.isPresent()) {
                response.put("erreur", "Aucun statut initial trouvé");
                return ResponseEntity.badRequest().body(response);
            }

            StatutContrat statut = statutInitial.get();
            response.put("statutId", statut.getId());
            response.put("snapshotLength", statut.getSnapshotJson().length());

            // Analyser la structure du JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(statut.getSnapshotJson());

            // Lister tous les champs
            List<String> champs = new ArrayList<>();
            rootNode.fieldNames().forEachRemaining(champs::add);
            response.put("champs", champs);

            // Afficher les valeurs importantes
            Map<String, Object> valeurs = new HashMap<>();
            for (String champ : champs) {
                if (champ.equals("id") || champ.contains("contrat") || champ.contains("salaire") || champ.contains("date")) {
                    valeurs.put(champ, rootNode.get(champ));
                }
            }
            response.put("valeursImportantes", valeurs);

            // Essayer de désérialiser
            try {
                ContratEmploye contrat = mapper.readValue(statut.getSnapshotJson(), ContratEmploye.class);
                response.put("deserialisationStandardId", contrat.getId());
            } catch (Exception e) {
                response.put("deserialisationStandardErreur", e.getMessage());
            }

            response.put("success", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("erreur", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/{employeId}/test-pdf-only")
    public ResponseEntity<byte[]> testPdfSeul(
            @PathVariable Long employeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReference) {

        try {
            if (dateReference == null) {
                dateReference = LocalDate.now();
            }

//            log.info("🧪 TEST PDF seul pour employé {}", employeId);

            // 1. Obtenir le contrat consolidé
            ContratConsolideDTO contratConsolide = contratConsolidationService.getContratConsolide(employeId, dateReference);

            // 2. Générer seulement le PDF
            byte[] pdfBytes = contratService.generatePdf(contratConsolide.getContratFinal());

            String filename = "test_contrat.pdf";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
//            log.error("❌ Erreur test PDF", e);

            // Retourner l'erreur en JSON pour debug
            Map<String, String> errorResponse = Map.of(
                    "error", "Erreur génération PDF",
                    "message", e.getMessage(),
                    "employeId", employeId.toString(),
                    "dateReference", dateReference.toString()
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.toString().getBytes());
        }
    }

//
//    @GetMapping("/{employeId}/diagnostic-preuves")
//    public ResponseEntity<Map<String, Object>> diagnosticPreuves(
//            @PathVariable Long employeId,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReference) {
//
//        Map<String, Object> diagnostic = new HashMap<>();
//
//        try {
//            if (dateReference == null) {
//                dateReference = LocalDate.now();
//            }
//
//            // 1. Obtenir les modifications
//            ContratConsolideDTO contratConsolide = contratConsolidationService.getContratConsolide(employeId, dateReference);
//            diagnostic.put("nombreModifications", contratConsolide.getModifications().size());
//
//            // 2. Utiliser la méthode du service pour vérifier les preuves
//            List<Map<String, Object>> detailsPreuves = contratPackageService.verifierPreuves(contratConsolide.getModifications());
//            diagnostic.put("detailsPreuves", detailsPreuves);
//
//            // 3. Utiliser la méthode du service pour lister les fichiers
//            diagnostic.put("fichiersDisponibles", contratPackageService.listPreuvesDisponibles());
//
//            diagnostic.put("success", true);
//            return ResponseEntity.ok(diagnostic);
//
//        } catch (Exception e) {
//            diagnostic.put("success", false);
//            diagnostic.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(diagnostic);
//        }
//    }
//
//    @GetMapping("/diagnostic-upload")
//    public ResponseEntity<Map<String, Object>> diagnosticUpload() {
//        return ResponseEntity.ok(contratPackageService.diagnosticUploadDir());
//    }

    @GetMapping("/{employeId}/debug-modifications")
    public ResponseEntity<Map<String, Object>> debugModifications(
            @PathVariable Long employeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReference) {

        Map<String, Object> debug = new HashMap<>();

        try {
            LocalDate dateRef = dateReference != null ? dateReference : LocalDate.now();
            if (dateReference == null) {
                dateReference = LocalDate.now();
            }

            debug.put("employeId", employeId);
            debug.put("dateReference", dateReference);

            // 1. Statut initial
            Optional<StatutContrat> statutInitial = statutContratRepository.findStatutInitialisationByEmployeId(employeId);
            debug.put("statutInitialTrouve", statutInitial.isPresent());

            if (statutInitial.isPresent()) {
                debug.put("statutInitialId", statutInitial.get().getId());
                debug.put("statutInitialDate", statutInitial.get().getDateEffet());
                debug.put("statutInitialInitialisation", statutInitial.get().isInitialisation());
            }

            // 2. ✅ CORRIGÉ : Tous les statuts avec la bonne méthode
            List<StatutContrat> tousStatuts = statutContratRepository.findByEmployeIdOrderByDateEffetDesc(employeId);
            debug.put("totalStatuts", tousStatuts.size());

            // 3. Détail des statuts
            List<Map<String, Object>> detailsStatuts = tousStatuts.stream().map(statut -> {
                Map<String, Object> detail = new HashMap<>();
                detail.put("id", statut.getId());
                detail.put("initialisation", statut.isInitialisation());
                detail.put("dateEffet", statut.getDateEffet());
                detail.put("typeModification", statut.getTypeModification());
                detail.put("motif", statut.getMotif());
                detail.put("preuve", statut.getPreuve());
                detail.put("contratEmployeId", statut.getContratEmploye() != null ? statut.getContratEmploye().getId() : null);
                return detail;
            }).collect(Collectors.toList());
            debug.put("detailsStatuts", detailsStatuts);

            // 4. Modifications avec méthode simple
            List<StatutContrat> modificationsSimple = statutContratRepository.findModificationsSimple(employeId, dateReference);
            debug.put("modificationsSimpleCount", modificationsSimple.size());

            // 5. Modifications avec filtrage manuel
            // Modifications avec filtrage manuel
            List<StatutContrat> modificationsManuelles = tousStatuts.stream()
                    .filter(statut -> !statut.isInitialisation())
                    .filter(statut -> !statut.getDateEffet().isAfter(dateRef)) // ✅ Utiliser la variable locale
                    .collect(Collectors.toList());

            debug.put("modificationsManuellesCount", modificationsManuelles.size());

            debug.put("success", true);
            return ResponseEntity.ok(debug);

        } catch (Exception e) {
            debug.put("success", false);
            debug.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(debug);
        }
    }


}