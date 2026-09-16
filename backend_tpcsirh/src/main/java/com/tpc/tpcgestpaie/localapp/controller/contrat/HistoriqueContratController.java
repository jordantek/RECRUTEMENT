package com.tpc.tpcgestpaie.localapp.controller.contrat;

import com.tpc.tpcgestpaie.localapp.dto.contrat.HistoriqueContratDTO;
import com.tpc.tpcgestpaie.localapp.service.contrat.HistoriqueContratService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/historique-contrat")
public class HistoriqueContratController {

    private final HistoriqueContratService historiqueContratService;

    public HistoriqueContratController(HistoriqueContratService historiqueContratService) {
        this.historiqueContratService = historiqueContratService;
    }

    @GetMapping("/employe/{employeId}")
    public ResponseEntity<ApiResponse<List<HistoriqueContratDTO>>> getHistoriqueByEmploye(
            @PathVariable Long employeId) {
        try {
            List<HistoriqueContratDTO> historique = historiqueContratService.getHistoriqueByEmployeId(employeId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Historique récupéré avec succès", historique)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération de l'historique", null));
        }
    }

    @GetMapping("/contrat/{contratEmployeId}")
    public ResponseEntity<ApiResponse<List<HistoriqueContratDTO>>> getHistoriqueByContrat(
            @PathVariable Long contratEmployeId) {
        try {
            List<HistoriqueContratDTO> historique = historiqueContratService
                    .getHistoriqueByContratEmployeId(contratEmployeId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Historique récupéré avec succès", historique)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération de l'historique", null));
        }
    }

    // UTILISEZ la méthode du service corrigée
    @GetMapping("/{statutId}/differences")
    public ResponseEntity<ApiResponse<?>> getDifferences(@PathVariable Long statutId) {
        try {
            Map<String, Object> differences = historiqueContratService.getDifferences(statutId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Différences récupérées avec succès", differences)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Erreur de validation", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur", e.getMessage()));
        }
    }

    // Endpoint pour télécharger la preuve (reste identique)
    @GetMapping("/preuve/{filename}")
    public ResponseEntity<Resource> downloadPreuve(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/preuves").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

//@RestController
//@RequestMapping("/api/historique-contrat")
//public class HistoriqueContratController {
//
//
//    private final HistoriqueContratService historiqueContratService;
//    private final StatutContratRepository statutContratRepository;
//    private final ObjectMapper objectMapper;
//
//    public HistoriqueContratController(HistoriqueContratService historiqueContratService, StatutContratRepository statutContratRepository, ObjectMapper objectMapper) {
//        this.historiqueContratService = historiqueContratService;
//        this.statutContratRepository = statutContratRepository;
//        this.objectMapper = objectMapper;
//    }
//
//    @GetMapping("/employe/{employeId}")
//    public ResponseEntity<ApiResponse<List<HistoriqueContratDTO>>> getHistoriqueByEmploye(
//            @PathVariable Long employeId) {
//        try {
//            List<HistoriqueContratDTO> historique = historiqueContratService.getHistoriqueByEmployeId(employeId);
//
//            return ResponseEntity.ok(
//                    new ApiResponse<>(true, "Historique récupéré avec succès", historique)
//            );
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>(false, "Erreur lors de la récupération de l'historique", null));
//        }
//    }
//
//    @GetMapping("/contrat/{contratEmployeId}")
//    public ResponseEntity<ApiResponse<List<HistoriqueContratDTO>>> getHistoriqueByContrat(
//            @PathVariable Long contratEmployeId) {
//        try {
//            List<HistoriqueContratDTO> historique = historiqueContratService
//                    .getHistoriqueByContratEmployeId(contratEmployeId);
//
//            return ResponseEntity.ok(
//                    new ApiResponse<>(true, "Historique récupéré avec succès", historique)
//            );
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>(false, "Erreur lors de la récupération de l'historique", null));
//        }
//    }
//
//    // Endpoint pour télécharger la preuve
//    @GetMapping("/preuve/{filename}")
//    public ResponseEntity<Resource> downloadPreuve(@PathVariable String filename) {
//        try {
//            Path filePath = Paths.get("uploads/preuves").resolve(filename).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (resource.exists() && resource.isReadable()) {
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION,
//                                "attachment; filename=\"" + resource.getFilename() + "\"")
//                        .body(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @GetMapping("/{statutId}/differences")
//    public ResponseEntity<?> getDifferences(@PathVariable Long statutId) {
//        try {
//            StatutContrat statut = statutContratRepository.findById(statutId)
//                    .orElseThrow(() -> new IllegalArgumentException("Statut introuvable"));
//
//            // Désérialiser le snapshot
//            ContratEmploye ancienContrat = objectMapper.readValue(
//                    statut.getSnapshotJson(),
//                    ContratEmploye.class
//            );
//
//            // Contrat actuel
//            ContratEmploye nouveauContrat = statut.getContratEmploye();
//
//            Map<String, Object> differences = new HashMap<>();
//
//            // Comparer chaque champ
//            differences.put("typeContrat", Map.of(
//                    "ancien", ancienContrat.getType_contrat(),
//                    "nouveau", nouveauContrat.getType_contrat(),
//                    "modifie", !Objects.equals(ancienContrat.getType_contrat(), nouveauContrat.getType_contrat())
//            ));
//
//            differences.put("salaireBase", Map.of(
//                    "ancien", ancienContrat.getSalaire_base(),
//                    "nouveau", nouveauContrat.getSalaire_base(),
//                    "modifie", !Objects.equals(ancienContrat.getSalaire_base(), nouveauContrat.getSalaire_base())
//            ));
//
//            // Ajouter d'autres champs...
//
//            return ResponseEntity.ok(
//                    new ApiResponse<>(true, "Différences récupérées", differences)
//            );
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, "Erreur", e.getMessage()));
//        }
//    }
//}