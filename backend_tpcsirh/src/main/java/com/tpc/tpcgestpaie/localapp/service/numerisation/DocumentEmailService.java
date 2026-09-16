package com.tpc.tpcgestpaie.localapp.service.numerisation;


import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.EmployeeDocumentRepository;
import com.tpc.tpcgestpaie.localapp.service.emailConfig.EmailConfigService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentEmailService {

    private final EmailConfigService emailConfigService;
    private final EmployeeDocumentService employeeDocumentService;
    private final EmployeeDocumentRepository employeeDocumentRepository;

    public DocumentEmailService(EmailConfigService emailConfigService,
                                EmployeeDocumentService employeeDocumentService, EmployeeDocumentRepository employeeDocumentRepository) {
        this.emailConfigService = emailConfigService;
        this.employeeDocumentService = employeeDocumentService;
        this.employeeDocumentRepository = employeeDocumentRepository;
    }

    /**
     * Envoyer un document spécifique par email
     */

    // Dans DocumentEmailService
    @Transactional
    public void sendDocumentByEmail(Long documentId, String toEmail, String subject, String customMessage)
            throws MessagingException, IOException {

        EmployeeDocument document = employeeDocumentService.getDocumentById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        Employe employe = document.getEmploye();
        Company company = employe.getCompany();
        Resource documentResource = employeeDocumentService.downloadDocument(documentId);

        // Préparer les variables pour le template
        Map<String, Object> variables = new HashMap<>();
        variables.put("companyName", company.getName());
        variables.put("hrEmail", company.getEmail());
        variables.put("logoPath", company.getLogo());

        // Informations document
        variables.put("documentId", document.getId());
        variables.put("documentName", document.getFileName());
        variables.put("documentType", document.getSubCategory().getName());
        variables.put("uploadDate", document.getCreatedAt());
        variables.put("fileSize", formatFileSize(document.getFileSize()));

        // Informations employé
        variables.put("employeeName", employe.getPrenom() + " " + employe.getNom());
        variables.put("employeeMatricule", employe.getMatricule());
        variables.put("customMessage", customMessage);

        // QR Code
        variables.put("hasQRCode", document.getQrCode() != null);
        if (document.getQrCode() != null) {
            variables.put("qrCodePath", "/chemin/vers/qrcodes/" + document.getQrCode().getDocument());
        }

        // Envoyer l'email avec le template document-email
        emailConfigService.sendEmailWithTemplate(
                toEmail,
                subject != null ? subject : "Document partagé - " + document.getFileName(),
                variables,
                documentResource.getFile().getAbsolutePath(),
                "email/document-email"  // Nom du template
        );
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " bytes";
        else if (size < 1024 * 1024) return (size / 1024) + " KB";
        else return String.format("%.2f MB", size / (1024.0 * 1024.0));
    }

    /**
     * ENVOYER PAR ENTREPRISE ET SOUS-CATÉGORIE - Combinaison des deux
     */
    @Transactional
    public void sendDocumentsByCompanyAndSubCategory(Long companyId, Long subCategoryId, String subject, String customMessage)
            throws MessagingException, IOException {

        // Récupérer tous les documents de l'entreprise pour cette sous-catégorie
        List<EmployeeDocument> documents = employeeDocumentRepository.findByCompanyIdAndSubCategoryId(companyId, subCategoryId);

        for (EmployeeDocument document : documents) {
            Employe employe = document.getEmploye();

            try {
                sendDocumentByEmail(
                        document.getId(),
                        employe.getEmail(),
                        subject,
                        customMessage
                );
//                log.info("Document envoyé à {}: {}", employe.getEmail(), document.getFileName());
            } catch (Exception e) {
//                log.error("Erreur envoi document {} à {}: {}",
//                        document.getId(), employe.getEmail(), e.getMessage());
            }
        }
    }


    /**
     * Envoyer plusieurs documents par email
     */
    public void sendMultipleDocumentsByEmail(String toEmail, String subject,
                                             Map<String, Object> variables,
                                             String attachmentDirectoryPath)
            throws MessagingException {

        emailConfigService.sendEmail(
                toEmail,
                subject,
                variables,
                attachmentDirectoryPath
        );
    }

    /**
     * Envoyer un document avec template spécifique
     */
    public void sendDocumentWithTemplate(Long documentId, String toEmail, String subject,
                                         String templateName, Map<String, Object> variables)
            throws MessagingException, IOException {

        EmployeeDocument document = employeeDocumentService.getDocumentById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        Resource documentResource = employeeDocumentService.downloadDocument(documentId);

        // Ajouter les informations du document aux variables
        variables.put("document", document);
        variables.put("employee", document.getEmploye());
        variables.put("documentCategory", document.getSubCategory().getName());

        // Utiliser un template spécifique pour les documents
        // Note: Vous devrez étendre EmailConfigService pour supporter différents templates
        emailConfigService.sendEmail(
                toEmail,
                subject,
                variables,
                documentResource.getFile().getAbsolutePath()
        );
    }
}