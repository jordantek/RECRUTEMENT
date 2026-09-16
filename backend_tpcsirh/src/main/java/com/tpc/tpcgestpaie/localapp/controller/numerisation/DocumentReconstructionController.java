package com.tpc.tpcgestpaie.localapp.controller.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.ProcessusReconstructionDTO;
import com.tpc.tpcgestpaie.localapp.service.numerisation.DocumentReconstructionService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document-reconstruction")
@Slf4j
public class DocumentReconstructionController {

    private final DocumentReconstructionService reconstructionService;

    public DocumentReconstructionController(DocumentReconstructionService reconstructionService) {
        this.reconstructionService = reconstructionService;
    }

    /**
     * Lancer la reconstruction d'un document
     */
    @PostMapping("/document/{documentId}/reconstruct")
    public ResponseEntity<?> launchReconstruction(
            @PathVariable Long documentId,
            @RequestParam Long modeleId) {
        try {
            ProcessusReconstructionDTO processus =
                    reconstructionService.launchDocumentReconstruction(documentId, modeleId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Reconstruction lancée avec succès", processus));
        } catch (Exception e) {
            log.error("Erreur lors du lancement de la reconstruction du document {}", documentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors du lancement de la reconstruction", null));
        }
    }

    /**
     * Statut d'un processus de reconstruction
     */
    @GetMapping("/process/{processId}")
    public ResponseEntity<?> getProcessStatus(@PathVariable Long processId) {
        try {
            ProcessusReconstructionDTO processus = reconstructionService.getProcessStatus(processId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Statut du processus récupéré", processus));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du statut du processus {}", processId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Processus non trouvé", null));
        }
    }

    /**
     * Liste des processus pour un employé
     */
    @GetMapping("/employee/{employeId}/processes")
    public ResponseEntity<?> getEmployeeProcesses(@PathVariable Long employeId) {
        try {
            List<ProcessusReconstructionDTO> processus =
                    reconstructionService.getEmployeeProcesses(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Processus de l'employé récupérés", processus));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des processus de l'employé {}", employeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des processus", null));
        }
    }

    /**
     * Annuler un processus de reconstruction
     */
    @PostMapping("/process/{processId}/cancel")
    public ResponseEntity<?> cancelProcess(@PathVariable Long processId) {
        try {
            reconstructionService.cancelProcess(processId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Processus annulé avec succès", null));
        } catch (Exception e) {
            log.error("Erreur lors de l'annulation du processus {}", processId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de l'annulation du processus", null));
        }
    }

    /**
     * Relancer un processus en erreur
     */
    @PostMapping("/process/{processId}/retry")
    public ResponseEntity<?> retryProcess(@PathVariable Long processId) {
        try {
            ProcessusReconstructionDTO processus = reconstructionService.retryProcess(processId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Processus relancé avec succès", processus));
        } catch (Exception e) {
            log.error("Erreur lors de la relance du processus {}", processId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la relance du processus", null));
        }
    }
}