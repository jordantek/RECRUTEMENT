package com.tpc.tpcgestpaie.localapp.controller.contrat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.StatutContrat;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.StatutContratRepository;
import com.tpc.tpcgestpaie.localapp.service.bulletun.ContratCdiService;
import com.tpc.tpcgestpaie.localapp.service.contrat.ContratHistoriqueService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/contrat-historique")
public class ContratHistoriqueController {

    private static final Logger log = LoggerFactory.getLogger(ContratHistoriqueController.class);


    private final ContratHistoriqueService contratHistoriqueService;

    private final ContratCdiService contratService;
    private final ContratEmployeRepository contratEmployeRepository;
    private final StatutContratRepository statutContratRepository;

    public ContratHistoriqueController(ContratHistoriqueService contratHistoriqueService, ContratCdiService contratService, ContratEmployeRepository contratEmployeRepository, StatutContratRepository statutContratRepository) {
        this.contratHistoriqueService = contratHistoriqueService;
        this.contratService = contratService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.statutContratRepository = statutContratRepository;
    }

    @GetMapping("/{employeId}/pdf")
    public ResponseEntity<?> downloadContratPdfByDate(
            @PathVariable Long employeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReference) {

        try {
            // Si pas de date référence, utiliser la date actuelle
            if (dateReference == null) {
                dateReference = LocalDate.now();
            }

//            log.info("📥 Demande PDF pour employé ID: {}, date: {}", employeId, dateReference);

            // Récupérer le contrat à la date spécifiée
            ContratEmploye contrat = contratHistoriqueService.getContratByDate(employeId, dateReference);
//            log.info("✅ Contrat récupéré ID: {}", contrat.getId());

            // Vérifications de sécurité
            if (contrat.getCompany() == null) {
                throw new RuntimeException("L'entreprise du contrat est manquante");
            }
            if (contrat.getEmploye() == null) {
                throw new RuntimeException("L'employé du contrat est manquant");
            }
            if (contrat.getNatureContrat() == null || contrat.getNatureContrat().getLibelle() == null) {
                throw new RuntimeException("La nature du contrat est manquante");
            }

            // Génération du PDF (votre code existant)
            byte[] pdfBytes;
            String filename;

            String nature = contrat.getNatureContrat().getLibelle().toUpperCase();
            String type = (contrat.getType_contrat() != null) ? contrat.getType_contrat().toUpperCase() : "";

            switch (nature) {
                case "TRAVAIL":
                    if ("CDI".equals(type)) {
                        pdfBytes = contratService.generatePdf(contrat);
                        filename = "Contrat_CDI";
                    } else if ("CDD".equals(type)) {
                        pdfBytes = contratService.generatePdfCdd(contrat);
                        filename = "Contrat_CDD";
                    } else {
                        throw new RuntimeException("Type de contrat TRAVAIL inconnu : " + type);
                    }
                    break;

                default:
                    throw new RuntimeException("Nature de contrat non supportée : " + nature);
            }

            // Nom du fichier
            filename = String.format("%s_%s_%s_%s.pdf",
                    filename,
                    contrat.getEmploye().getNom().replaceAll("[^a-zA-Z0-9]", "_"),
                    contrat.getEmploye().getPrenom().replaceAll("[^a-zA-Z0-9]", "_"),
                    dateReference.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
//            log.error("❌ Erreur de validation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Erreur de validation: " + e.getMessage(), null)
            );

        } catch (Exception e) {
//            log.error("❌ Erreur lors de la génération du PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur serveur: " + e.getMessage(), null)
            );
        }
    }

    @GetMapping("/{employeId}/test")
    public ResponseEntity<ApiResponse<String>> testContratHistorique(
            @PathVariable Long employeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReference) {

        try {
            if (dateReference == null) {
                dateReference = LocalDate.now();
            }

            ContratEmploye contrat = contratHistoriqueService.getContratByDate(employeId, dateReference);

            String message = String.format("✅ Contrat trouvé - ID: %d, Employé: %s %s, Type: %s",
                    contrat.getId(),
                    contrat.getEmploye() != null ? contrat.getEmploye().getNom() : "N/A",
                    contrat.getEmploye() != null ? contrat.getEmploye().getPrenom() : "N/A",
                    contrat.getType_contrat()
            );

            return ResponseEntity.ok(new ApiResponse<>(true, message, null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "❌ Erreur: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{employeId}/debug")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugContratHistorique(
            @PathVariable Long employeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReference) {

        try {
            if (dateReference == null) {
                dateReference = LocalDate.now();
            }

            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("employeId", employeId);
            debugInfo.put("dateReference", dateReference);

            // 1. Vérifier si l'employé existe
            Optional<ContratEmploye> contratActuel = contratEmployeRepository.findByEmployeIdWithAllRelations(employeId);
            debugInfo.put("employeExiste", contratActuel.isPresent());

            if (contratActuel.isPresent()) {
                debugInfo.put("contratActuelId", contratActuel.get().getId());
                debugInfo.put("contratActuelType", contratActuel.get().getType_contrat());
            }

            // 2. Chercher les statuts
            Optional<StatutContrat> statut = statutContratRepository.findStatutByEmployeAndDate(employeId, dateReference);
            debugInfo.put("statutTrouve", statut.isPresent());

            if (statut.isPresent()) {
                StatutContrat s = statut.get();
                debugInfo.put("statutId", s.getId());
                debugInfo.put("statutDateEffet", s.getDateEffet());
                debugInfo.put("snapshotDisponible", s.getSnapshotJson() != null);
                debugInfo.put("snapshotLongueur", s.getSnapshotJson() != null ? s.getSnapshotJson().length() : 0);

                if (s.getSnapshotJson() != null) {
                    // Test de désérialisation
                    try {
                        ObjectMapper testMapper = new ObjectMapper();
                        testMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        JsonNode jsonNode = testMapper.readTree(s.getSnapshotJson());
                        debugInfo.put("snapshotId", jsonNode.has("id") ? jsonNode.get("id").asLong() : null);
                        debugInfo.put("snapshotTypeContrat", jsonNode.has("type_contrat") ? jsonNode.get("type_contrat").asText() : null);
                    } catch (Exception e) {
                        debugInfo.put("erreurDeserialisation", e.getMessage());
                    }
                }
            }

            // 3. Appeler la méthode normale
            ContratEmploye contrat = contratHistoriqueService.getContratByDate(employeId, dateReference);
            debugInfo.put("contratResultatId", contrat.getId());
            debugInfo.put("contratResultatType", contrat.getType_contrat());
            debugInfo.put("contratResultatEmployeNom", contrat.getEmploye() != null ? contrat.getEmploye().getNom() : "null");

            return ResponseEntity.ok(new ApiResponse<>(true, "Debug info", debugInfo));

        } catch (Exception e) {
            log.error("❌ Erreur debug", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur debug: " + e.getMessage(), null));
        }
    }
}