package com.tpc.tpcgestpaie.localapp.controller.numerisation;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentSubCategory;
import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import com.tpc.tpcgestpaie.localapp.service.numerisation.SecureQRService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller de test pour déboguer la génération et validation de QR Codes
 */
@RestController
@RequestMapping("/api/qr-test")
@Slf4j
public class QRTestController {

    private final SecureQRService secureQRService;

    public QRTestController(SecureQRService secureQRService) {
        this.secureQRService = secureQRService;
    }

    /**
     * Génère un QR Code de test avec des données fictives COMPLÈTES
     *
     * Test avec: GET http://localhost:8080/api/qr-test/generate-sample
     */
    @GetMapping("/generate-sample")
    public ResponseEntity<ApiResponse<?>> generateSampleQR() {
        log.info("🧪 Génération QR Code de test complet...");

        try {
            // Créer un document fictif pour test avec TOUTES les données
            EmployeeDocument testDoc = createCompleteTestDocument();

            // Générer les données QR sécurisées
            String qrData = secureQRService.generateSecureQRData(testDoc);

            // Générer l'image QR
            String qrImage = secureQRService.generateQRImage(qrData);

            Map<String, Object> response = new HashMap<>();
            response.put("qrData", qrData);
            response.put("qrImage", qrImage);
            response.put("documentId", testDoc.getId());
            response.put("reference", testDoc.getReference());
            response.put("employeeName", testDoc.getEmploye().getNom() + " " + testDoc.getEmploye().getPrenom());
            response.put("employeeMatricule", testDoc.getEmploye().getMatricule());
            response.put("companyName", testDoc.getEmploye().getCompany().getName());
            response.put("categoryName", testDoc.getSubCategory().getName());
            response.put("fileHash", testDoc.getFileHash());
            response.put("dataLength", qrData.length());

            // Informations de débogage détaillées
            Map<String, Object> debug = new HashMap<>();
            debug.put("format", qrData.substring(0, Math.min(30, qrData.length())) + "...");
            debug.put("totalLength", qrData.length());
            debug.put("encryptedPartLength", qrData.length() - 6); // Moins "TPCv1:"
            debug.put("timestamp", System.currentTimeMillis());
            debug.put("signedBy", testDoc.getSignedBy());

            response.put("debug", debug);

            log.info("✅ QR Code de test complet généré - Longueur: {}", qrData.length());

            return ResponseEntity.ok(ApiResponse.success("QR Code de test généré", response));

        } catch (Exception e) {
            log.error("❌ Erreur génération QR test: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur: " + e.getMessage()));
        }
    }

    /**
     * Valide un QR Code de test (pour vérifier le cycle complet)
     *
     * Test avec: POST http://localhost:8080/api/qr-test/validate-sample
     * Body: { "qrData": "TPCv1:..." }
     */
    @PostMapping("/validate-sample")
    public ResponseEntity<ApiResponse<?>> validateSampleQR(@RequestBody Map<String, String> request) {
        String qrData = request.get("qrData");

        log.info("🧪 Validation QR Code de test...");
        log.debug("📝 QR Data reçu (50 premiers caractères): {}",
                qrData.substring(0, Math.min(50, qrData.length())));

        try {
            // Valider et déchiffrer
            Map<String, Object> result = secureQRService.validateAndDecryptQR(qrData);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("documentId", result.get("DOC"));
            response.put("reference", result.get("REF"));
            response.put("employeeName", result.get("EMP"));
            response.put("employeeMatricule", result.get("EMPMAT"));
            response.put("companyName", result.get("COMP"));
            response.put("companyPhone", result.get("PHONE"));
            response.put("companyAddress", result.get("ADDRESS"));
            response.put("categoryName", result.get("CAT"));
            response.put("dateNumerisation", result.get("DATENUM"));
            response.put("signataire", result.get("SIGNATAIRE"));
            response.put("fileHash", result.get("HASH"));
            response.put("timestamp", result.get("TS"));
            response.put("signature", result.get("SIG"));

            log.info("✅ QR Code de test validé avec succès");
            log.debug("📊 Données extraites - Doc: {}, Emp: {}, Sig: {}",
                    result.get("DOC"), result.get("EMPMAT"), result.get("SIG"));

            return ResponseEntity.ok(ApiResponse.success("QR Code valide", response));

        } catch (SecurityException e) {
            log.warn("⚠️ QR Code invalide: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("QR Code invalide: " + e.getMessage()));

        } catch (Exception e) {
            log.error("❌ Erreur validation QR test: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur: " + e.getMessage()));
        }
    }

    /**
     * Test du cycle complet: génération + validation
     *
     * Test avec: GET http://localhost:8080/api/qr-test/full-cycle
     */
    @GetMapping("/full-cycle")
    public ResponseEntity<ApiResponse<?>> testFullCycle() {
        log.info("🧪 Test cycle complet génération + validation...");

        try {
            // 1. Générer
            EmployeeDocument testDoc = createCompleteTestDocument();
            String qrData = secureQRService.generateSecureQRData(testDoc);
            log.info("✅ Étape 1: QR généré - {}", qrData.substring(0, 30) + "...");

            // 2. Valider
            Map<String, Object> validationResult = secureQRService.validateAndDecryptQR(qrData);
            log.info("✅ Étape 2: QR validé - Doc ID: {}, Matricule: {}",
                    validationResult.get("DOC"), validationResult.get("EMPMAT"));

            // 3. Vérifier la cohérence de TOUTES les données
            boolean docIdConsistent = testDoc.getId().toString().equals(validationResult.get("DOC"));
            boolean matriculeConsistent = testDoc.getEmploye().getMatricule().equals(validationResult.get("EMPMAT"));
            boolean refConsistent = testDoc.getReference().equals(validationResult.get("REF"));

            boolean allConsistent = docIdConsistent && matriculeConsistent && refConsistent;

            Map<String, Object> response = new HashMap<>();
            response.put("success", allConsistent);

            // Informations de génération
            Map<String, Object> generated = new HashMap<>();
            generated.put("documentId", testDoc.getId());
            generated.put("reference", testDoc.getReference());
            generated.put("matricule", testDoc.getEmploye().getMatricule());
            generated.put("qrData", qrData.substring(0, 50) + "...");
            generated.put("length", qrData.length());
            response.put("generated", generated);

            // Informations de validation
            response.put("validated", validationResult);

            // Rapport de cohérence
            Map<String, Boolean> consistency = new HashMap<>();
            consistency.put("documentId", docIdConsistent);
            consistency.put("matricule", matriculeConsistent);
            consistency.put("reference", refConsistent);
            consistency.put("overall", allConsistent);
            response.put("dataConsistency", consistency);

            if (allConsistent) {
                log.info("🎉 Test cycle complet RÉUSSI - Toutes les données cohérentes!");
                return ResponseEntity.ok(ApiResponse.success("Cycle complet validé", response));
            } else {
                log.error("❌ Incohérence des données!");
                log.error("   - Doc ID: {} vs {}", testDoc.getId(), validationResult.get("DOC"));
                log.error("   - Matricule: {} vs {}", testDoc.getEmploye().getMatricule(), validationResult.get("EMPMAT"));
                log.error("   - Référence: {} vs {}", testDoc.getReference(), validationResult.get("REF"));

                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Incohérence entre génération et validation"));
            }

        } catch (Exception e) {
            log.error("❌ Erreur test cycle complet: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur: " + e.getMessage()));
        }
    }

    /**
     * Affiche le format propriétaire déchiffré (pour debug)
     *
     * Test avec: POST http://localhost:8080/api/qr-test/decode-raw
     * Body: { "qrData": "TPCv1:..." }
     */
    @PostMapping("/decode-raw")
    public ResponseEntity<ApiResponse<?>> decodeRaw(@RequestBody Map<String, String> request) {
        String qrData = request.get("qrData");

        log.info("🔍 Décodage brut du QR Code...");

        try {
            Map<String, Object> result = secureQRService.validateAndDecryptQR(qrData);

            // Construire une représentation lisible
            StringBuilder raw = new StringBuilder();
            result.forEach((key, value) -> raw.append(key).append(":").append(value).append(";\n"));

            Map<String, Object> response = new HashMap<>();
            response.put("rawFormat", raw.toString());
            response.put("parsedData", result);
            response.put("fieldCount", result.size());

            return ResponseEntity.ok(ApiResponse.success("Format décodé", response));

        } catch (Exception e) {
            log.error("❌ Erreur décodage: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur: " + e.getMessage()));
        }
    }

    /**
     * Crée un document fictif COMPLET pour les tests
     */
    private EmployeeDocument createCompleteTestDocument() {
        // Créer une entreprise fictive
        Company testCompany = new Company();
        testCompany.setId(1L);
        testCompany.setName("TPC Enterprises");
        testCompany.setPhone("+229 97 00 00 00");
        testCompany.setAddress("123 Rue de la Paie, Cotonou, Bénin");

        // Créer un employé fictif
        Employe testEmployee = new Employe();
        testEmployee.setId(999L);
        testEmployee.setNom("DUPONT");
        testEmployee.setPrenom("Jean");
        testEmployee.setMatricule("EMP-2024-999");
        testEmployee.setCompany(testCompany);

        // Créer une catégorie fictive
        DocumentSubCategory testCategory = new DocumentSubCategory();
        testCategory.setId(1L);
        testCategory.setName("Contrat de Travail");

        // Créer un document fictif COMPLET
        EmployeeDocument testDoc = new EmployeeDocument();
        testDoc.setId(12345L);
        testDoc.setReference("DOC-2024-12345");
        testDoc.setEmploye(testEmployee);
        testDoc.setSubCategory(testCategory);
        testDoc.setFileName("contrat_jean_dupont.pdf");
        testDoc.setFileHash("abcdef123456789012345678901234567890abcdef123456789012345678901234");
        testDoc.setDocumentType(EmployeeDocument.DocumentType.RECONSTRUCTED);
        testDoc.setCreatedAt(LocalDateTime.now());
        testDoc.setSignedBy("Marie MARTIN - Directrice RH");

        return testDoc;
    }
}