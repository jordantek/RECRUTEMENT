package com.tpc.tpcgestpaie.localapp.controller.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.QRCodeVerificationDTO;
import com.tpc.tpcgestpaie.localapp.service.numerisation.QRCodeGenerationService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/document-qrcodes")
@Slf4j
public class DocumentQRCodeController {

    private final QRCodeGenerationService qrCodeService;

    public DocumentQRCodeController(QRCodeGenerationService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    /**
     * Vérification d'un QR Code
     */
    @GetMapping("/verify/{digitalId}")
    public ResponseEntity<?> verifyQRCode(@PathVariable String digitalId) {
        try {
            QRCodeVerificationDTO verification = qrCodeService.verifyQRCode(digitalId);
            return ResponseEntity.ok(new ApiResponse<>(true, "QR Code vérifié avec succès", verification));
        } catch (Exception e) {
            log.error("Erreur lors de la vérification du QR Code {}", digitalId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la vérification du QR Code: " + e.getMessage(), null));
        }
    }

    /**
     * Statut détaillé d'un QR Code
     */
    @GetMapping("/status/{digitalId}")
    public ResponseEntity<?> getQRCodeStatus(@PathVariable String digitalId) {
        try {
            QRCodeVerificationDTO verification = qrCodeService.verifyQRCode(digitalId);

            Map<String, Object> status = new HashMap<>();
            status.put("digitalId", verification.getDigitalId()); // ✅ CORRIGÉ: digitalId au lieu de uniqueCode
            status.put("documentValid", verification.isValid());
            status.put("integrityValid", verification.isIntegrityValid());
            status.put("fullyValid", verification.isIntegrityValid() && verification.isValid());
            status.put("employeeFullName", verification.getEmployeeFullName());
            status.put("employeeMatricule", verification.getEmployeeMatricule());
            status.put("documentName", verification.getDocumentName());
            status.put("documentType", verification.getDocumentType()); // ✅ NOUVEAU CHAMP
            status.put("generationDate", verification.getGenerationDate());
            // ✅ SUPPRIMÉ: expirationDate n'existe plus

            return ResponseEntity.ok(new ApiResponse<>(true, "Statut du QR Code récupéré avec succès", status));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du statut du QR Code {}", digitalId, e);

            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("digitalId", digitalId); // ✅ CORRIGÉ
            errorStatus.put("valid", false);
            errorStatus.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "QR Code non trouvé ou invalide: " + e.getMessage(), errorStatus));
        }
    }

    /**
     * Générer un nouveau QR Code pour un document
     */
    @PostMapping("/document/{documentId}/generate")
    public ResponseEntity<?> generateQRCodeForDocument(@PathVariable Long documentId) {
        try {
            qrCodeService.generateQRCodeForDocument(documentId);
            return ResponseEntity.ok(new ApiResponse<>(true, "QR Code généré avec succès", null));
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR Code pour le document {}", documentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la génération du QR Code: " + e.getMessage(), null));
        }
    }

    /**
     * Récupérer l'image du QR Code
     */
    @GetMapping("/{digitalId}/image")
    public ResponseEntity<?> getQRCodeImage(@PathVariable String digitalId) {
        try {
            byte[] qrCodeImage = qrCodeService.getQRCodeImage(digitalId);

            if (qrCodeImage != null && qrCodeImage.length > 0) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(qrCodeImage);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Image du QR Code non trouvée", null));
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'image du QR Code {}", digitalId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération de l'image du QR Code: " + e.getMessage(), null));
        }
    }

    /**
     * Récupérer les informations complètes d'un QR Code
     */
    @GetMapping("/{digitalId}/info")
    public ResponseEntity<?> getQRCodeInfo(@PathVariable String digitalId) {
        try {
            QRCodeVerificationDTO verification = qrCodeService.verifyQRCode(digitalId);

            Map<String, Object> info = new HashMap<>();
            info.put("digitalId", verification.getDigitalId());
            info.put("documentName", verification.getDocumentName());
            info.put("employeeFullName", verification.getEmployeeFullName());
            info.put("employeeMatricule", verification.getEmployeeMatricule());
            info.put("documentType", verification.getDocumentType());
            info.put("generationDate", verification.getGenerationDate());
            info.put("valid", verification.isValid());
            info.put("integrityValid", verification.isIntegrityValid());

            return ResponseEntity.ok(new ApiResponse<>(true, "Informations QR Code récupérées avec succès", info));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des informations du QR Code {}", digitalId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "QR Code non trouvé: " + e.getMessage(), null));
        }
    }
}