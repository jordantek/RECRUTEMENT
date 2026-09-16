package com.tpc.tpcgestpaie.localapp.controller.numerisation;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import com.tpc.tpcgestpaie.localapp.service.numerisation.DocumentEmailService;
import com.tpc.tpcgestpaie.localapp.service.numerisation.EmployeeDocumentService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentEmailController {

    private final DocumentEmailService documentEmailService;
    private final EmployeeDocumentService employeeDocumentService;

    public DocumentEmailController(DocumentEmailService documentEmailService,
                                   EmployeeDocumentService employeeDocumentService) {
        this.documentEmailService = documentEmailService;
        this.employeeDocumentService = employeeDocumentService;
    }

    /**
     * Envoyer un document spécifique par email
     */
    @PostMapping("/{documentId}/send-email")
    public ResponseEntity<ApiResponse<?>> sendDocumentByEmail(
            @PathVariable Long documentId,
            @RequestBody SendDocumentRequest request) {

        try {
            EmployeeDocument document = employeeDocumentService.getDocumentById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document non trouvé"));

            Employe employe = document.getEmploye();

            String message = request.getMessage() != null ? request.getMessage() :
                    "Ce document fait partie de votre dossier personnel et peut être requis pour différents processus administratifs.";

            String subject = request.getSubject() != null ? request.getSubject() : "Document administratif";

            String toEmail = request.getToEmail() != null ? request.getToEmail() : employe.getEmail();

            documentEmailService.sendDocumentByEmail(
                    documentId,
                    toEmail,
                    subject,
                    message
            );

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("documentId", documentId);
            responseData.put("documentName", document.getFileName());
            responseData.put("sentTo", toEmail);
            responseData.put("employeeName", employe.getPrenom() + " " + employe.getNom());
            responseData.put("employeeId", employe.getId());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Document envoyé avec succès", responseData));

        } catch (MessagingException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de l'envoi de l'email: " + e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    /**
     * Envoyer par sous-catégorie
     */

    /**
     * Envoyer par entreprise ET sous-catégorie
     */
    @PostMapping("/company/{companyId}/subcategory/{subCategoryId}/send-emails")
    public ResponseEntity<ApiResponse<?>> sendDocumentsByCompanyAndSubCategory(
            @PathVariable Long companyId,
            @PathVariable Long subCategoryId,
            @RequestBody(required = false) BulkSendRequest request) {

        try {
            String subject = (request != null && request.getSubject() != null) ?
                    request.getSubject() : "Document administratif";

            String message = (request != null && request.getMessage() != null) ?
                    request.getMessage() : "Ce document fait partie de votre dossier personnel et peut être requis pour différents processus administratifs.";

            documentEmailService.sendDocumentsByCompanyAndSubCategory(
                    companyId, subCategoryId, subject, message);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("companyId", companyId);
            responseData.put("subCategoryId", subCategoryId);
            responseData.put("subject", subject);
            responseData.put("message", message);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Documents envoyés pour l'entreprise et sous-catégorie spécifiées", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de l'envoi des documents: " + e.getMessage(), null));
        }
    }

    /**
     * Statistiques d'envoi par entreprise
     */
    @GetMapping("/company/{companyId}/stats")
    public ResponseEntity<ApiResponse<?>> getEmailStatsByCompany(@PathVariable Long companyId) {
        try {
            // Implémentez cette méthode dans votre service si nécessaire
            Map<String, Object> stats = new HashMap<>();
            stats.put("companyId", companyId);
            stats.put("totalDocumentsSent", 0); // À implémenter
            stats.put("lastSentDate", null); // À implémenter

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Statistiques d'envoi récupérées avec succès", stats));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des statistiques: " + e.getMessage(), null));
        }
    }
}

// DTO pour l'envoi simple
class SendDocumentRequest {
    private String toEmail;
    private String subject;
    private String message;

    // getters et setters
    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

// DTO pour l'envoi en masse
class BulkSendRequest {
    private String subject;
    private String message;

    // getters et setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}