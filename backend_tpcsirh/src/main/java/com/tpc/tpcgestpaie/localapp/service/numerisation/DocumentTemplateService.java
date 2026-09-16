package com.tpc.tpcgestpaie.localapp.service.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentSubCategory;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentTemplate;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.DocumentSubCategoryRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.DocumentTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class DocumentTemplateService {

    private final DocumentTemplateRepository templateRepository;
    private final DocumentSubCategoryRepository subCategoryRepository;
    private final String UPLOAD_DIR = "uploads/templates/";

    public DocumentTemplateService(DocumentTemplateRepository templateRepository,
                                   DocumentSubCategoryRepository subCategoryRepository) {
        this.templateRepository = templateRepository;
        this.subCategoryRepository = subCategoryRepository;

        // Créer le répertoire d'upload s'il n'existe pas
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            log.error("Erreur lors de la création du répertoire d'upload", e);
        }
    }

    /**
     * Créer un template avec upload de fichier
     */
    public DocumentTemplate createTemplateWithFile(MultipartFile file, String templateName,
                                                   Long subCategoryId, String description,
                                                   Integer qrPage, Float qrPositionX, Float qrPositionY,
                                                   Float qrWidth, Float qrHeight) throws IOException {

        // Vérifier la sous-catégorie
        DocumentSubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new RuntimeException("Sous-catégorie non trouvée: " + subCategoryId));

        // Générer un nom de fichier unique
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        String filePath = UPLOAD_DIR + uniqueFileName;

        // Sauvegarder le fichier
        Path path = Paths.get(filePath);
        Files.copy(file.getInputStream(), path);

        // Créer le template
        DocumentTemplate template = new DocumentTemplate();
        template.setTemplateName(templateName);
        template.setTemplatePath(filePath);
        template.setSubCategory(subCategory);
        template.setIsActive(true);
        template.setQrPage(qrPage != null ? qrPage : 1);
        template.setQrPositionX(qrPositionX);
        template.setQrPositionY(qrPositionY);
        template.setQrWidth(qrWidth != null ? qrWidth : 80f);
        template.setQrHeight(qrHeight != null ? qrHeight : 80f);
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());

        return templateRepository.save(template);
    }

    public DocumentTemplate createTemplate(DocumentTemplate template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        return templateRepository.save(template);
    }

    public DocumentTemplate updateTemplate(DocumentTemplate template) {
        template.setUpdatedAt(LocalDateTime.now());
        return templateRepository.save(template);
    }

    public DocumentTemplate toggleTemplate(Long templateId) {
        DocumentTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template non trouvé"));
        template.setIsActive(!template.getIsActive());
        template.setUpdatedAt(LocalDateTime.now());
        return templateRepository.save(template);
    }

    public List<DocumentTemplate> getTemplatesBySubCategory(Long subCategoryId) {
        return templateRepository.findBySubCategoryIdAndIsActiveTrue(subCategoryId);
    }

    public List<DocumentTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    public DocumentTemplate getTemplateById(Long templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template non trouvé: " + templateId));
    }

    public void deleteTemplate(Long templateId) {
        DocumentTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template non trouvé: " + templateId));

        // Supprimer le fichier physique
        try {
            Files.deleteIfExists(Paths.get(template.getTemplatePath()));
        } catch (IOException e) {
            log.warn("Impossible de supprimer le fichier physique: {}", template.getTemplatePath());
        }

        templateRepository.delete(template);
    }
}